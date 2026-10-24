package com.aurummusic.player.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.aurummusic.player.data.model.Song
import com.aurummusic.player.data.repository.MusicRepository
import com.aurummusic.player.playback.PlaybackManager
import com.aurummusic.player.ui.components.AurumTopAppBar
import com.aurummusic.player.ui.components.MiniPlayer
import com.aurummusic.player.ui.screens.AlbumDetailScreen
import com.aurummusic.player.ui.screens.AlbumsScreen
import com.aurummusic.player.ui.screens.EqualizerScreen
import com.aurummusic.player.ui.screens.FoldersScreen
import com.aurummusic.player.ui.screens.HomeScreen
import com.aurummusic.player.ui.screens.LibraryScreen
import com.aurummusic.player.ui.screens.NowPlayingScreen
import com.aurummusic.player.ui.screens.PlaylistDetailScreen
import com.aurummusic.player.ui.screens.PlaylistsScreen
import com.aurummusic.player.ui.screens.QueueScreen
import com.aurummusic.player.ui.screens.SearchScreen
import com.aurummusic.player.ui.screens.SettingsScreen
import com.aurummusic.player.ui.screens.SongsScreen
import com.aurummusic.player.ui.theme.BlackPrimary
import com.aurummusic.player.ui.theme.BorderSubtle
import com.aurummusic.player.ui.theme.GoldAccent
import com.aurummusic.player.ui.theme.PurpleAccent
import com.aurummusic.player.ui.theme.PurpleGlow
import com.aurummusic.player.ui.theme.SurfaceCard
import com.aurummusic.player.ui.theme.SurfaceDark
import com.aurummusic.player.ui.theme.TextMuted
import com.aurummusic.player.ui.theme.TextWhite
import com.aurummusic.player.ui.theme.VioletNeon
import kotlinx.coroutines.launch

@Composable
fun AurumNavHost(
    repository: MusicRepository,
    playbackManager: PlaybackManager,
    onImportM3uClick: () -> Unit,
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val coroutineScope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Repository flows
    val allSongs by repository.songs.collectAsState()
    val albums by repository.albums.collectAsState()
    val artists by repository.artists.collectAsState()
    val folders by repository.folders.collectAsState()
    val favoriteSongs by repository.favoriteSongs.collectAsState()
    val recentlyPlayed by repository.recentlyPlayedSongs.collectAsState()
    val recentlyAdded by repository.recentlyAddedSongs.collectAsState()
    val playlists by repository.playlists.collectAsState(initial = emptyList())

    // Playback flows
    val currentSong by playbackManager.currentSong.collectAsState()
    val isPlaying by playbackManager.isPlaying.collectAsState()
    val currentPosition by playbackManager.currentPosition.collectAsState()
    val duration by playbackManager.duration.collectAsState()
    val queue by playbackManager.queue.collectAsState()
    val currentIndex by playbackManager.currentIndex.collectAsState()
    val isShuffle by playbackManager.isShuffle.collectAsState()
    val repeatMode by playbackManager.repeatMode.collectAsState()
    val isSleepTimerActive by playbackManager.sleepTimerManager.isActive.collectAsState()
    val sleepTimerSeconds by playbackManager.sleepTimerManager.remainingSeconds.collectAsState()

    // Equalizer flows
    val eqEnabled by playbackManager.audioEffectManager.isEnabled.collectAsState()
    val eqBands by playbackManager.audioEffectManager.bands.collectAsState()
    val eqPreset by playbackManager.audioEffectManager.currentPreset.collectAsState()
    val bassBoost by playbackManager.audioEffectManager.bassBoostStrength.collectAsState()
    val virtualizer by playbackManager.audioEffectManager.virtualizerStrength.collectAsState()

    var songToAddToPlaylist by remember { mutableStateOf<Song?>(null) }
    var selectedAlbumId by remember { mutableStateOf<Long?>(null) }
    var selectedPlaylistId by remember { mutableStateOf<Long?>(null) }

    val isTopLevelRoute = currentRoute in Screen.bottomNavItems.map { it.route }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            if (isTopLevelRoute) {
                AurumTopAppBar(
                    title = "Aurum Music",
                    onSearchClick = { navController.navigate(Screen.Search.route) },
                    onEqualizerClick = { navController.navigate(Screen.Equalizer.route) },
                    onSettingsClick = { navController.navigate(Screen.Settings.route) }
                )
            }
        },
        bottomBar = {
            if (isTopLevelRoute) {
                Column(modifier = Modifier.background(SurfaceDark)) {
                    // Mini player positioned above the bottom bar
                    if (currentSong != null) {
                        MiniPlayer(
                            song = currentSong,
                            isPlaying = isPlaying,
                            currentPosition = currentPosition,
                            duration = duration,
                            onPlayPause = { playbackManager.togglePlayPause() },
                            onSkipNext = { playbackManager.skipToNext() },
                            onClick = { navController.navigate(Screen.NowPlaying.route) }
                        )
                    }

                    // Bottom Navigation Bar
                    NavigationBar(
                        containerColor = SurfaceDark,
                        tonalElevation = 8.dp
                    ) {
                        Screen.bottomNavItems.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = selected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    screen.icon?.let {
                                        Icon(
                                            imageVector = it,
                                            contentDescription = screen.title,
                                            tint = if (selected) GoldAccent else TextMuted
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 11.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) TextWhite else TextMuted
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = PurpleGlow.copy(alpha = 0.25f)
                                )
                            )
                        }
                    }
                }
            }
        },
        containerColor = BlackPrimary
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    allSongs = allSongs,
                    recentlyPlayed = recentlyPlayed,
                    favoriteSongs = favoriteSongs,
                    albums = albums,
                    artists = artists,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(allSongs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onAlbumClick = { id ->
                        selectedAlbumId = id
                        navController.navigate("album_detail/$id")
                    },
                    onArtistClick = { name ->
                        navController.navigate("artist_detail/$name")
                    },
                    playlists = playlists,
                    onPlaylistClick = { id ->
                        selectedPlaylistId = id
                        navController.navigate("playlist_detail/$id")
                    }
                )
            }

            composable(Screen.Songs.route) {
                SongsScreen(
                    songs = allSongs,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(allSongs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it }
                )
            }

            composable(Screen.Library.route) {
                LibraryScreen(
                    allSongs = allSongs,
                    albums = albums,
                    artists = artists,
                    playlists = playlists,
                    favoriteSongs = favoriteSongs,
                    recentlyPlayed = recentlyPlayed,
                    recentlyAdded = recentlyAdded,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(allSongs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onAlbumClick = { id ->
                        selectedAlbumId = id
                        navController.navigate("album_detail/$id")
                    },
                    onArtistClick = { name ->
                        navController.navigate("artist_detail/$name")
                    },
                    onPlaylistClick = { id ->
                        selectedPlaylistId = id
                        navController.navigate("playlist_detail/$id")
                    },
                    onCreatePlaylist = { name, desc ->
                        coroutineScope.launch { repository.createPlaylist(name, desc) }
                    },
                    onImportM3uClick = onImportM3uClick
                )
            }

            composable(Screen.Albums.route) {
                AlbumsScreen(
                    albums = albums,
                    onAlbumClick = { id ->
                        selectedAlbumId = id
                        navController.navigate("album_detail/$id")
                    }
                )
            }

            composable(Screen.Folders.route) {
                FoldersScreen(
                    folders = folders,
                    onFolderClick = { folder ->
                        playbackManager.playSongList(folder.songs, 0)
                    }
                )
            }

            composable(Screen.Playlists.route) {
                PlaylistsScreen(
                    playlists = playlists,
                    onPlaylistClick = { id ->
                        selectedPlaylistId = id
                        navController.navigate("playlist_detail/$id")
                    },
                    onCreatePlaylist = { name, desc ->
                        coroutineScope.launch { repository.createPlaylist(name, desc) }
                    },
                    onImportM3uClick = onImportM3uClick
                )
            }

            composable(Screen.NowPlaying.route) {
                NowPlayingScreen(
                    song = currentSong,
                    isPlaying = isPlaying,
                    currentPosition = currentPosition,
                    duration = duration,
                    isShuffle = isShuffle,
                    repeatMode = repeatMode,
                    isSleepTimerActive = isSleepTimerActive,
                    sleepTimerRemaining = playbackManager.sleepTimerManager.formattedRemainingTime,
                    onBackClick = { navController.popBackStack() },
                    onPlayPause = { playbackManager.togglePlayPause() },
                    onSkipNext = { playbackManager.skipToNext() },
                    onSkipPrevious = { playbackManager.skipToPrevious() },
                    onSeekTo = { playbackManager.seekTo(it) },
                    onToggleShuffle = { playbackManager.toggleShuffle() },
                    onToggleRepeat = { playbackManager.toggleRepeatMode() },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onOpenEqualizer = { navController.navigate(Screen.Equalizer.route) },
                    onOpenQueue = { navController.navigate(Screen.Queue.route) },
                    onStartSleepTimer = { mins -> playbackManager.sleepTimerManager.startTimer(mins) },
                    onCancelSleepTimer = { playbackManager.sleepTimerManager.cancelTimer() }
                )
            }

            composable(Screen.Equalizer.route) {
                EqualizerScreen(
                    audioEffectManager = playbackManager.audioEffectManager,
                    isEnabled = eqEnabled,
                    bands = eqBands,
                    currentPreset = eqPreset,
                    bassBoostStrength = bassBoost,
                    virtualizerStrength = virtualizer,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Queue.route) {
                QueueScreen(
                    queue = queue,
                    currentIndex = currentIndex,
                    onTrackClick = { idx -> playbackManager.playTrackAtIndex(idx) },
                    onRemoveFromQueue = { idx -> playbackManager.removeFromQueue(idx) },
                    onClearQueue = { playbackManager.clearQueue() },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Search.route) {
                SearchScreen(
                    allSongs = allSongs,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable(Screen.Settings.route) {
                SettingsScreen(
                    onRescanLibrary = { coroutineScope.launch { repository.refreshLibrary() } },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("album_detail/{albumId}") { backStackEntry ->
                val albumId = backStackEntry.arguments?.getString("albumId")?.toLongOrNull() ?: selectedAlbumId
                val album = albums.find { it.id == albumId }
                val albumSongs = allSongs.filter { it.albumId == albumId }
                AlbumDetailScreen(
                    album = album,
                    albumSongs = albumSongs,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(albumSongs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("playlist_detail/{playlistId}") { backStackEntry ->
                val id = backStackEntry.arguments?.getString("playlistId")?.toLongOrNull() ?: selectedPlaylistId ?: -1L
                val (title, desc, songs) = when (id) {
                    -1L -> Triple("Favorites", "Your starred tracks", favoriteSongs)
                    -2L -> Triple("Recently Played", "Recently played tracks", recentlyPlayed)
                    -3L -> Triple("Recently Added", "Freshly added music", recentlyAdded)
                    else -> {
                        val pl = playlists.find { it.id == id }
                        Triple(pl?.name ?: "Playlist", pl?.description ?: "", allSongs.filter { it.id % 2L == 0L })
                    }
                }
                PlaylistDetailScreen(
                    title = title,
                    description = desc,
                    songs = songs,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(songs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onExportM3u = { /* Export action */ },
            composable("artist_detail/{artistName}") { backStackEntry ->
                val artistName = backStackEntry.arguments?.getString("artistName") ?: ""
                val artistSongs = allSongs.filter { it.artist.equals(artistName, ignoreCase = true) }
                com.aurummusic.player.ui.screens.ArtistDetailScreen(
                    artistName = artistName,
                    artistSongs = artistSongs,
                    currentPlayingSongId = currentSong?.id,
                    onPlaySongList = { list, idx -> playbackManager.playSongList(list, idx) },
                    onShuffleAll = {
                        playbackManager.toggleShuffle()
                        playbackManager.playSongList(artistSongs, 0)
                    },
                    onToggleFavorite = { coroutineScope.launch { repository.toggleFavorite(it) } },
                    onPlayNext = { playbackManager.playNext(it) },
                    onAddToQueue = { playbackManager.addToQueue(it) },
                    onAddToPlaylist = { songToAddToPlaylist = it },
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }

    // Add to playlist modal dialog
    if (songToAddToPlaylist != null) {
        val song = songToAddToPlaylist!!
        AlertDialog(
            onDismissRequest = { songToAddToPlaylist = null },
            title = { Text("Add to Playlist", color = TextWhite, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    Text(
                        text = "Select a playlist for \"${song.title}\":",
                        color = TextMuted,
                        fontSize = 13.sp,
                        modifier = Modifier.padding(bottom = 12.dp)
                    )
                    playlists.forEach { pl ->
                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    repository.addSongToPlaylist(pl.id, song.id)
                                    songToAddToPlaylist = null
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = SurfaceCard)
                        ) {
                            Text(pl.name, color = TextWhite)
                        }
                    }
                    if (playlists.isEmpty()) {
                        Text("No custom playlists created yet. Create one in the Playlists tab.", color = TextMuted, fontSize = 12.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { songToAddToPlaylist = null }) {
                    Text("Cancel", color = TextMuted)
                }
            },
            containerColor = SurfaceDark
        )
    }
}
