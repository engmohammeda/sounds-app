package com.agon.app.data

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import kotlin.math.*
import kotlin.random.Random

class AudioEngine {
    private val sampleRate = 44100
    private val tracks = mutableMapOf<String, AudioTrack>()
    private val threads = mutableMapOf<String, Thread>()
    private val volumes = mutableMapOf<String, Float>()
    @Volatile private var masterVolume = 1.0f
    @Volatile private var fadeMultiplier = 1.0f

    // Pink noise state
    private val pinkB = FloatArray(7)

    // Brown noise state
    private var brownLast = 0f

    fun play(sound: SoundItem, volume: Float = 0.7f) {
        if (tracks.containsKey(sound.id)) {
            setVolume(sound.id, volume)
            return
        }

        volumes[sound.id] = volume

        val bufferSize = AudioTrack.getMinBufferSize(
            sampleRate,
            AudioFormat.CHANNEL_OUT_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        ).coerceAtLeast(sampleRate * 2)

        val audioTrack = AudioTrack.Builder()
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
            .setAudioFormat(
                AudioFormat.Builder()
                    .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                    .setSampleRate(sampleRate)
                    .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                    .build()
            )
            .setBufferSizeInBytes(bufferSize)
            .setTransferMode(AudioTrack.MODE_STREAM)
            .build()

        tracks[sound.id] = audioTrack

        val thread = Thread {
            audioTrack.play()
            val buffer = ShortArray(4096)
            var phase = 0.0
            var phase2 = 0.0
            var modPhase = 0.0
            var localBrownLast = 0f
            val localPinkB = FloatArray(7)
            var bellPhase = 0.0
            var bellEnvelope = 1.0
            var bellTriggerCount = 0
            val bellInterval = (sampleRate * (3.0 + Random.nextDouble() * 4.0)).toInt()

            try {
                while (!Thread.currentThread().isInterrupted && tracks.containsKey(sound.id)) {
                    for (i in buffer.indices) {
                        val vol = (volumes[sound.id] ?: 0.7f) * masterVolume * fadeMultiplier
                        val sample = when (sound.waveType) {
                            WaveType.SINE -> {
                                val s = sin(2.0 * PI * sound.baseFrequency * phase / sampleRate)
                                phase++
                                (s * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.SQUARE -> {
                                val s = if (sin(2.0 * PI * sound.baseFrequency * phase / sampleRate) >= 0) 1.0 else -1.0
                                phase++
                                (s * vol * 0.3 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.TRIANGLE -> {
                                val t = (phase * sound.baseFrequency / sampleRate) % 1.0
                                val s = 2.0 * abs(2.0 * t - 1.0) - 1.0
                                phase++
                                (s * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.SAWTOOTH -> {
                                val t = (phase * sound.baseFrequency / sampleRate) % 1.0
                                val s = 2.0 * t - 1.0
                                phase++
                                (s * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.NOISE_WHITE -> {
                                val s = Random.nextFloat() * 2f - 1f
                                (s * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.NOISE_PINK -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localPinkB[0] = 0.99886f * localPinkB[0] + white * 0.0555179f
                                localPinkB[1] = 0.99332f * localPinkB[1] + white * 0.0750759f
                                localPinkB[2] = 0.96900f * localPinkB[2] + white * 0.1538520f
                                localPinkB[3] = 0.86650f * localPinkB[3] + white * 0.3104856f
                                localPinkB[4] = 0.55000f * localPinkB[4] + white * 0.5329522f
                                localPinkB[5] = -0.7616f * localPinkB[5] - white * 0.0168980f
                                val pink = (localPinkB[0] + localPinkB[1] + localPinkB[2] + localPinkB[3] + localPinkB[4] + localPinkB[5] + localPinkB[6] + white * 0.5362f) / 7f
                                localPinkB[6] = white * 0.115926f
                                (pink * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.NOISE_BROWN -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localBrownLast = (localBrownLast + (0.02f * white)).coerceIn(-1f, 1f)
                                (localBrownLast * vol * 0.8 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.BINAURAL -> {
                                val s1 = sin(2.0 * PI * sound.baseFrequency * phase / sampleRate)
                                val s2 = sin(2.0 * PI * sound.secondaryFrequency * phase / sampleRate)
                                phase++
                                val mixed = (s1 + s2) / 2.0
                                (mixed * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.ISOCHRONIC -> {
                                val carrier = sin(2.0 * PI * sound.baseFrequency * phase / sampleRate)
                                val modFreq = sound.modulationRate.toDouble()
                                val envelope = (sin(2.0 * PI * modFreq * modPhase / sampleRate) + 1.0) / 2.0
                                phase++
                                modPhase++
                                (carrier * envelope * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.PULSE -> {
                                val modFreq = sound.modulationRate.toDouble()
                                val env = (sin(2.0 * PI * modFreq * modPhase / sampleRate) + 1.0) / 2.0
                                val noise = Random.nextFloat() * 2f - 1f
                                modPhase++
                                (noise * env * vol * 0.3 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.HARMONIC -> {
                                var s = 0.0
                                for (h in 1..sound.harmonics) {
                                    val amp = 1.0 / (h * h)
                                    s += amp * sin(2.0 * PI * sound.baseFrequency * h * phase / sampleRate)
                                }
                                phase++
                                (s / sound.harmonics * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.CHOIR -> {
                                var s = 0.0
                                val voices = sound.harmonics.coerceAtLeast(3)
                                for (v in 0 until voices) {
                                    val detune = 1.0 + (v - voices / 2) * 0.003
                                    val vibrato = sin(2.0 * PI * (4.5 + v * 0.3) * modPhase / sampleRate) * 2.0
                                    s += sin(2.0 * PI * (sound.baseFrequency * detune + vibrato) * phase / sampleRate)
                                }
                                phase++
                                modPhase++
                                (s / voices * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.WIND -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localBrownLast = (localBrownLast + 0.02f * white).coerceIn(-1f, 1f)
                                val windMod = (sin(2.0 * PI * 0.1 * modPhase / sampleRate) + 1.0) / 2.0 * 0.5 + 0.5
                                modPhase++
                                (localBrownLast * windMod.toFloat() * vol * 0.6 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.RAIN -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localPinkB[0] = 0.99886f * localPinkB[0] + white * 0.0555179f
                                localPinkB[1] = 0.99332f * localPinkB[1] + white * 0.0750759f
                                localPinkB[2] = 0.96900f * localPinkB[2] + white * 0.1538520f
                                val rain = (localPinkB[0] + localPinkB[1] + localPinkB[2]) / 3f
                                val dropChance = Random.nextFloat()
                                val drop = if (dropChance > 0.998f) Random.nextFloat() * 0.5f else 0f
                                ((rain * 0.7f + drop) * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.OCEAN -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localBrownLast = (localBrownLast + 0.01f * white).coerceIn(-1f, 1f)
                                val wave1 = (sin(2.0 * PI * 0.08 * modPhase / sampleRate) + 1.0) / 2.0
                                val wave2 = (sin(2.0 * PI * 0.05 * modPhase / sampleRate) + 1.0) / 2.0
                                val combined = (wave1 * 0.6 + wave2 * 0.4)
                                modPhase++
                                (localBrownLast * combined.toFloat() * vol * 0.8 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.THUNDER -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localBrownLast = (localBrownLast + 0.015f * white).coerceIn(-1f, 1f)
                                val rumble = sin(2.0 * PI * sound.baseFrequency * phase / sampleRate) * 0.3
                                phase++
                                val thunderChance = Random.nextFloat()
                                val thunder = if (thunderChance > 0.9999f) {
                                    bellEnvelope = 1.0
                                    0.0
                                } else {
                                    bellEnvelope *= 0.99998
                                    bellEnvelope * Random.nextFloat() * 0.4f
                                }
                                ((localBrownLast * 0.5f + rumble.toFloat() + thunder.toFloat()) * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.CRICKETS -> {
                                val chirpRate = 15.0 + sin(2.0 * PI * 0.2 * modPhase / sampleRate) * 5.0
                                val chirp = sin(2.0 * PI * 4500.0 * phase / sampleRate)
                                val env = (sin(2.0 * PI * chirpRate * modPhase / sampleRate).coerceAtLeast(0.0)).pow(4.0)
                                val chirp2 = sin(2.0 * PI * 3800.0 * phase / sampleRate)
                                val env2 = (sin(2.0 * PI * (chirpRate * 0.7) * modPhase / sampleRate + 1.5).coerceAtLeast(0.0)).pow(4.0)
                                phase++
                                modPhase++
                                ((chirp * env * 0.3 + chirp2 * env2 * 0.2) * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.FIRE -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localBrownLast = (localBrownLast + 0.03f * white).coerceIn(-1f, 1f)
                                val crackle = if (Random.nextFloat() > 0.995f) Random.nextFloat() * 0.8f else 0f
                                val pop = if (Random.nextFloat() > 0.999f) (Random.nextFloat() - 0.5f) * 1.5f else 0f
                                ((localBrownLast * 0.4f + crackle + pop) * vol * 0.4 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.STREAM -> {
                                val white = Random.nextFloat() * 2f - 1f
                                localPinkB[0] = 0.99f * localPinkB[0] + white * 0.1f
                                localPinkB[1] = 0.97f * localPinkB[1] + white * 0.15f
                                val stream = (localPinkB[0] + localPinkB[1]) / 2f
                                val babble = sin(2.0 * PI * 1200 * phase / sampleRate) * (sin(2.0 * PI * 3.0 * modPhase / sampleRate) + 1.0) / 2.0 * 0.05
                                phase++
                                modPhase++
                                ((stream * 0.6f + babble.toFloat()) * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.BIRDS -> {
                                val birdFreq = 2000.0 + sin(2.0 * PI * 8.0 * modPhase / sampleRate) * 800.0
                                val bird = sin(2.0 * PI * birdFreq * phase / sampleRate)
                                val tweetEnv = (sin(2.0 * PI * 3.0 * modPhase / sampleRate).coerceAtLeast(0.0)).pow(2.0)
                                val bird2Freq = 3000.0 + sin(2.0 * PI * 12.0 * modPhase / sampleRate) * 600.0
                                val bird2 = sin(2.0 * PI * bird2Freq * phase / sampleRate)
                                val tweet2Env = (sin(2.0 * PI * 2.0 * modPhase / sampleRate + 2.0).coerceAtLeast(0.0)).pow(3.0)
                                phase++
                                modPhase++
                                ((bird * tweetEnv * 0.2 + bird2 * tweet2Env * 0.15) * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.BELL -> {
                                bellTriggerCount++
                                if (bellTriggerCount >= bellInterval) {
                                    bellTriggerCount = 0
                                    bellEnvelope = 1.0
                                    bellPhase = 0.0
                                }
                                var s = 0.0
                                val partials = listOf(1.0, 2.76, 5.40, 8.93, 13.34, 18.64)
                                for (idx in partials.indices) {
                                    if (idx >= sound.harmonics) break
                                    val amp = 1.0 / (idx + 1)
                                    s += amp * sin(2.0 * PI * sound.baseFrequency * partials[idx] * bellPhase / sampleRate)
                                }
                                bellPhase++
                                val decayRate = 1.0 - (1.0 / (sound.releaseMs.toDouble() / 1000.0 * sampleRate))
                                bellEnvelope *= decayRate
                                (s * bellEnvelope * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.DRONE -> {
                                var s = 0.0
                                for (h in 1..sound.harmonics) {
                                    val detune = 1.0 + sin(2.0 * PI * (0.1 * h) * modPhase / sampleRate) * 0.002
                                    val amp = 1.0 / h
                                    s += amp * sin(2.0 * PI * sound.baseFrequency * h * detune * phase / sampleRate)
                                }
                                phase++
                                modPhase++
                                (s / sound.harmonics * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.SWEEP -> {
                                val sweepDuration = 1.0 / sound.modulationRate.coerceAtLeast(0.001f)
                                val t = (modPhase / sampleRate) % sweepDuration / sweepDuration
                                val currentFreq = sound.baseFrequency + (sound.secondaryFrequency - sound.baseFrequency) * t
                                val s = sin(2.0 * PI * currentFreq * phase / sampleRate)
                                phase++
                                modPhase++
                                (s * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.FM_SYNTH -> {
                                val modSig = sin(2.0 * PI * sound.modulationRate * modPhase / sampleRate) * sound.modulationDepth
                                val s = sin(2.0 * PI * (sound.baseFrequency + modSig) * phase / sampleRate)
                                phase++
                                modPhase++
                                (s * vol * 0.7 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.AM_SYNTH -> {
                                val carrier = sin(2.0 * PI * sound.baseFrequency * phase / sampleRate)
                                val mod = (sin(2.0 * PI * sound.modulationRate * modPhase / sampleRate) + 1.0) / 2.0
                                phase++
                                modPhase++
                                (carrier * mod * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.FILTERED_NOISE -> {
                                val white = Random.nextFloat() * 2f - 1f
                                val cutoff = sound.filterCutoff.coerceAtLeast(50f)
                                val rc = 1.0f / (2.0f * PI.toFloat() * cutoff)
                                val dt = 1.0f / sampleRate
                                val alpha = dt / (rc + dt)
                                localBrownLast = localBrownLast + alpha * (white - localBrownLast)
                                val modulation = if (sound.modulationRate > 0f) {
                                    val m = (sin(2.0 * PI * sound.modulationRate * modPhase / sampleRate) + 1.0) / 2.0 * 0.5 + 0.5
                                    modPhase++
                                    m.toFloat()
                                } else 1f
                                (localBrownLast * modulation * vol * 0.8 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.BOWL -> {
                                // Same as BELL but with different characteristics
                                bellTriggerCount++
                                if (bellTriggerCount >= bellInterval) {
                                    bellTriggerCount = 0
                                    bellEnvelope = 1.0
                                    bellPhase = 0.0
                                }
                                var s = 0.0
                                val bowlPartials = listOf(1.0, 2.71, 5.19, 8.75)
                                for (idx in bowlPartials.indices) {
                                    if (idx >= sound.harmonics) break
                                    val amp = 1.0 / (idx + 1)
                                    s += amp * sin(2.0 * PI * sound.baseFrequency * bowlPartials[idx] * bellPhase / sampleRate)
                                }
                                bellPhase++
                                val bowlDecayRate = 1.0 - (1.0 / (sound.releaseMs.toDouble() / 1000.0 * sampleRate))
                                bellEnvelope *= bowlDecayRate
                                (s * bellEnvelope * vol * 0.5 * Short.MAX_VALUE).toInt().toShort()
                            }
                            WaveType.HEARTBEAT -> {
                                val bpm = 60.0
                                val beatDuration = 60.0 / bpm
                                val t = (phase / sampleRate) % beatDuration
                                val lub = if (t < 0.1) {
                                    sin(2.0 * PI * 60.0 * t) * exp(-t * 30.0)
                                } else 0.0
                                val dub = if (t > 0.2 && t < 0.3) {
                                    val t2 = t - 0.2
                                    sin(2.0 * PI * 80.0 * t2) * exp(-t2 * 40.0) * 0.7
                                } else 0.0
                                phase++
                                ((lub + dub) * vol * Short.MAX_VALUE).toInt().toShort()
                            }
                        }
                        buffer[i] = sample
                    }
                    audioTrack.write(buffer, 0, buffer.size)
                }
            } catch (e: Exception) {
                // Thread interrupted
            } finally {
                try {
                    audioTrack.stop()
                    audioTrack.release()
                } catch (_: Exception) {}
            }
        }
        thread.isDaemon = true
        thread.start()
        threads[sound.id] = thread
    }

    fun stop(soundId: String) {
        threads[soundId]?.interrupt()
        threads.remove(soundId)
        tracks.remove(soundId)
        volumes.remove(soundId)
    }

    fun stopAll() {
        val ids = tracks.keys.toList()
        ids.forEach { stop(it) }
    }

    fun setVolume(soundId: String, volume: Float) {
        volumes[soundId] = volume.coerceIn(0f, 1f)
    }

    fun setMasterVolume(volume: Float) {
        masterVolume = volume.coerceIn(0f, 1f)
    }

    fun setFadeMultiplier(mult: Float) {
        fadeMultiplier = mult.coerceIn(0f, 1f)
    }

    fun isPlaying(soundId: String): Boolean = tracks.containsKey(soundId)

    fun getActiveSoundIds(): Set<String> = tracks.keys.toSet()

    fun getVolume(soundId: String): Float = volumes[soundId] ?: 0.7f

    fun release() {
        stopAll()
    }
}
