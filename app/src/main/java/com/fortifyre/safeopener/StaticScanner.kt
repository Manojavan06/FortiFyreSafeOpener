
package com.fortifyre.safeopener.detector

import android.net.Uri
import android.content.Context
import android.util.Log
import java.io.InputStream

object StaticScanner {

    fun analyzeFile(context: Context, uri: Uri): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val content = inputStream?.bufferedReader()?.use { it.readText() } ?: ""
            inputStream?.close()

            when {
                content.contains("<script>", ignoreCase = true) -> "Suspicious: Embedded script"
                content.contains("dex") -> "Suspicious: May contain executable code"
                content.contains("base64") -> "Moderate: Encoded content detected"
                else -> "Clean: No malicious patterns found"
            }
        } catch (e: Exception) {
            Log.e("FortiFyre", "Error scanning file", e)
            "Error scanning file"
        }
    }
}
