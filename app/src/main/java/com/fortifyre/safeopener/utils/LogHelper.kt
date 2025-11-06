package com.fortifyre.safeopener.utils

object LogHelper {
    private val logs = mutableListOf<String>()

    fun addLog(message: String) {
        logs.add("[${System.currentTimeMillis()}] $message")
    }

    fun getLogs(): List<String> {
        return logs
    }
}
