package com.guardanis

import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStreamWriter
import java.net.Socket

class SocketWriter @Throws(IOException::class)
constructor(socket: Socket) {

    private val writer: BufferedWriter?

    init {
        writer = BufferedWriter(OutputStreamWriter(socket.getOutputStream()))
    }

    fun write(data: String) {
        try {
            writer?.write(data)
            writer?.newLine()
            writer?.flush()
        }
        catch (e: Exception) { Logger.log(e) }
    }

    fun onDestroy() {
        try {
            writer?.close()
        }
        catch (e: Exception) { }
    }
}
