package com.fortifyre.safeopener.detector

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.Handler
import android.os.Looper
import android.os.StatFs
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import java.io.File

class RuntimeMonitorService : Service() {

    private val handler = Handler(Looper.getMainLooper())
    private var lastFreeSpace = 0L
    private var lastNetworkTx = 0L
    private var lastNetworkRx = 0L

    private val monitorRunnable = object : Runnable {
        override fun run() {
            try {
                val storage = StatFs(filesDir.absolutePath)
                val freeSpace = storage.availableBytes

                // Compare storage changes
                if (lastFreeSpace != 0L && Math.abs(freeSpace - lastFreeSpace) > 10_000_000) {
                    broadcastAlert("Storage", "Large unexpected file write detected")
                }
                lastFreeSpace = freeSpace

                // Monitor simple network metrics
                val txBytes = android.net.TrafficStats.getTotalTxBytes()
                val rxBytes = android.net.TrafficStats.getTotalRxBytes()
                if (lastNetworkTx != 0L && (txBytes - lastNetworkTx > 5_000_000 || rxBytes - lastNetworkRx > 5_000_000)) {
                    broadcastAlert("Network", "Unexpected large network activity")
                }
                lastNetworkTx = txBytes
                lastNetworkRx = rxBytes

                handler.postDelayed(this, 5000) // recheck every 5 seconds
            } catch (e: Exception) {
                Log.e("FortiFyre", "Runtime monitor error: ${e.message}")
            }
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("FortiFyre", "RuntimeMonitorService started")
        handler.post(monitorRunnable)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(monitorRunnable)
        Log.d("FortiFyre", "RuntimeMonitorService stopped")
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun broadcastAlert(kind: String, message: String) {
        val alertIntent = Intent("com.fortifyre.safeopener.RUNTIME_ALERT").apply {
            putExtra("kind", kind)
            putExtra("message", message)
        }
        LocalBroadcastManager.getInstance(this).sendBroadcast(alertIntent)
        Log.w("FortiFyre", "⚠ Runtime Alert: $kind – $message")
    }
}
