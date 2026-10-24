package com.aurummusic.player.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.components.TrackListItem
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

enum class SongSort {
    TITLE,
    ARTIST,
    DATE_ADDED,
    DURATION
}

@Composable
fun SongsScreen(
    songs: List<Song>,
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var currentSort by remember { mutableStateOf(SongSort.TITLE) }
    var showSortMenu by remember { mutableStateOf(false) }

    val filteredSongs = remember(songs, searchQuery, selectedFilter, currentSort) {
        var result = songs.filter {
            it.title.contains(searchQuery, ignoreCase = true) ||
            it.artist.contains(searchQuery, ignoreCase = true) ||
            it.album.contains(searchQuery, ignoreCase = true)
        }

        result = when (selectedFilter) {
            "Favorites" -> result.filter { it.isFavorite }
            "FLAC/WAV" -> result.filter {
                it.mimeType.contains("flac", true) ||
                it.mimeType.contains("wav", true) ||
                it.dataUri.endsWith(".flac") ||
                it.dataUri.endsWith(".wav")
            }
            else -> result
        }

        when (currentSort) {
            SongSort.TITLE -> result.sortedBy { it.title.lowercase() }
            SongSort.ARTIST -> result.sortedBy { it.artist.lowercase() }
            SongSort.DATE_ADDED -> result.sortedByDescending { it.dateAdded }
            SongSort.DURATION -> result.sortedByDescending { it.durationMs }
        }
    }

    val songsByArtist = remember(filteredSongs) {
        filteredSongs.groupBy { it.artist }.toSortedMap(String.CASE_INSENSITIVE_ORDER)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        // Search & Filter header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Search songs, artists, albums...", color = TextMuted, fontSize = 13.sp) },
                leadingIcon = { Icon(Icons.Default.Search, null, tint = TextMuted) },
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard,
                    focusedBorderColor = PurpleAccent,
                    unfocusedBorderColor = BorderSubtle,
                    focusedTextColor = TextWhite,
                    unfocusedTextColor = TextWhite
                ),
                singleLine = true
            )

            Spacer(modifier = Modifier.width(8.dp))

            // Sort menu button
            Box {
                IconButton(
                    onClick = { showSortMenu = true },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(SurfaceCard)
                ) {
                    Icon(Icons.Default.Sort, contentDescription = "Sort", tint = VioletNeon)
                }

                DropdownMenu(
                    expanded = showSortMenu,
                    onDismissRequest = { showSortMenu = false },
                    modifier = Modifier.background(SurfaceCard)
                ) {
                    DropdownMenuItem(
                        text = { Text("Sort by Title", color = TextWhite) },
                        onClick = { currentSort = SongSort.TITLE; showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Artist", color = TextWhite) },
                        onClick = { currentSort = SongSort.ARTIST; showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Recently Added", color = TextWhite) },
                        onClick = { currentSort = SongSort.DATE_ADDED; showSortMenu = false }
                    )
                    DropdownMenuItem(
                        text = { Text("Sort by Duration", color = TextWhite) },
                        onClick = { currentSort = SongSort.DURATION; showSortMenu = false }
                    )
                }
            }
        }

        // Quick filter pills with "Classify by Artist" mode
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(bottom = 6.dp)
        ) {
            val chips = listOf("All", "By Artist (Classified)", "Favorites", "FLAC/WAV")
            items(chips) { chip ->
                val isSelected = selectedFilter == chip
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedFilter = chip },
                    label = { Text(chip, color = if (isSelected) TextWhite else TextMuted, fontSize = 12.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = PurpleAccent,
                        containerColor = SurfaceCard
                    ),
                    border = null
                )
            }
        }

        if (selectedFilter == "By Artist (Classified)") {
            // CLASSIFICATION LIST BY ARTIST
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                songsByArtist.forEach { (artistName, artistTrackList) ->
                    item(key = "artist_header_$artistName") {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceGlow)
                                .padding(horizontal = 14.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurpleAccent.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Person, contentDescription = null, tint = GoldAccent, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = artistName,
                                    color = TextWhite,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${artistTrackList.size} ${if (artistTrackList.size == 1) "track" else "tracks"}",
                                    color = VioletNeon,
                                    fontSize = 11.sp
                                )
                            }
                            Button(
                                onClick = { onPlaySongList(artistTrackList, 0) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Icon(Icons.Default.PlayArrow, null, tint = TextWhite, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Play", fontSize = 12.sp)
                            }
                        }
                    }

                    itemsIndexed(artistTrackList, key = { _, s -> "${artistName}_${s.id}" }) { index, song ->
                        TrackListItem(
                            song = song,
                            isPlayingThis = song.id == currentPlayingSongId,
                            onClick = { onPlaySongList(artistTrackList, index) },
                            onToggleFavorite = { onToggleFavorite(song.id) },
                            onPlayNext = { onPlayNext(song) },
                            onAddToQueue = { onAddToQueue(song) },
                            onAddToPlaylist = { onAddToPlaylist(song) },
                            modifier = Modifier.padding(start = 12.dp)
                        )
                    }
                }
            }
        } else {
            // Standard Tracks List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                itemsIndexed(filteredSongs, key = { _, s -> s.id }) { index, song ->
                    TrackListItem(
                        song = song,
                        isPlayingThis = song.id == currentPlayingSongId,
                        onClick = { onPlaySongList(filteredSongs, index) },
                        onToggleFavorite = { onToggleFavorite(song.id) },
                        onPlayNext = { onPlayNext(song) },
                        onAddToQueue = { onAddToQueue(song) },
                        onAddToPlaylist = { onAddToPlaylist(song) }
                    )
                }
            }
        }
    }
}
