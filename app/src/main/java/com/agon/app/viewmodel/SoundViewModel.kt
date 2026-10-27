package com.agon.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.agon.app.data.*
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SoundUiState(
    val activeSounds: Map<String, Float> = emptyMap(),
    val isAnyPlaying: Boolean = false,
    val timerMinutes: Int = 0,
    val timerRemainingSeconds: Long = 0,
    val isTimerRunning: Boolean = false,
    val masterVolume: Float = 0.8f,
    val selectedCategory: SoundCategory? = null,
    val isMixerOpen: Boolean = false,
    val favoriteIds: Set<String> = emptySet()
)

class SoundViewModel(application: Application) : AndroidViewModel(application) {
    private val audioEngine = AudioEngine()
    private val _uiState = MutableStateFlow(SoundUiState())
    val uiState: StateFlow<SoundUiState> = _uiState.asStateFlow()

    private var timerJob: Job? = null
    private val fadeOutDuration = 15_000L // 15 seconds fade out

    fun toggleSound(sound: SoundItem) {
        if (audioEngine.isPlaying(sound.id)) {
            audioEngine.stop(sound.id)
        } else {
            audioEngine.play(sound, _uiState.value.activeSounds[sound.id] ?: 0.7f)
        }
        updateActiveState()
    }

    fun setSoundVolume(soundId: String, volume: Float) {
        audioEngine.setVolume(soundId, volume)
        _uiState.value = _uiState.value.copy(
            activeSounds = _uiState.value.activeSounds.toMutableMap().apply {
                put(soundId, volume)
            }
        )
    }

    fun setMasterVolume(volume: Float) {
        audioEngine.setMasterVolume(volume)
        _uiState.value = _uiState.value.copy(masterVolume = volume)
    }

    fun stopAll() {
        audioEngine.stopAll()
        timerJob?.cancel()
        _uiState.value = _uiState.value.copy(
            activeSounds = emptyMap(),
            isAnyPlaying = false,
            isTimerRunning = false,
            timerRemainingSeconds = 0
        )
    }

    fun startTimer(minutes: Int) {
        timerJob?.cancel()
        if (minutes == 0) {
            _uiState.value = _uiState.value.copy(
                timerMinutes = 0,
                timerRemainingSeconds = 0,
                isTimerRunning = false
            )
            audioEngine.setFadeMultiplier(1f)
            return
        }

        val totalSeconds = minutes * 60L
        _uiState.value = _uiState.value.copy(
            timerMinutes = minutes,
            timerRemainingSeconds = totalSeconds,
            isTimerRunning = true
        )

        timerJob = viewModelScope.launch {
            var remaining = totalSeconds
            while (remaining > 0) {
                delay(1000)
                remaining--
                
                // Fade out in last 15 seconds
                if (remaining <= fadeOutDuration / 1000) {
                    val fade = remaining.toFloat() / (fadeOutDuration / 1000f)
                    audioEngine.setFadeMultiplier(fade)
                }

                _uiState.value = _uiState.value.copy(
                    timerRemainingSeconds = remaining
                )
            }
            // Timer finished - stop all
            audioEngine.stopAll()
            audioEngine.setFadeMultiplier(1f)
            _uiState.value = _uiState.value.copy(
                activeSounds = emptyMap(),
                isAnyPlaying = false,
                isTimerRunning = false,
                timerRemainingSeconds = 0
            )
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        audioEngine.setFadeMultiplier(1f)
        _uiState.value = _uiState.value.copy(
            isTimerRunning = false,
            timerRemainingSeconds = 0,
            timerMinutes = 0
        )
    }

    fun selectCategory(category: SoundCategory?) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun toggleMixer() {
        _uiState.value = _uiState.value.copy(isMixerOpen = !_uiState.value.isMixerOpen)
    }

    fun toggleFavorite(soundId: String) {
        val current = _uiState.value.favoriteIds.toMutableSet()
        if (current.contains(soundId)) current.remove(soundId) else current.add(soundId)
        _uiState.value = _uiState.value.copy(favoriteIds = current)
    }

    fun isSoundPlaying(soundId: String): Boolean = audioEngine.isPlaying(soundId)

    private fun updateActiveState() {
        val activeIds = audioEngine.getActiveSoundIds()
        val volumes = mutableMapOf<String, Float>()
        activeIds.forEach { id ->
            volumes[id] = audioEngine.getVolume(id)
        }
        _uiState.value = _uiState.value.copy(
            activeSounds = volumes,
            isAnyPlaying = activeIds.isNotEmpty()
        )
    }

    override fun onCleared() {
        super.onCleared()
        audioEngine.release()
    }
}
