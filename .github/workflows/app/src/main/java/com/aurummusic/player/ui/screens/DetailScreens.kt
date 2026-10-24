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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shuffle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.aurummusic.player.data.model.Album
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.ui.components.TrackListItem
import com.aurummusic.player.ui.theme.BlackPrimary
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceGlow
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon

@Composable
fun AlbumDetailScreen(
    album: Album?,
    albumSongs: List<Song>,
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = album?.title ?: "Album Details",
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(SurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        if (album != null) {
                            AsyncImage(
                                model = album.artworkUri,
                                contentDescription = album.title,
                                modifier = Modifier.size(160.dp),
                                contentScale = ContentScale.Crop,
                                error = null
                            )
                        }
                        Icon(
                            imageVector = Icons.Default.Album,
                            contentDescription = null,
                            tint = PurpleAccent,
                            modifier = Modifier.size(54.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = album?.title ?: "",
                        color = TextWhite,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${album?.artist ?: ""} • ${albumSongs.size} tracks",
                        color = TextMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlaySongList(albumSongs, 0) },
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = TextWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play All", color = TextWhite)
                        }

                        Button(
                            onClick = onShuffleAll,
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlow)
                        ) {
                            Icon(Icons.Default.Shuffle, null, tint = VioletNeon)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Shuffle", color = VioletNeon)
                        }
                    }
                }
            }

            itemsIndexed(albumSongs, key = { _, s -> s.id }) { index, song ->
                TrackListItem(
                    song = song,
                    isPlayingThis = song.id == currentPlayingSongId,
                    onClick = { onPlaySongList(albumSongs, index) },
                    onToggleFavorite = { onToggleFavorite(song.id) },
                    onPlayNext = { onPlayNext(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onAddToPlaylist = { onAddToPlaylist(song) }
                )
            }
        }
    }
}

@Composable
fun PlaylistDetailScreen(
    title: String,
    description: String,
    songs: List<Song>,
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onExportM3u: () -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = TextWhite,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                if (description.isNotEmpty()) {
                    Text(text = description, color = TextMuted, fontSize = 12.sp)
                }
            }

            OutlinedButton(
                onClick = onExportM3u,
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = GoldAccent)
            ) {
                Icon(Icons.Default.FileDownload, null, tint = GoldAccent, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export")
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Button(
                        onClick = { onPlaySongList(songs, 0) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                    ) {
                        Icon(Icons.Default.PlayArrow, null, tint = TextWhite)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Play All", color = TextWhite)
                    }

                    Button(
                        onClick = onShuffleAll,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = SurfaceGlow)
                    ) {
                        Icon(Icons.Default.Shuffle, null, tint = VioletNeon)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Shuffle", color = VioletNeon)
                    }
                }
            }

            itemsIndexed(songs, key = { _, s -> s.id }) { index, song ->
                TrackListItem(
                    song = song,
                    isPlayingThis = song.id == currentPlayingSongId,
                    onClick = { onPlaySongList(songs, index) },
                    onToggleFavorite = { onToggleFavorite(song.id) },
                    onPlayNext = { onPlayNext(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onAddToPlaylist = { onAddToPlaylist(song) }
                )
            }
        }
    }
}

@Composable
fun ArtistDetailScreen(
    artistName: String,
    artistSongs: List<Song>,
    currentPlayingSongId: Long?,
    onPlaySongList: (List<Song>, Int) -> Unit,
    onShuffleAll: () -> Unit,
    onToggleFavorite: (Long) -> Unit,
    onPlayNext: (Song) -> Unit,
    onAddToQueue: (Song) -> Unit,
    onAddToPlaylist: (Song) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(BlackPrimary)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBackClick) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = TextWhite)
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = artistName,
                color = TextWhite,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 90.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(
                                androidx.compose.ui.graphics.Brush.sweepGradient(
                                    listOf(PurpleAccent, VioletNeon, PurpleAccent)
                                )
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(SurfaceCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = artistName,
                            tint = GoldAccent,
                            modifier = Modifier.size(60.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = artistName,
                        color = TextWhite,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${artistSongs.size} Tracks by this Artist",
                        color = TextMuted,
                        fontSize = 13.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { onPlaySongList(artistSongs, 0) },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent)
                        ) {
                            Icon(Icons.Default.PlayArrow, null, tint = TextWhite)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Play Artist", color = TextWhite, fontWeight = FontWeight.SemiBold)
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

            itemsIndexed(artistSongs, key = { _, s -> s.id }) { index, song ->
                TrackListItem(
                    song = song,
                    isPlayingThis = song.id == currentPlayingSongId,
                    onClick = { onPlaySongList(artistSongs, index) },
                    onToggleFavorite = { onToggleFavorite(song.id) },
                    onPlayNext = { onPlayNext(song) },
                    onAddToQueue = { onAddToQueue(song) },
                    onAddToPlaylist = { onAddToPlaylist(song) }
                )
            }
        }
    }
}

