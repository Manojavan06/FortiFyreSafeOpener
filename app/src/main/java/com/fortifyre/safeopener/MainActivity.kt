package com.fortifyre.safeopener

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fortifyre.safeopener.utils.LogHelper
import com.google.android.material.snackbar.Snackbar
import android.graphics.Color
import com.fortifyre.safeopener.viewer.SafeViewerActivity

class MainActivity : AppCompatActivity() {
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        resultView = findViewById(R.id.scanResultText)

        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        val uri: Uri? = intent?.data
        if (uri != null) {
            val result = com.fortifyre.safeopener.detector.StaticScanner.analyzeFile(this, uri)
            LogHelper.addLog("FortiFyre: Scan result: $result")

            val root = findViewById<android.view.View>(android.R.id.content)

            if (result.contains("Suspicious", ignoreCase = true)) {
                val snackbar = Snackbar.make(
                    root,
                    "⚠ Suspicious file detected — not opened.",
                    Snackbar.LENGTH_INDEFINITE
                )
                snackbar.setAction("Dismiss") { snackbar.dismiss() }
                snackbar.setBackgroundTint(Color.parseColor("#B00020"))
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()
                LogHelper.addLog("FortiFyre: Suspicious file blocked from opening.")
            } else {
                val snackbar = Snackbar.make(
                    root,
                    "✅ File appears safe — opening in secure view.",
                    Snackbar.LENGTH_SHORT
                )
                snackbar.setBackgroundTint(Color.parseColor("#2E7D32"))
                snackbar.setTextColor(Color.WHITE)
                snackbar.show()

                LogHelper.addLog("FortiFyre: File appears safe. Launching SafeViewerActivity.")
                val viewIntent = Intent(this, com.fortifyre.safeopener.viewer.SafeViewerActivity::class.java).apply {
                    data = uri
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                startActivity(viewIntent)

            }

        } else {
            LogHelper.addLog("FortiFyre: No file or link received.")
            val root = findViewById<android.view.View>(android.R.id.content)
            val snackbar = Snackbar.make(
                root,
                "No file or link received.",
                Snackbar.LENGTH_SHORT
            )
            snackbar.setBackgroundTint(Color.DKGRAY)
            snackbar.show()
        }
    }
}
