package com.aurummusic.player.playback

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepTimerManager(private val onTimerExpired: () -> Unit) {

    private val scope = CoroutineScope(Dispatchers.Default)
    private var timerJob: Job? = null

    private val _remainingSeconds = MutableStateFlow<Long>(0L)
    val remainingSeconds: StateFlow<Long> = _remainingSeconds.asStateFlow()

    private val _isActive = MutableStateFlow<Boolean>(false)
    val isActive: StateFlow<Boolean> = _isActive.asStateFlow()

    fun startTimer(minutes: Int) {
        cancelTimer()
        if (minutes <= 0) return

        val totalSeconds = minutes * 60L
        _remainingSeconds.value = totalSeconds
        _isActive.value = true

        timerJob = scope.launch {
            var current = totalSeconds
            while (current > 0) {
                delay(1000)
                current--
                _remainingSeconds.value = current
            }
            _isActive.value = false
            onTimerExpired()
        }
    }

    fun cancelTimer() {
        timerJob?.cancel()
        timerJob = null
        _remainingSeconds.value = 0L
        _isActive.value = false
    }

    val formattedRemainingTime: String
        get() {
            val total = _remainingSeconds.value
            val m = total / 60
            val s = total % 60
            return "%02d:%02d".format(m, s)
        }
}
