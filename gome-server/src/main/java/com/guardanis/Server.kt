package com.guardanis

import java.net.Inet4Address
import java.net.Inet6Address
import java.net.InetAddress
import java.net.NetworkInterface
import java.net.SocketException
import java.util.ArrayList
import java.util.Enumeration
import java.util.HashMap
import java.util.concurrent.CopyOnWriteArrayList

import com.guardanis.SocketManager.ConnectionEvents
import com.guardanis.commands.Command
import com.guardanis.commands.CommandController
import com.guardanis.commands.KeyboardCommandController
import com.guardanis.commands.MouseCommandController
import com.guardanis.commands.WebCommandController
import com.guardanis.display.DisplayController

class Server @Throws(Exception::class)
protected constructor(): ConnectionEvents {

    private val ipAddresses: List<String>

    private val socketManager: SocketManager
    private val pingManager: PingManager

    private val connectedDevices = CopyOnWriteArrayList<Device>()

    private val commandControllers = HashMap<String, CommandController>()

    private val displayController: DisplayController

    init {
        this.socketManager = SocketManager(Configs.CONNECTION_PORT, this)
        this.pingManager = PingManager(Configs.PING_PORT)
        this.ipAddresses = getIpAddresses()

        commandControllers[ACTION_MOUSE] = MouseCommandController()
        commandControllers[ACTION_KEYBOARD] = KeyboardCommandController()
        commandControllers[ACTION_WEBSITE] = WebCommandController()

        displayController = DisplayController(ipAddresses)
                .show(connectedDevices)

        socketManager.start()
        pingManager.start()
    }

    override fun onAwaitingNextConnection() {

    }

    override fun onConnected(ip: String, port: Int) {

    }

    override fun onDeviceIdentified(client: ClientHelper, device: Device, ipAddress: String) {
        Logger.info("%1\$s connected from %2\$s", device.getName(), ipAddress)

        device.ipAddress = ipAddress // Weird...

        connectedDevices.add(device)

        displayController.onDeviceAdded(device)
    }

    override fun onCommandReceived(client: ClientHelper, command: Command) {
        try {
            //			Logger.info("Received action [%1$s] with: %2$s",
            //					command.getAction(),
            //					command.getData().toString());

            commandControllers.takeIf({ it.containsKey(command.action) })
                    ?.let({ it[command.action] })
                    ?.process(command)
        }
        catch (e: Exception) { e.printStackTrace() }

    }

    override fun onClientDisconnected(client: ClientHelper) {
        val device = client.device ?: return

        Logger.info("%1\$s disconnected", device.getName())

        withDeviceOrForget(client, { connectedDevices.remove(it) })

        displayController.onDeviceRemoved(device)
    }

    private fun withDeviceOrForget(client: ClientHelper, callback: (Device) -> Unit) {
        for (device in connectedDevices) {
            if (device.identifier == client.device?.identifier) {
                callback.invoke(device)

                return
            }
        }
    }

    private fun getIpAddresses(): List<String> {
        val ipAddresses = ArrayList<String>()

        var en: Enumeration<*>? = null

        try {
            en = NetworkInterface.getNetworkInterfaces()
        }
        catch (e: SocketException) { e.printStackTrace() }

        if (en == null)
            return ipAddresses

        while (en.hasMoreElements()) {
            val i = en.nextElement() as NetworkInterface
            val en2 = i.inetAddresses

            while (en2.hasMoreElements()) {
                en2.nextElement()
                        .takeIf({ !it.isLoopbackAddress && it is Inet4Address })
                        ?.run({ ipAddresses.add(this.getHostAddress()) })
            }
        }

        return ipAddresses
    }

    companion object {

        val ACTION_MOUSE = "mouse"
        val ACTION_KEYBOARD = "key"
        val ACTION_WEBSITE = "web"

        @JvmStatic
        fun main(args: Array<String>) {
            try {
                Server()
            }
            catch (e: Exception) {
                e.printStackTrace()

                throw RuntimeException("Unable to start the Server!")
            }
        }
    }
}
