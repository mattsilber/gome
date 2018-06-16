package com.guardanis

import java.net.InetAddress
import java.net.ServerSocket
import java.net.Socket

class PingManager(private val connectionPort: Int) : Thread() {

    private var serverSocket: ServerSocket? = null

    override fun run() {
        Logger.info("Starting Ping Manager on port $connectionPort")

        try {
            serverSocket = ServerSocket(connectionPort)

            attemptConnection()
        }
        catch (e: Throwable) { Logger.log(e) }

        stopServer()
    }

    private fun attemptConnection() {
        try {
            Logger.info("Ping-Server standing by for next connection on port $connectionPort")

            val socket = serverSocket!!.accept()

            Logger.info("Ping requested by ${socket.inetAddress}")

            val writer = SocketWriter(socket)
            writer.write(InetAddress.getLocalHost().hostName)
            writer.onDestroy()

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
