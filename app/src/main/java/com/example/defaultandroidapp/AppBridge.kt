package com.example.defaultandroidapp

import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.JavascriptInterface
import androidx.activity.ComponentActivity
import java.io.File

/** Called from the game as window.Android.*: close the app, and save a backup file to Downloads. */
class AppBridge(private val activity: ComponentActivity) {

    @JavascriptInterface
    fun exitApp() {
        activity.runOnUiThread { activity.finishAndRemoveTask() }
    }

    @JavascriptInterface
    fun saveFile(name: String, text: String): Boolean = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val values = ContentValues().apply {
                put(MediaStore.Downloads.DISPLAY_NAME, name)
                put(MediaStore.Downloads.MIME_TYPE, "application/json")
                put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
            }
            val resolver = activity.contentResolver
            val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            if (uri == null) false else {
                resolver.openOutputStream(uri)?.use { it.write(text.toByteArray()) }
                true
            }
        } else {
            val dir = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) ?: activity.filesDir
            File(dir, name).writeText(text)
            true
        }
    } catch (e: Exception) {
        false
    }
}
