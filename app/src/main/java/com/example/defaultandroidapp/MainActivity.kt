package com.example.defaultandroidapp

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsets
import android.view.WindowInsetsController
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts

class MainActivity : ComponentActivity() {

    private lateinit var webView: WebView
    private lateinit var ttsBridge: TtsBridge

    private var filePathCallback: ValueCallback<Array<Uri>>? = null

    private val filePickerLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->

            val callback = filePathCallback
            filePathCallback = null

            if (result.resultCode == Activity.RESULT_OK) {
                val data = result.data
                val selectedUris = mutableListOf<Uri>()

                data?.clipData?.let { clipData ->
                    for (i in 0 until clipData.itemCount) {
                        selectedUris.add(clipData.getItemAt(i).uri)
                    }
                }

                data?.data?.let { uri ->
                    if (selectedUris.isEmpty()) {
                        selectedUris.add(uri)
                    }
                }

                callback?.onReceiveValue(
                    if (selectedUris.isNotEmpty()) {
                        selectedUris.toTypedArray()
                    } else {
                        null
                    }
                )
            } else {
                callback?.onReceiveValue(null)
            }
        }

    @SuppressLint("SetJavaScriptEnabled", "JavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        webView = WebView(this)

        webView.settings.apply {
            javaScriptEnabled = true
            domStorageEnabled = true
            allowFileAccess = true
            allowContentAccess = true
            mediaPlaybackRequiresUserGesture = false
        }

        // Native text-to-speech bridge for the web app.
        ttsBridge = TtsBridge(this)
        webView.addJavascriptInterface(ttsBridge, "AndroidTTS")

        // NEW: lets the game close the app ("Leave game") and save backup files to Downloads.
        webView.addJavascriptInterface(AppBridge(this), "Android")

        webView.webViewClient = WebViewClient()

        webView.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                view: WebView?,
                callback: ValueCallback<Array<Uri>>?,
                params: FileChooserParams?
            ): Boolean {
                // Cancel any older file-selection request.
                filePathCallback?.onReceiveValue(null)
                filePathCallback = callback

                // CHANGED: the fallback accepts any file, so a save backup (.json) can be picked too.
                val intent = try {
                    params?.createIntent()
                        ?: Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "*/*"
                        }
                } catch (exception: Exception) {
                    Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                        addCategory(Intent.CATEGORY_OPENABLE)
                        type = "*/*"
                    }
                }

                intent.addCategory(Intent.CATEGORY_OPENABLE)
                filePickerLauncher.launch(intent)

                return true
            }
        }

        // CHANGED: only load the page when there is no saved state to restore.
        val restored = savedInstanceState != null && webView.restoreState(savedInstanceState) != null
        if (!restored) {
            webView.loadUrl("https://metur100.github.io/Starfall.Grove/")
        }

        setContentView(webView)
        hideSystemUI()

        onBackPressedDispatcher.addCallback(
            this,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finish()
                    }
                }
            }
        )
    }

    @Suppress("DEPRECATION")
    private fun hideSystemUI() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)

            window.insetsController?.let { controller ->
                controller.hide(WindowInsets.Type.systemBars())
                controller.systemBarsBehavior =
                    WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        } else {
            window.decorView.systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                            or View.SYSTEM_UI_FLAG_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    )
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)

        // Re-hide the bars if they appeared temporarily or the app regained focus.
        if (hasFocus) {
            hideSystemUI()
        }
    }

    override fun onPause() {
        ttsBridge.stop()
        super.onPause()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        webView.saveState(outState)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        filePathCallback?.onReceiveValue(null)
        filePathCallback = null

        ttsBridge.shutdown()

        webView.loadUrl("about:blank")
        webView.stopLoading()
        webView.destroy()

        super.onDestroy()
    }
}
