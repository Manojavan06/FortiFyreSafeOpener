package com.fortifyre.safeopener.viewer

import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.barteksc.pdfviewer.PDFView
import com.fortifyre.safeopener.R
import com.fortifyre.safeopener.utils.LogHelper

class SafeViewerActivity : AppCompatActivity() {

    private lateinit var pdfView: PDFView
    private lateinit var logTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safe_viewer)

        // Initialize views
        pdfView = findViewById(R.id.pdfView)
        logTextView = findViewById(R.id.logTextView)

        // Prevent GPU surface crash in emulator / Mesa
        pdfView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)

        // Display any previous logs
        updateLogView("SafeViewerActivity started.")

        val uri: Uri? = intent?.data
        if (uri != null) {
            try {
                updateLogView("Opening PDF: $uri")
                LogHelper.addLog("Opening PDF file: $uri")

                // Load and render PDF
                pdfView.fromUri(uri)
                    .enableSwipe(true)
                    .swipeHorizontal(false)
                    .enableDoubletap(true)
                    .defaultPage(0)
                    .enableAnnotationRendering(true)
                    .load()

                updateLogView("✅ PDF loaded successfully.")
            } catch (e: Exception) {
                val errorMsg = "❌ Failed to load PDF: ${e.message}"
                updateLogView(errorMsg)
                LogHelper.addLog(errorMsg)
            }
        } else {
            updateLogView("⚠ No file URI received in intent.")
            LogHelper.addLog("No file URI received in SafeViewerActivity")
        }
    }

    private fun updateLogView(message: String) {
        LogHelper.addLog(message)
        logTextView.text = LogHelper.getLogs().joinToString("\n")
    }
}
