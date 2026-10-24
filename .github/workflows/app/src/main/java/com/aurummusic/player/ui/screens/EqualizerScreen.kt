package com.aurummusic.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurummusic.player.data.model.EqualizerBand
import com.aurummusic.player.data.model.EqualizerPreset
import com.aurummusic.player.playback.AudioEffectManager
import com.aurummusic.player.ui.theme.BlackPrimary
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.PurpleGlow
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceGlow
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon

@Composable
fun EqualizerScreen(
    audioEffectManager: AudioEffectManager,
    isEnabled: Boolean,
    bands: List<EqualizerBand>,
    currentPreset: String,
    bassBoostStrength: Short,
    virtualizerStrength: Short,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Audio Equalizer",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.weight(1f)
            )

            Switch(
                checked = isEnabled,
                onCheckedChange = { audioEffectManager.setEnabled(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldAccent,
                    checkedTrackColor = PurpleAccent
                )
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preset Chips
        Text(
            text = "PRESETS",
            color = TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(audioEffectManager.builtInPresets) { preset ->
                val isSelected = currentPreset == preset.name
                FilterChip(
                    selected = isSelected,
                    onClick = { audioEffectManager.applyPreset(preset) },
                    label = {
                        Text(
                            preset.name,
                            color = if (isSelected) TextWhite else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleAccent,
                        containerColor = SurfaceCard
                    ),
                    border = null
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Frequency Equalizer Bands Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Frequency Bands",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(12.dp))

                if (bands.isEmpty()) {
                    // Default fallback visual representation when hardware effect initializing
                    val defaultBands = listOf("60 Hz", "230 Hz", "910 Hz", "3.6 kHz", "14 kHz")
                    defaultBands.forEach { freq ->
                        FrequencyRowMock(freq)
                    }
                } else {
                    bands.forEach { band ->
                        val min = band.minLevelMb.toFloat()
                        val max = band.maxLevelMb.toFloat()
                        val current = band.currentLevelMb.toFloat()

                        val freqLabel = if (band.centerFreqHz >= 1000) {
                            "%.1f kHz".format(band.centerFreqHz / 1000f)
                        } else {
                            "${band.centerFreqHz} Hz"
                        }

                        Column(modifier = Modifier.padding(vertical = 6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = freqLabel, color = VioletNeon, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                Text(
                                    text = "${band.currentLevelMb / 100} dB",
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            }
                            Slider(
                                value = current,
                                onValueChange = { newVal ->
                                    audioEffectManager.setBandLevel(band.index, newVal.toInt().toShort())
                                },
                                valueRange = min..max,
                                colors = SliderDefaults.colors(
                                    thumbColor = GoldAccent,
                                    activeTrackColor = PurpleGlow,
                                    inactiveTrackColor = BorderSubtle
                                ),
                                enabled = isEnabled
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Bass Boost Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Bass Boost",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${bassBoostStrength / 10}%",
                        color = GoldAccent,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Slider(
                    value = bassBoostStrength.toFloat(),
                    onValueChange = { audioEffectManager.setBassBoost(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = GoldAccent,
                        activeTrackColor = PurpleAccent,
                        inactiveTrackColor = BorderSubtle
                    ),
                    enabled = isEnabled
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 3D Virtualizer Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "3D Virtualizer (Surround)",
                        color = TextWhite,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${virtualizerStrength / 10}%",
                        color = VioletNeon,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Slider(
                    value = virtualizerStrength.toFloat(),
                    onValueChange = { audioEffectManager.setVirtualizer(it.toInt().toShort()) },
                    valueRange = 0f..1000f,
                    colors = SliderDefaults.colors(
                        thumbColor = VioletNeon,
                        activeTrackColor = PurpleGlow,
                        inactiveTrackColor = BorderSubtle
                    ),
                    enabled = isEnabled
                )
            }
        }
    }
}

@Composable
private fun FrequencyRowMock(freq: String) {
    Column(modifier = Modifier.padding(vertical = 4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = freq, color = VioletNeon, fontSize = 13.sp)
            Text(text = "0 dB", color = TextMuted, fontSize = 12.sp)
        }
        Slider(
            value = 0f,
            onValueChange = {},
            valueRange = -1500f..1500f,
            colors = SliderDefaults.colors(
                thumbColor = GoldAccent,
                activeTrackColor = PurpleGlow,
                inactiveTrackColor = BorderSubtle
            )
        )
    }
}
