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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import com.aurummusic.player.data.model.Album
import com.aurummusic.player.data.model.Artist
import com.aurummusic.player.data.model.Playlist
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.components.PlaylistCardItem
import com.aurummusic.player.ui.components.TrackListItem
import com.aurummusic.player.ui.theme.BlackPrimary
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.PurpleGlow
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceDark
import com.aurummusic.player.ui.theme.SurfaceGlow
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon

enum class LibraryClassification {
    PLAYLISTS,
    GENRE,
    ARTIST
}

@Composable
fun LibraryScreen(
    allSongs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<Playlist>,
    favoriteSongs: List<Song>,
    recentlyPlayed: List<Song>,
    recentlyAdded: List<Song>,
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (Long) -> Unit,
    onCreatePlaylist: (String, String) -> Unit,
    onImportM3uClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableStateOf(LibraryClassification.PLAYLISTS) }

    // Group songs by genre
    val songsByGenre = remember(allSongs) {
        allSongs.groupBy {
            if (it.genre.isBlank() || it.genre == "<unknown>") "Hi-Fi & Acoustic" else it.genre
        }
    }

    // Group songs by artist
    val songsByArtist = remember(allSongs) {
        allSongs.groupBy { it.artist }.toSortedMap(String.CASE_INSENSITIVE_ORDER)
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        // Library Screen Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Library",
                    color = TextWhite,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Classified by Playlists, Genre and Artist",
                    color = VioletNeon,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(14.dp))

                // 3 Classification Filter Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = selectedTab == LibraryClassification.PLAYLISTS,
                        onClick = { selectedTab = LibraryClassification.PLAYLISTS },
                        label = { Text("Playlists (${playlists.size + 3})", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.QueueMusic, null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleAccent,
                            selectedLabelColor = TextWhite,
                            containerColor = SurfaceCard,
                            labelColor = TextMuted
                        ),
                        border = null
                    )

                    FilterChip(
                        selected = selectedTab == LibraryClassification.GENRE,
                        onClick = { selectedTab = LibraryClassification.GENRE },
                        label = { Text("Genre (${songsByGenre.size})", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Category, null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleAccent,
                            selectedLabelColor = TextWhite,
                            containerColor = SurfaceCard,
                            labelColor = TextMuted
                        ),
                        border = null
                    )

                    FilterChip(
                        selected = selectedTab == LibraryClassification.ARTIST,
                        onClick = { selectedTab = LibraryClassification.ARTIST },
                        label = { Text("Artist (${artists.size})", fontSize = 12.sp) },
                        leadingIcon = { Icon(Icons.Default.Person, null, modifier = Modifier.size(14.dp)) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = PurpleAccent,
                            selectedLabelColor = TextWhite,
                            containerColor = SurfaceCard,
                            labelColor = TextMuted
                        ),
                        border = null
                    )
                }
            }
        }

        // Sub-content based on selected tab:
        when (selectedTab) {
            LibraryClassification.PLAYLISTS -> {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Action buttons: New Playlist and Import M3U
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { onCreatePlaylist("My New Mix", "Created from Library") },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                            ) {
                                Icon(Icons.Default.Add, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("New Playlist", fontSize = 12.sp)
                            }

                            OutlinedButton(
                                onClick = onImportM3uClick,
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = VioletNeon)
                            ) {
                                Icon(Icons.Default.FileUpload, null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Import M3U", fontSize = 12.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        // Smart playlist entries
                        PlaylistCardItem(
                            playlist = Playlist(id = -1, name = "Favorites", description = "Your starred tracks (${favoriteSongs.size} songs)"),
                            onClick = { onPlaylistClick(-1) }
                        )

                        PlaylistCardItem(
                            playlist = Playlist(id = -2, name = "Recently Played", description = "Latest session (${recentlyPlayed.size} songs)"),
                            onClick = { onPlaylistClick(-2) }
                        )

                        PlaylistCardItem(
                            playlist = Playlist(id = -3, name = "Recently Added", description = "Newest tracks in library (${recentlyAdded.size} songs)"),
                            onClick = { onPlaylistClick(-3) }
                        )

                        // User created playlists
                        playlists.forEach { playlist ->
                            PlaylistCardItem(
                                playlist = playlist,
                                onClick = { onPlaylistClick(playlist.id) }
                            )
                        }
                    }
                }
            }

            LibraryClassification.GENRE -> {
                songsByGenre.forEach { (genreName, genreSongList) ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceCard)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = genreName,
                                        color = TextWhite,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${genreSongList.size} ${if (genreSongList.size == 1) "track" else "tracks"}",
                                        color = VioletNeon,
                                        fontSize = 11.sp
                                    )
                                }

                                Button(
                                    onClick = { onPlaySongList(genreSongList, 0) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play Genre", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mini preview of tracks in this genre
                            genreSongList.take(3).forEachIndexed { index, song ->
                                TrackListItem(
                                    song = song,
                                    isPlaying = song.id == currentPlayingSongId,
                                    onClick = { onPlaySongList(genreSongList, index) },
                                    onFavoriteClick = { onToggleFavorite(song.id) },
                                    onMoreClick = { onAddToPlaylist(song) },
                                    showDuration = true
                                )
                            }
                        }
                    }
                }
            }

            LibraryClassification.ARTIST -> {
                songsByArtist.forEach { (artistName, artistSongList) ->
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 6.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(SurfaceCard)
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.clickable { onArtistClick(artistName) }
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(PurpleGlow),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = GoldAccent,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = artistName,
                                            color = TextWhite,
                                            fontSize = 15.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${artistSongList.size} ${if (artistSongList.size == 1) "track" else "tracks"}",
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    }
                                }

                                Button(
                                    onClick = { onPlaySongList(artistSongList, 0) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play All", fontSize = 11.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // Mini preview of tracks
                            artistSongList.take(3).forEachIndexed { index, song ->
                                TrackListItem(
                                    song = song,
                                    isPlaying = song.id == currentPlayingSongId,
                                    onClick = { onPlaySongList(artistSongList, index) },
                                    onFavoriteClick = { onToggleFavorite(song.id) },
                                    onMoreClick = { onAddToPlaylist(song) },
                                    showDuration = true
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
