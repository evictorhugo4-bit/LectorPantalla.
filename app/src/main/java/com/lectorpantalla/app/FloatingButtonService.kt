package com.lectorpantalla.app

import android.accessibilityservice.AccessibilityService
import android.graphics.Bitmap
import android.os.Build
import android.speech.tts.TextToSpeech
import android.view.Display
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.util.Locale
import java.util.concurrent.Executors

class ScreenReaderAccessibilityService : AccessibilityService(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var ttsReady = false
    private val ocrExecutor = Executors.newSingleThreadExecutor()

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

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {}

    override fun onInterrupt() {}

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
        instance = null
    }

    fun readCurrentScreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            readScreenWithOcr()
        } else {
            readScreenWithAccessibilityTree()
        }
    }

    fun stopReading() {
        tts?.stop()
    }

    @Suppress("DEPRECATION")
    private fun readScreenWithOcr() {
        speak("Leyendo pantalla")
        takeScreenshot(
            Display.DEFAULT_DISPLAY,
            ocrExecutor,
            object : TakeScreenshotCallback {
                override fun onSuccess(result: ScreenshotResult) {
                    try {
                        val hardwareBitmap =
                            Bitmap.wrapHardwareBuffer(result.hardwareBuffer, result.colorSpace)
                        result.hardwareBuffer.close()

                        if (hardwareBitmap == null) {
                            speak("No pude capturar la pantalla.")
                            return
                        }

                        val bitmap = hardwareBitmap.copy(Bitmap.Config.ARGB_8888, false)
                        hardwareBitmap.recycle()

                        val image = InputImage.fromBitmap(bitmap, 0)
                        val recognizer =
                            TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

                        recognizer.process(image)
                            .addOnSuccessListener { visionText ->
                                val text = visionText.text.trim()
                                if (text.isEmpty()) {
                                    speak("No encontré texto en esta pantalla.")
                                } else {
                                    speak(text)
                                }
                                bitmap.recycle()
                            }
                            .addOnFailureListener {
                                speak("No pude reconocer el texto de la pantalla.")
                                bitmap.recycle()
                            }
                    } catch (e: Exception) {
                        speak("Ocurrió un error al capturar la pantalla.")
                    }
                }

                override fun onFailure(errorCode: Int) {
                    speak("No pude capturar la pantalla. Código de error: $errorCode")
                }
            }
        )
    }

    private fun readScreenWithAccessibilityTree() {
        val root = rootInActiveWindow
        if (root == null) {
            speak("No pude acceder al contenido de la pantalla.")
            return
        }

        val texts = LinkedHashSet<String>()
        collectText(root, texts)
        root.recycle()

        if (texts.isEmpty()) {
            speak("No encontré texto legible en esta pantalla.")
        } else {
            speak(texts.joinToString(". "))
        }
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
