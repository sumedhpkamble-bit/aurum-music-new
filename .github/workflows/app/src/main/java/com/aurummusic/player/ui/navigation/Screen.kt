package com.aurummusic.player.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Album
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.QueueMusic
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String = "", val icon: ImageVector? = null) {
    object Home : Screen("home", "Home", Icons.Default.Home)
    object Songs : Screen("songs", "Songs", Icons.Default.MusicNote)
    object Library : Screen("library", "Library", Icons.Default.QueueMusic)
    object Search : Screen("search", "Search", Icons.Default.Search)
    object Equalizer : Screen("equalizer", "Equalizer", Icons.Default.Tune)

    object Albums : Screen("albums", "Albums", Icons.Default.Album)
    object Folders : Screen("folders", "Folders", Icons.Default.Folder)
    object Playlists : Screen("playlists", "Playlists", Icons.Default.QueueMusic)

    object NowPlaying : Screen("now_playing", "Now Playing")
    object Queue : Screen("queue", "Playing Queue")
    object Settings : Screen("settings", "Settings")

    object PlaylistDetail : Screen("playlist_detail/{playlistId}") {
        fun createRoute(playlistId: Long) = "playlist_detail/$playlistId"
    }

    object AlbumDetail : Screen("album_detail/{albumId}") {
        fun createRoute(albumId: Long) = "album_detail/$albumId"
    }

    object ArtistDetail : Screen("artist_detail/{artistName}") {
        fun createRoute(artistName: String) = "artist_detail/$artistName"
    }

    companion object {
        val bottomNavItems = listOf(Home, Songs, Library, Search, Equalizer)
    }
}
