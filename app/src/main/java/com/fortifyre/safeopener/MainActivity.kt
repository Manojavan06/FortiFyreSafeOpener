package com.fortifyre.safeopener

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            val result = com.fortifyre.safeopener.detector.StaticScanner.analyzeFile(this, uri)
            Log.d("FortiFyre", "Scan result: $result")
        } else {
            Log.d("FortiFyre", "No file or link received")
        }
    }

}
