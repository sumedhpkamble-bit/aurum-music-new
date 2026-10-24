package com.aurummusic.player.playback

import android.media.audiofx.BassBoost
import android.media.audiofx.Equalizer
import android.media.audiofx.Virtualizer
import com.aurummusic.player.data.model.EqualizerBand
import com.aurummusic.player.data.model.EqualizerPreset
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AudioEffectManager {

    private var equalizer: Equalizer? = null
    private var bassBoost: BassBoost? = null
    private var virtualizer: Virtualizer? = null

    private val _isEnabled = MutableStateFlow(true)
    val isEnabled: StateFlow<Boolean> = _isEnabled.asStateFlow()

    private val _bands = MutableStateFlow<List<EqualizerBand>>(emptyList())
    val bands: StateFlow<List<EqualizerBand>> = _bands.asStateFlow()

    private val _bassBoostStrength = MutableStateFlow<Short>(0)
    val bassBoostStrength: StateFlow<Short> = _bassBoostStrength.asStateFlow()

    private val _virtualizerStrength = MutableStateFlow<Short>(0)
    val virtualizerStrength: StateFlow<Short> = _virtualizerStrength.asStateFlow()

    private val _currentPreset = MutableStateFlow<String>("Normal")
    val currentPreset: StateFlow<String> = _currentPreset.asStateFlow()

    val builtInPresets: List<EqualizerPreset> = listOf(
        EqualizerPreset("Flat", listOf(0, 0, 0, 0, 0), 0, 0),
        EqualizerPreset("Bass Boost", listOf(600, 400, 0, 0, -200), 800, 200),
        EqualizerPreset("Rock", listOf(500, 300, -100, 200, 500), 400, 300),
        EqualizerPreset("Pop", listOf(-100, 200, 500, 100, -200), 200, 400),
        EqualizerPreset("Jazz", listOf(0, 200, 0, 300, 500), 300, 400),
        EqualizerPreset("Electronic", listOf(500, 300, 0, 200, 400), 600, 500),
        EqualizerPreset("Vocal Boost", listOf(-300, 0, 600, 300, -100), 0, 200),
        EqualizerPreset("Classical", listOf(400, 300, -200, 400, 400), 200, 500)
    )

    fun attachAudioSession(audioSessionId: Int) {
        if (audioSessionId == 0) return
        try {
            release()

            equalizer = Equalizer(0, audioSessionId).apply {
                enabled = _isEnabled.value
            }

            bassBoost = BassBoost(0, audioSessionId).apply {
                enabled = _isEnabled.value
                if (strengthSupported) {
                    setStrength(_bassBoostStrength.value)
                }
            }

            virtualizer = Virtualizer(0, audioSessionId).apply {
                enabled = _isEnabled.value
                if (strengthSupported) {
                    setStrength(_virtualizerStrength.value)
                }
            }

            loadBands()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun loadBands() {
        val eq = equalizer ?: return
        try {
            val numBands = eq.numberOfBands
            val range = eq.bandLevelRange // [min, max] in millibels
            val min = range.getOrNull(0) ?: -1500
            val max = range.getOrNull(1) ?: 1500

            val bandList = mutableListOf<EqualizerBand>()
            for (i in 0 until numBands) {
                val shortIdx = i.toShort()
                val centerFreq = eq.getCenterFreq(shortIdx) / 1000 // mHz to Hz
                val currentLevel = eq.getBandLevel(shortIdx)
                bandList.add(
                    EqualizerBand(
                        index = shortIdx,
                        centerFreqHz = centerFreq,
                        minLevelMb = min,
                        maxLevelMb = max,
                        currentLevelMb = currentLevel
                    )
                )
            }
            _bands.value = bandList
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBandLevel(bandIndex: Short, levelMb: Short) {
        try {
            equalizer?.setBandLevel(bandIndex, levelMb)
            _bands.value = _bands.value.map {
                if (it.index == bandIndex) it.copy(currentLevelMb = levelMb) else it
            }
            _currentPreset.value = "Custom"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setBassBoost(strength: Short) {
        _bassBoostStrength.value = strength
        try {
            if (bassBoost?.strengthSupported == true) {
                bassBoost?.setStrength(strength)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setVirtualizer(strength: Short) {
        _virtualizerStrength.value = strength
        try {
            if (virtualizer?.strengthSupported == true) {
                virtualizer?.setStrength(strength)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun applyPreset(preset: EqualizerPreset) {
        _currentPreset.value = preset.name
        val eq = equalizer ?: return
        try {
            val numBands = eq.numberOfBands.toInt()
            preset.bandLevels.forEachIndexed { index, level ->
                if (index < numBands) {
                    eq.setBandLevel(index.toShort(), level)
                }
            }
            setBassBoost(preset.bassBoost)
            setVirtualizer(preset.virtualizer)
            loadBands()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun setEnabled(enabled: Boolean) {
        _isEnabled.value = enabled
        try {
            equalizer?.enabled = enabled
            bassBoost?.enabled = enabled
            virtualizer?.enabled = enabled
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun release() {
        try {
            equalizer?.release()
            bassBoost?.release()
            virtualizer?.release()
            equalizer = null
            bassBoost = null
            virtualizer = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
