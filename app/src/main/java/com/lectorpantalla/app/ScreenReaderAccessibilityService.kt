package com.lectorpantalla.app

import android.accessibilityservice.AccessibilityService
import android.speech.tts.TextToSpeech
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

/**
 * Servicio que puede leer el árbol de texto de la ventana activa (por ejemplo,
 * el versículo mostrado en Logos Bible Software) y convertirlo en voz.
 *
 * No lee automáticamente ante cada cambio de pantalla: la lectura se dispara
 * manualmente desde el botón flotante (FloatingButtonService) para que el
 * usuario controle cuándo escuchar el contenido.
 */
class ScreenReaderAccessibilityService : AccessibilityService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsReady = false

    companion object {
        var instance: ScreenReaderAccessibilityService? = null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        tts = TextToSpeech(this, this)
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale("es", "AR")
            ttsReady = true
        }
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Intencionalmente vacío: la lectura es manual, no automática.
    }

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
        instance = null
    }

    /** Lee en voz alta todo el texto visible en la ventana activa. */
    fun readCurrentScreen() {
        val root = rootInActiveWindow
        if (root == null) {
            speak("No pude acceder al contenido de la pantalla.")
            return
        }

        val texts = LinkedHashSet<String>()
        collectText(root, texts)
        root.recycle()

        if (texts.isEmpty()) {
            speak("No encontré texto legible en esta pantalla. Puede que el contenido sea una imagen.")
        } else {
            speak(texts.joinToString(". "))
        }
    }

    fun stopReading() {
        tts?.stop()
    }

    private fun collectText(node: AccessibilityNodeInfo?, out: MutableSet<String>) {
        if (node == null) return

        val text = node.text?.toString()?.trim()
        if (!text.isNullOrEmpty()) out.add(text)

        val desc = node.contentDescription?.toString()?.trim()
        if (!desc.isNullOrEmpty()) out.add(desc)

        for (i in 0 until node.childCount) {
            collectText(node.getChild(i), out)
        }
    }

    private fun speak(text: String) {
        if (!ttsReady) return
        tts?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "lectura_pantalla")
    }
}
