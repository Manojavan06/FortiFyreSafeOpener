package com.fortifyre.safeopener.detector

import android.content.Context
import android.net.TrafficStats
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import android.content.Intent

object RuntimeMonitor {
    private val handler = Handler(Looper.getMainLooper())
    private var lastTx: Long = 0L
    private var monitoring = false

    private val check = object : Runnable {
        override fun run() {
            try {
                val heap = Debug.getNativeHeapAllocatedSize()
                val uid = android.os.Process.myUid()
                val tx = TrafficStats.getUidTxBytes(uid)
                val rx = TrafficStats.getUidRxBytes(uid)
                val txDelta = if (lastTx == 0L) 0L else tx - lastTx
                lastTx = tx

                Log.d("RuntimeMonitor", "heap=$heap txDelta=$txDelta rx=$rx")
                // Simple heuristics:
                // - large memory spike
                // - outgoing bytes while viewer opened
                if (heap > 80_000_000L) {
                    emitAlert("MEM_SPIKE", "Memory spike detected: $heap")
                }
                if (txDelta > 50_000L) { // >50 KB since last tick
                    emitAlert("NETWORK_ACTIVITY", "Network activity detected: $txDelta bytes")
                }
            } catch (e: Exception) {
                Log.w("RuntimeMonitor", "monitor error", e)
            } finally {
                if (monitoring) handler.postDelayed(this, 2000)
            }
        }
    }

    private fun emitAlert(kind: String, message: String) {
        Log.w("RuntimeMonitor", "$kind : $message")
        // Broadcast locally so UI can show a Snackbar/Alert
        val intent = Intent("com.fortifyre.safeopener.RUNTIME_ALERT")
        intent.putExtra("kind", kind)
        intent.putExtra("message", message)
        // Application context required from caller
        // We'll send it later via LocalBroadcastManager; stash last context or pass context in startMonitoring
        lastContext?.let {
            LocalBroadcastManager.getInstance(it).sendBroadcast(intent)
        }
    }

    private var lastContext: Context? = null

    fun startMonitoring(context: Context) {
        if (monitoring) return
        lastContext = context.applicationContext
        monitoring = true
        lastTx = TrafficStats.getUidTxBytes(android.os.Process.myUid())
        handler.postDelayed(check, 2000)
    }

    fun stopMonitoring() {
        monitoring = false
        handler.removeCallbacks(check)
        lastContext = null
    }
}
