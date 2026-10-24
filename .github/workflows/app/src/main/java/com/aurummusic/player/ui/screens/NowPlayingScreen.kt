package com.aurummusic.player.ui.screens

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bedtime
import androidx.compose.material.icons.filled.Equalizer
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Forward10
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Repeat
import androidx.compose.material.icons.filled.RepeatOne
import androidx.compose.material.icons.filled.Replay10
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aurummusic.player.data.model.RepeatMode
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.theme.BorderHighlight
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.PurpleDeep
import com.aurummusic.player.ui.theme.PurpleGlow
import com.aurummusic.player.ui.theme.RoseAccent
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceGlow
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon

@Composable
fun NowPlayingScreen(
    song: Song?,
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    isShuffle: Boolean,
    repeatMode: RepeatMode,
    isSleepTimerActive: Boolean,
    sleepTimerRemaining: String,
    onBackClick: () -> Unit,
    onPlayPause: () -> Unit,
    onSkipNext: () -> Unit,
    onSkipPrevious: () -> Unit,
    onSeekTo: (Long) -> Unit,
    onToggleShuffle: () -> Unit,
    onToggleRepeat: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onOpenEqualizer: () -> Unit,
    onOpenQueue: () -> Unit,
    onStartSleepTimer: (Int) -> Unit,
    onCancelSleepTimer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isSeeking by remember { mutableStateOf(false) }
    var seekPosition by remember { mutableStateOf(0f) }
    var showSleepTimerModal by remember { mutableStateOf(false) }

    if (song == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(Color(0xFF0B0B0F)),
            contentAlignment = Alignment.Center
        ) {
            Text("No song loaded", color = TextMuted)
        }
        return
    }

    val sliderProgress = if (isSeeking) {
        seekPosition
    } else {
        if (duration > 0) (currentPosition.toFloat() / duration.toFloat()).coerceIn(0f, 1f) else 0f
    }

    val displayedElapsedMs = if (isSeeking) {
        (seekPosition * duration).toLong()
    } else {
        currentPosition
    }

    val elapsedFormatted = formatTime(displayedElapsedMs)
    val remainingFormatted = "-" + formatTime((duration - displayedElapsedMs).coerceAtLeast(0L))

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF20123A),
                        Color(0xFF110A22),
                        Color(0xFF08070D)
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // Header row: Collapse button, Title, Sleep Timer indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Collapse",
                        tint = TextWhite,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "PLAYING FROM LIBRARY",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = song.album,
                        color = VioletNeon,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(onClick = { showSleepTimerModal = true }) {
                    Icon(
                        imageVector = Icons.Default.Bedtime,
                        contentDescription = "Sleep Timer",
                        tint = if (isSleepTimerActive) GoldAccent else TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Large Album Artwork with glowing violet border
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(28.dp))
                    .background(SurfaceCard)
                    .border(2.dp, BorderHighlight.copy(alpha = 0.6f), RoundedCornerShape(28.dp)),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = song.artworkUri,
                    contentDescription = song.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = null
                )
                Icon(
                    imageVector = Icons.Default.MusicNote,
                    contentDescription = null,
                    tint = PurpleGlow.copy(alpha = 0.6f),
                    modifier = Modifier.size(80.dp)
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Title & Artist row with Favorite Heart
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = song.artist,
                        color = TextMuted,
                        fontSize = 15.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                IconButton(
                    onClick = { onToggleFavorite(song.id) },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = if (song.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (song.isFavorite) RoseAccent else TextMuted,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Slider
            Slider(
                value = sliderProgress,
                onValueChange = {
                    isSeeking = true
                    seekPosition = it
                },
                onValueChangeFinished = {
                    isSeeking = false
                    val targetMs = (seekPosition * duration).toLong()
                    onSeekTo(targetMs)
                },
                colors = SliderDefaults.colors(
                    thumbColor = GoldAccent,
                    activeTrackColor = PurpleGlow,
                    inactiveTrackColor = BorderSubtle
                ),
                modifier = Modifier.fillMaxWidth()
            )

            // Elapsed & Remaining time labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = elapsedFormatted, color = TextMuted, fontSize = 12.sp)
                Text(text = remainingFormatted, color = TextMuted, fontSize = 12.sp)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Main Playback Controls Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Shuffle
                IconButton(onClick = onToggleShuffle) {
                    Icon(
                        imageVector = Icons.Default.Shuffle,
                        contentDescription = "Shuffle",
                        tint = if (isShuffle) GoldAccent else TextMuted,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Previous
                IconButton(onClick = onSkipPrevious) {
                    Icon(
                        imageVector = Icons.Default.SkipPrevious,
                        contentDescription = "Previous",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Rewind 10s
                IconButton(onClick = { onSeekTo((currentPosition - 10000).coerceAtLeast(0L)) }) {
                    Icon(
                        imageVector = Icons.Default.Replay10,
                        contentDescription = "Rewind 10s",
                        tint = TextLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Play / Pause prominent circular button
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(PurpleAccent, PurpleDeep)
                            )
                        )
                        .border(1.5.dp, GoldAccent.copy(alpha = 0.5f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onPlayPause,
                        modifier = Modifier.size(68.dp)
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = TextWhite,
                            modifier = Modifier.size(38.dp)
                        )
                    }
                }

                // Forward 10s
                IconButton(onClick = { onSeekTo((currentPosition + 10000).coerceAtMost(duration)) }) {
                    Icon(
                        imageVector = Icons.Default.Forward10,
                        contentDescription = "Forward 10s",
                        tint = TextLight,
                        modifier = Modifier.size(26.dp)
                    )
                }

                // Next
                IconButton(onClick = onSkipNext) {
                    Icon(
                        imageVector = Icons.Default.SkipNext,
                        contentDescription = "Next",
                        tint = TextWhite,
                        modifier = Modifier.size(36.dp)
                    )
                }

                // Repeat (Off, All, One)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            when (repeatMode) {
                                RepeatMode.OFF -> androidx.compose.ui.graphics.Color.Transparent
                                RepeatMode.ALL -> PurpleGlow.copy(alpha = 0.2f)
                                RepeatMode.ONE -> GoldAccent.copy(alpha = 0.25f)
                            }
                        )
                ) {
                    IconButton(onClick = onToggleRepeat) {
                        val (icon, tint) = when (repeatMode) {
                            RepeatMode.OFF -> Pair(Icons.Default.Repeat, TextMuted)
                            RepeatMode.ALL -> Pair(Icons.Default.Repeat, PurpleAccent)
                            RepeatMode.ONE -> Pair(Icons.Default.RepeatOne, GoldAccent)
                        }
                        Icon(
                            imageVector = icon,
                            contentDescription = when (repeatMode) {
                                RepeatMode.OFF -> "Repeat Off"
                                RepeatMode.ALL -> "Repeat All"
                                RepeatMode.ONE -> "Repeat One"
                            },
                            tint = tint,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Bottom Quick Action Bar: Equalizer, Queue
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(SurfaceCard.copy(alpha = 0.8f))
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenEqualizer) {
                        Icon(Icons.Default.Equalizer, contentDescription = "Equalizer", tint = VioletNeon)
                    }
                    Text(
                        text = "Equalizer",
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(24.dp)
                        .background(BorderSubtle)
                )

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onOpenQueue) {
                        Icon(Icons.Default.QueueMusic, contentDescription = "Queue", tint = PurpleAccent)
                    }
                    Text(
                        text = "Playing Queue",
                        color = TextLight,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    if (showSleepTimerModal) {
        SleepTimerDialog(
            isTimerActive = isSleepTimerActive,
            remainingFormatted = sleepTimerRemaining,
            onSelectMinutes = { mins ->
                onStartSleepTimer(mins)
                showSleepTimerModal = false
            },
            onCancelTimer = {
                onCancelSleepTimer()
                showSleepTimerModal = false
            },
            onDismiss = { showSleepTimerModal = false }
        )
    }
}

@Composable
fun SleepTimerDialog(
    isTimerActive: Boolean,
    remainingFormatted: String,
    onSelectMinutes: (Int) -> Unit,
    onCancelTimer: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (isTimerActive) "Sleep Timer: $remainingFormatted" else "Set Sleep Timer",
                color = TextWhite,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                val durations = listOf(15, 30, 45, 60, 90)
                durations.forEach { mins ->
                    Button(
                        onClick = { onSelectMinutes(mins) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlow)
                    ) {
                        Text("$mins minutes", color = VioletNeon)
                    }
                }
                if (isTimerActive) {
                    Button(
                        onClick = onCancelTimer,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = RoseAccent.copy(alpha = 0.2f))
                    ) {
                        Text("Turn Off Timer", color = RoseAccent)
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = TextMuted)
            }
        },
        containerColor = SurfaceCard
    )
}

private fun formatTime(ms: Long): String {
    val totalSeconds = (ms / 1000).coerceAtLeast(0)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "%d:%02d".format(minutes, seconds)
}
