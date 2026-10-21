package com.example.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Pure Kotlin dynamic audio & haptics synthesizer.
 * Generates fantasy sound effects directly via AudioTrack without external audio asset dependencies.
 */
class GameSoundEffects(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var isMuted = false

    private val vibrator: Vibrator? by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
        }
    }

    fun toggleMute(): Boolean {
        isMuted = !isMuted
        return isMuted
    }

    fun isAudioMuted(): Boolean = isMuted

    fun playSwordSlash() {
        if (isMuted) return
        vibrate(30)
        scope.launch {
            // Quick sweeping pitch downwards (woosh + metal impact)
            generateToneSweep(startFreq = 800f, endFreq = 220f, durationMs = 120, volume = 0.6f)
        }
    }

    fun playMagicCast() {
        if (isMuted) return
        vibrate(40)
        scope.launch {
            // High magical rising chirp
            generateToneSweep(startFreq = 440f, endFreq = 980f, durationMs = 180, volume = 0.5f)
        }
    }

    fun playHitImpact() {
        if (isMuted) return
        vibrate(50)
        scope.launch {
            generateToneSweep(startFreq = 280f, endFreq = 90f, durationMs = 100, volume = 0.7f)
        }
    }

    fun playCoinPickup() {
        if (isMuted) return
        vibrate(20)
        scope.launch {
            // Dual chime (B5 -> E6)
            playTwoNotes(987f, 1318f, 70)
        }
    }

    fun playLevelUpFanfare() {
        if (isMuted) return
        vibrate(100)
        scope.launch {
            // Major triad arpeggio: C5 -> E5 -> G5 -> C6
            val notes = listOf(523f, 659f, 784f, 1046f)
            for (freq in notes) {
                generateTone(freq, durationMs = 110, volume = 0.7f)
            }
        }
    }

    fun playQuestComplete() {
        if (isMuted) return
        vibrate(80)
        scope.launch {
            val notes = listOf(440f, 554f, 659f, 880f)
            for (freq in notes) {
                generateTone(freq, durationMs = 100, volume = 0.65f)
            }
        }
    }

    fun playPotionDrink() {
        if (isMuted) return
        vibrate(25)
        scope.launch {
            generateToneSweep(startFreq = 300f, endFreq = 650f, durationMs = 150, volume = 0.5f)
        }
    }

    fun playDodgeRoll() {
        if (isMuted) return
        vibrate(20)
        scope.launch {
            generateToneSweep(startFreq = 500f, endFreq = 180f, durationMs = 90, volume = 0.4f)
        }
    }

    fun playChestOpen() {
        if (isMuted) return
        vibrate(60)
        scope.launch {
            playTwoNotes(600f, 900f, 120)
        }
    }

    fun vibrate(durationMs: Long) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(durationMs)
            }
        } catch (_: Exception) {
            // Silently ignore if vibrator unavailable
        }
    }

    private fun playTwoNotes(f1: Float, f2: Float, noteDurationMs: Int) {
        generateTone(f1, noteDurationMs, volume = 0.55f)
        generateTone(f2, noteDurationMs, volume = 0.65f)
    }

    private fun generateTone(frequency: Float, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(numSamples)
        for (i in 0 until numSamples) {
            val envelope = (1f - (i.toFloat() / numSamples)) // Linear decay
            val angle = 2.0 * Math.PI * i / (sampleRate / frequency)
            val sample = (sin(angle) * Short.MAX_VALUE * volume * envelope).toInt()
            buffer[i] = sample.toShort()
        }
        playAudioBuffer(buffer, sampleRate)
    }

    private fun generateToneSweep(startFreq: Float, endFreq: Float, durationMs: Int, volume: Float) {
        val sampleRate = 22050
        val numSamples = (sampleRate * (durationMs / 1000f)).toInt()
        val buffer = ShortArray(numSamples)
        var phase = 0.0
        for (i in 0 until numSamples) {
            val progress = i.toFloat() / numSamples
            val currentFreq = startFreq + (endFreq - startFreq) * progress
            val envelope = (1f - progress * 0.7f)
            phase += 2.0 * Math.PI * currentFreq / sampleRate
            val sample = (sin(phase) * Short.MAX_VALUE * volume * envelope).toInt()
            buffer[i] = sample.toShort()
        }
        playAudioBuffer(buffer, sampleRate)
    }

    private fun playAudioBuffer(buffer: ShortArray, sampleRate: Int) {
        try {
            val track = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_GAME)
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(buffer.size * 2)
                .setTransferMode(AudioTrack.MODE_STATIC)
                .build()

            track.write(buffer, 0, buffer.size)
            track.play()
            // Release after playback finished in separate thread
            scope.launch {
                val delayMs = (buffer.size.toDouble() / sampleRate * 1000).toLong() + 50
                kotlinx.coroutines.delay(delayMs)
                try {
                    track.stop()
                    track.release()
                } catch (_: Exception) {}
            }
        } catch (_: Exception) {}
    }
}
