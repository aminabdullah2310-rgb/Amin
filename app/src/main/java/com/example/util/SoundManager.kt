package com.example.util

import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Handler
import android.os.Looper

object SoundManager {

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 85)
        } catch (e: Exception) {
            toneGenerator = null
        }
    }

    fun playWhistle() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 160)
            Handler(Looper.getMainLooper()).postDelayed({
                toneGenerator?.startTone(ToneGenerator.TONE_CDMA_ALERT_NETWORK_LITE, 280)
            }, 200)
        } catch (e: Exception) {
            // Ignore if audio hardware is unavailable
        }
    }

    fun playGoalCheer() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 300)
            Handler(Looper.getMainLooper()).postDelayed({
                toneGenerator?.startTone(ToneGenerator.TONE_PROP_PROMPT, 400)
            }, 320)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun playClick() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 70)
        } catch (e: Exception) {
            // Ignore
        }
    }

    fun playOffsideBuzzer() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_SUP_ERROR, 220)
        } catch (e: Exception) {
            // Ignore
        }
    }
}
