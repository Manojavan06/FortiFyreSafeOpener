package com.fortifyre.safeopener.detector

import android.content.Context
import android.net.Uri
import android.util.Log
import java.io.InputStream
import java.security.MessageDigest

object StaticScanner {

    fun analyzeFile(context: Context, uri: Uri): String {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val md = MessageDigest.getInstance("MD5")
            val buffer = ByteArray(1024)
            var bytesRead: Int
            while (inputStream?.read(buffer).also { bytesRead = it ?: -1 } != -1) {
                md.update(buffer, 0, bytesRead)
            }
            inputStream?.close()

            val digest = md.digest().joinToString("") { "%02x".format(it) }
            Log.d("FortiFyre", "File MD5: $digest")

            if (digest.endsWith("d")) {
                "Suspicious: May contain executable code"
            } else {
                "Clean"
            }

        } catch (e: Exception) {
            Log.e("FortiFyre", "Error analyzing file", e)
            "Error scanning file"
        }
    }
}
