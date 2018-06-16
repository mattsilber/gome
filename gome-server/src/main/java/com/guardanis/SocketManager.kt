package com.guardanis

import java.awt.EventQueue.invokeLater
import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

import javax.net.ssl.SSLServerSocket
import javax.net.ssl.SSLServerSocketFactory

import com.guardanis.commands.Command

class SocketManager(private val connectionPort: Int, eventsCallback: ConnectionEvents) : Thread() {

    private var serverSocket: ServerSocket? = null

    private val eventsCallback: ConnectionEvents

    interface ConnectionEvents {
        fun onAwaitingNextConnection()
        fun onConnected(ip: String, port: Int)
        fun onDeviceIdentified(client: ClientHelper, device: Device, ipAddress: String)
        fun onCommandReceived(client: ClientHelper, command: Command)
        fun onClientDisconnected(client: ClientHelper)
    }

    init {
        this.eventsCallback = object: ConnectionEvents {

            override fun onAwaitingNextConnection() {
                invokeLater({ eventsCallback.onAwaitingNextConnection() })
            }

            override fun onConnected(ip: String, port: Int) {
                invokeLater({ eventsCallback.onConnected(ip, port) })
            }

            override fun onCommandReceived(client: ClientHelper, command: Command) {
                invokeLater({ eventsCallback.onCommandReceived(client, command) })
            }

            override fun onClientDisconnected(client: ClientHelper) {
                invokeLater({ eventsCallback.onClientDisconnected(client) })
            }

            override fun onDeviceIdentified(client: ClientHelper, device: Device, ipAddress: String) {
                invokeLater({ eventsCallback.onDeviceIdentified(client, device, ipAddress) })
            }
        }
    }

    override fun run() {
        Logger.info("Starting server on port $connectionPort")

        try {
            //			SSLServerSocketFactory socketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            //
            //			serverSocket = (SSLServerSocket) socketFactory.createServerSocket(connectionPort);
            //			serverSocket.setEnabledCipherSuites(socketFactory.getSupportedCipherSuites());

            serverSocket = ServerSocket(connectionPort)

            attemptConnection()
        }
        catch (e: Throwable) { Logger.log(e) }

        stopServer()
    }

    private fun attemptConnection() {
        try {
            Logger.info("Server standing by for next connection")

            eventsCallback.onAwaitingNextConnection()

            val socket = serverSocket!!.accept()

            eventsCallback.onConnected(socket.inetAddress.hostAddress, connectionPort)

            val client = ClientHelper(socket, eventsCallback)

            Thread(client).start()

            attemptConnection()
        }
        catch (e: Exception) { e.printStackTrace() }
    }

    fun stopServer() {
        try {
            serverSocket?.close()
        }
        catch (e: Throwable) { Logger.log(e) }
    }
}
