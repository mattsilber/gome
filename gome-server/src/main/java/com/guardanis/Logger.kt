package com.guardanis

object Logger {

    private val logListeners = ArrayList<(String) -> Unit>()

    init {
        logListeners.add({ println(it) })
    }

    fun addLogListener(listener: (String) -> Unit) {
        this.logListeners.add(listener)
    }

    fun log(message: String) {
        logListeners.forEach({ it(message) })
    }

    fun info(message: String, vararg formats: String) {
        val fullMessage = String.format(message, *formats)

        logListeners.forEach({ it(fullMessage) })
    }

    fun log(e: Throwable?) {
        e?.printStackTrace()

        logListeners.forEach({ it(e?.message ?: e?.localizedMessage ?: "") })
    }
}
