package com.aurummusic.player.data.repository

import android.content.Context
import android.net.Uri
import com.aurummusic.player.data.local.AurumDatabase
import com.aurummusic.player.data.local.MediaStoreScanner
import com.aurummusic.player.data.local.entity.FavoriteSongEntity
import com.aurummusic.player.data.local.entity.PlaylistEntity
import com.aurummusic.player.data.local.entity.PlaylistSongCrossRef
import com.aurummusic.player.data.model.Album
import com.aurummusic.player.data.model.Artist
import com.aurummusic.player.data.model.AudioFolder
import com.aurummusic.player.data.model.Playlist
import com.aurummusic.player.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter

class MusicRepository(
    private val context: Context,
    private val database: AurumDatabase,
    private val scanner: MediaStoreScanner = MediaStoreScanner(context)
) {
    private val scope = CoroutineScope(Dispatchers.IO)
    private val musicDao = database.musicDao()

    private val _rawSongs = MutableStateFlow<List<Song>>(emptyList())
    private val _isScanning = MutableStateFlow<Boolean>(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    val favoriteIds: Flow<List<Long>> = musicDao.getAllFavoriteIds()

    // Songs enriched with favorites flag
    val songs: StateFlow<List<Song>> = combine(_rawSongs, favoriteIds) { songs, favIds ->
        val favSet = favIds.toSet()
        songs.map { it.copy(isFavorite = favSet.contains(it.id)) }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val albums: StateFlow<List<Album>> = songs.map { songList ->
        scanner.groupAlbums(songList)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val artists: StateFlow<List<Artist>> = songs.map { songList ->
        scanner.groupArtists(songList)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val folders: StateFlow<List<AudioFolder>> = songs.map { songList ->
        scanner.groupFolders(songList)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val genres: StateFlow<List<Genre>> = songs.map { songList ->
        songList.groupBy { if (it.genre.isBlank() || it.genre == "<unknown>") "Hi-Fi & Acoustic" else it.genre }
            .mapIndexed { index, (genreName, genreSongs) ->
                com.aurummusic.player.data.model.Genre(id = index.toLong(), name = genreName, trackCount = genreSongs.size)
            }.sortedBy { it.name }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val favoriteSongs: StateFlow<List<Song>> = songs.map { list ->
        list.filter { it.isFavorite }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val recentlyPlayedSongs: StateFlow<List<Song>> = combine(
        songs,
        musicDao.getRecentlyPlayedSongIds(30)
    ) { allSongs, recentIds ->
        val map = allSongs.associateBy { it.id }
        recentIds.mapNotNull { map[it] }
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val recentlyAddedSongs: StateFlow<List<Song>> = songs.map { list ->
        list.sortedByDescending { it.dateAdded }.take(25)
    }.stateIn(scope, SharingStarted.Eagerly, emptyList())

    val playlists: Flow<List<Playlist>> = musicDao.getAllPlaylists().map { entities ->
        entities.map { entity ->
            Playlist(
                id = entity.id,
                name = entity.name,
                description = entity.description,
                createdAt = entity.createdAt
            )
        }
    }

    suspend fun refreshLibrary() = withContext(Dispatchers.IO) {
        _isScanning.value = true
        try {
            val scanned = scanner.scanSongs()
            _rawSongs.value = scanned
        } finally {
            _isScanning.value = false
        }
    }

    suspend fun toggleFavorite(songId: Long) = withContext(Dispatchers.IO) {
        val isFav = musicDao.isFavorite(songId)
        if (isFav) {
            musicDao.removeFavorite(songId)
        } else {
            musicDao.addFavorite(FavoriteSongEntity(songId = songId))
        }
    }

    suspend fun recordSongPlayed(songId: Long) = withContext(Dispatchers.IO) {
        musicDao.recordSongPlayed(songId)
    }

    suspend fun createPlaylist(name: String, description: String = ""): Long = withContext(Dispatchers.IO) {
        musicDao.insertPlaylist(
            PlaylistEntity(
                name = name.trim(),
                description = description.trim()
            )
        )
    }

    suspend fun deletePlaylist(playlistId: Long) = withContext(Dispatchers.IO) {
        musicDao.deletePlaylist(playlistId)
    }

    suspend fun addSongToPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        musicDao.addSongToPlaylist(
            PlaylistSongCrossRef(
                playlistId = playlistId,
                songId = songId
            )
        )
    }

    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long) = withContext(Dispatchers.IO) {
        musicDao.removeSongFromPlaylist(playlistId, songId)
    }

    fun getSongsForPlaylist(playlistId: Long): Flow<List<Song>> = combine(
        songs,
        musicDao.getSongIdsForPlaylist(playlistId)
    ) { allSongs, ids ->
        val map = allSongs.associateBy { it.id }
        ids.mapNotNull { map[it] }
    }

    // Export playlist to M3U
    suspend fun exportPlaylistToM3u(uri: Uri, playlistName: String, songs: List<Song>): Boolean =
        withContext(Dispatchers.IO) {
            try {
                context.contentResolver.openOutputStream(uri)?.use { os ->
                    OutputStreamWriter(os).use { writer ->
                        writer.write("#EXTM3U\n")
                        writer.write("#PLAYLIST:$playlistName\n")
                        for (song in songs) {
                            writer.write("#EXTINF:${song.durationMs / 1000},${song.artist} - ${song.title}\n")
                            writer.write("${song.dataUri}\n")
                        }
                    }
                }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }

    // Import playlist from M3U
    suspend fun importPlaylistFromM3u(uri: Uri, playlistName: String): Long =
        withContext(Dispatchers.IO) {
            try {
                val newPlaylistId = createPlaylist(playlistName, "Imported from M3U")
                context.contentResolver.openInputStream(uri)?.use { `is` ->
                    BufferedReader(InputStreamReader(`is`)).use { reader ->
                        var line = reader.readLine()
                        while (line != null) {
                            val trimmed = line.trim()
                            if (trimmed.isNotEmpty() && !trimmed.startsWith("#")) {
                                val found = _rawSongs.value.find { it.dataUri == trimmed || it.title.equals(trimmed, true) }
                                if (found != null) {
                                    addSongToPlaylist(newPlaylistId, found.id)
                                }
                            }
                            line = reader.readLine()
                        }
                    }
                }
                newPlaylistId
            } catch (e: Exception) {
                e.printStackTrace()
                -1L
            }
        }
}
