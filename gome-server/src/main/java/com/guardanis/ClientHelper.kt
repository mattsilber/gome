package com.guardanis

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.InetAddress
import java.net.Socket

import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

import com.guardanis.SocketManager.ConnectionEvents
import com.guardanis.commands.Command

class ClientHelper(
        private val socket: Socket,
        private val eventsCallback: ConnectionEvents): Thread() {

    lateinit var device: Device
        private set

    private var reader: BufferedReader? = null
    private var writer: SocketWriter? = null

    private var finishedCallback: Runnable? = null

    init {
        Logger.log("Client connected from ${socket.inetAddress.hostAddress}, preparing to read.")
    }

    fun setFinishedCallback(finishedCallback: Runnable): ClientHelper {
        this.finishedCallback = finishedCallback
        return this
    }

    override fun run() {
        try {
            this.reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            this.device = readDevice()
            this.eventsCallback.onDeviceIdentified(this, device, socket.inetAddress.hostAddress)
            this.writer = SocketWriter(socket)

            writeHostName()

            readInputStream(device)
        }
        catch (e: Throwable) { Logger.log(e) }

        eventsCallback.onClientDisconnected(this)

        finishedCallback?.run()
    }

    @Throws(Throwable::class)
    private fun readDevice(): Device {
        val reader = this.reader ?: throw RuntimeException("Reader cannot be null when requesting device data.")

        val deviceData = JSONParser()
                .parse(reader.readLine()) as? JSONObject ?: throw RuntimeException("Device response was not JSON")

        return Device(deviceData)
    }

    private fun writeHostName() {
        var hostName = "gome-server"

        try {
            hostName = InetAddress.getLocalHost().hostName
        }
        catch (e: Exception) { e.printStackTrace() }

        writer?.write(hostName)
    }

    @Throws(Throwable::class)
    private fun readInputStream(device: Device) {
        val reader = this.reader ?: throw RuntimeException("Reader is null when accessing the input stream!")
        var line: String? = reader.readLine()

        while (line != null) {
            process(device, line)

            line = reader.readLine()
        }
    }

    @Throws(Exception::class)
    private fun process(device: Device, data: String) {
        //		Logger.info("Data received: " + data);

        val delimiter = data.indexOf(":")
        val commandKey = data.substring(0, delimiter)
        val commandData = JSONParser()
                .parse(data.substring(delimiter + 1)) as JSONObject

        val command = Command(commandKey, commandData)

        device.setLastPing(System.currentTimeMillis())

        eventsCallback.onCommandReceived(this, command)
    }

    fun write(data: String) {
        writer?.write(data)
    }

    override fun destroy() {
        reader?.close()
        writer?.onDestroy()

        try {
            socket?.close()
        }
        catch (e: Exception) { }
    }
}
