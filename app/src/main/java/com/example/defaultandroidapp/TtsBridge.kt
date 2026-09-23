package com.example.defaultandroidapp

import android.content.Context
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import java.util.Locale

class TtsBridge(context: Context) : TextToSpeech.OnInitListener {

    private val tts = TextToSpeech(context.applicationContext, this)
    private var ready = false
    private var pending: Pair<String, String>? = null

    override fun onInit(status: Int) {
        ready = status == TextToSpeech.SUCCESS
        if (ready) {
            tts.setPitch(1.25f)
            tts.setSpeechRate(0.9f)
            pending?.let { (text, lang) -> speak(text, lang) }
        }
        pending = null
    }

    @JavascriptInterface
    fun speak(text: String, lang: String) {
        if (!ready) {
            pending = text to lang // first prompt comes before the engine is ready
            return
        }
        val candidates = if (lang == "de") {
            listOf(Locale.GERMANY, Locale.GERMAN)
        } else {
            listOf(Locale("bs", "BA"), Locale("hr", "HR"), Locale("sr", "RS"))
        }
        val locale = candidates.firstOrNull {
            tts.isLanguageAvailable(it) >= TextToSpeech.LANG_AVAILABLE
        } ?: return // no fitting voice installed: stay silent instead of a wrong accent
        tts.language = locale
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, "dino")
    }

    @JavascriptInterface
    fun stop() {
        tts.stop()
    }

    fun shutdown() {
        tts.stop()
        tts.shutdown()
    }
}
