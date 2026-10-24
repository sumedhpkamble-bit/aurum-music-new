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
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.aurummusic.player.R
import com.aurummusic.player.data.model.Album
import com.aurummusic.player.data.model.Artist
import com.aurummusic.player.data.model.Genre
import com.aurummusic.player.data.model.Playlist
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.components.AlbumCardItem
import com.aurummusic.player.ui.components.ArtistCardItem
import com.aurummusic.player.ui.components.PlaylistCardItem
import com.aurummusic.player.ui.components.TrackListItem
import com.aurummusic.player.ui.theme.BlackPrimary
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.PurpleDeep
import com.aurummusic.player.ui.theme.PurpleGlow
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceGlow
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon

enum class LibraryTab {
    PLAYLISTS,
    GENRE,
    ARTIST
}

@Composable
fun HomeScreen(
    allSongs: List<Song>,
    recentlyPlayed: List<Song>,
    favoriteSongs: List<Song>,
    albums: List<Album>,
    artists: List<Artist>,
    playlists: List<Playlist> = emptyList(),
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onAlbumClick: (Long) -> Unit,
    onArtistClick: (String) -> Unit,
    onPlaylistClick: (Long) -> Unit = {},
    modifier: Modifier = Modifier
) {
    var selectedLibraryTab by remember { mutableStateOf(LibraryTab.PLAYLISTS) }

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
        // Hero quick actions & Library stats with the classic vector logo
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(ColorCardStart, ColorCardEnd)
                        )
                    )
                    .padding(18.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                    // Aurum Vector Logo (Headphones + Golden Music Note + Equalizer)
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = R.drawable.ic_aurum_logo),
                        contentDescription = "Aurum Music Logo",
                        modifier = Modifier
                            .size(72.dp)
                            .padding(bottom = 8.dp),
                        contentScale = ContentScale.Fit
                    )

                    Text(
                        text = "AURUM MUSIC",
                        color = GoldAccent,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )

                    Text(
                        text = "Your Local Hi-Fi Sanctuary",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${allSongs.size} Tracks • ${albums.size} Albums • ${artists.size} Artists",
                        color = TextMuted,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlaySongList(allSongs, 0) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = TextWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play All", color = TextWhite, fontWeight = FontWeight.SemiBold)
                        }

                        Button(
                            onClick = onShuffleAll,
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlow)
                        ) {
                            Icon(Icons.Default.Shuffle, null, tint = VioletNeon)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle", color = VioletNeon, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Recently Played
        if (recentlyPlayed.isNotEmpty()) {
            item {
                SectionHeader("Recently Played")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    itemsIndexed(recentlyPlayed.take(8)) { index, song ->
                        RecentSongCard(
                            song = song,
                            onClick = { onPlaySongList(recentlyPlayed, index) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // Albums Carousel
        if (albums.isNotEmpty()) {
            item {
                SectionHeader("Albums")
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(albums.take(10)) { album ->
                        AlbumCardItem(
                            album = album,
                            onClick = { onAlbumClick(album.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        // NEW: LIBRARY HUB (Classified by Playlists, Genre, and Artist)
        item {
            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Library",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Classified Audio",
                        color = VioletNeon,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Library Filter Chips: Playlists, Genre, Artist
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    FilterChip(
                        selected = selectedLibraryTab == LibraryTab.PLAYLISTS,
                        onClick = { selectedLibraryTab = LibraryTab.PLAYLISTS },
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
                        selected = selectedLibraryTab == LibraryTab.GENRE,
                        onClick = { selectedLibraryTab = LibraryTab.GENRE },
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
                        selected = selectedLibraryTab == LibraryTab.ARTIST,
                        onClick = { selectedLibraryTab = LibraryTab.ARTIST },
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

                Spacer(modifier = Modifier.height(10.dp))
            }
        }

        // Sub-content based on selected Library Tab:
        when (selectedLibraryTab) {
            LibraryTab.PLAYLISTS -> {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Smart playlist entries
                        PlaylistCardItem(
                            playlist = Playlist(id = -1, name = "Favorites", description = "Your starred tracks (${favoriteSongs.size} songs)"),
                            onClick = { onPlaylistClick(-1) }
                        )
                        PlaylistCardItem(
                            playlist = Playlist(id = -2, name = "Recently Played", description = "Playback history (${recentlyPlayed.size} songs)"),
                            onClick = { onPlaylistClick(-2) }
                        )
                        playlists.forEach { pl ->
                            PlaylistCardItem(
                                playlist = pl,
                                onClick = { onPlaylistClick(pl.id) }
                            )
                        }
                    }
                }
            }

            LibraryTab.GENRE -> {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        songsByGenre.forEach { (genreName, genreTracks) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .clickable { onPlaySongList(genreTracks, 0) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(SurfaceGlow),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Category, null, tint = GoldAccent, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = genreName,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${genreTracks.size} tracks",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { onPlaySongList(genreTracks, 0) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, tint = TextWhite, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }

            LibraryTab.ARTIST -> {
                item {
                    Column(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        songsByArtist.forEach { (artistName, artistTracks) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(SurfaceCard)
                                    .clickable { onArtistClick(artistName) }
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(42.dp)
                                        .clip(CircleShape)
                                        .background(PurpleAccent.copy(alpha = 0.3f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Person, null, tint = VioletNeon, modifier = Modifier.size(22.dp))
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = artistName,
                                        color = TextWhite,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                    Text(
                                        text = "${artistTracks.size} tracks",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                                Button(
                                    onClick = { onPlaySongList(artistTracks, 0) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, null, tint = TextWhite, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Play", fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        color = TextWhite,
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
    )
}

@Composable
private fun RecentSongCard(
    song: Song,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .width(200.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SurfaceCard)
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SurfaceGlow),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Default.PlayArrow, null, tint = GoldAccent, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = TextWhite,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )
            Text(
                text = song.artist,
                color = TextMuted,
                fontSize = 11.sp,
                maxLines = 1
            )
        }
    }
}

private val ColorCardStart = androidx.compose.ui.graphics.Color(0xFF23143E)
private val ColorCardEnd = androidx.compose.ui.graphics.Color(0xFF130A24)
