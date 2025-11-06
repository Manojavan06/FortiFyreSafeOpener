package com.fortifyre.safeopener.viewer

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.fortifyre.safeopener.R
import com.fortifyre.safeopener.utils.LogHelper

class LogActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log)

        val logTextView: TextView = findViewById(R.id.logTextView)
        val closeButton: Button = findViewById(R.id.closeButton)

        logTextView.text = LogHelper.getLogs().joinToString("\n")

        closeButton.setOnClickListener {
            finish()
        }
    }
}
