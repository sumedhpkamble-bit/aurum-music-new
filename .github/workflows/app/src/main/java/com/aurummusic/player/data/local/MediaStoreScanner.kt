package com.aurummusic.player.data.local

import android.content.ContentUris
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import com.aurummusic.player.data.model.Album
import com.aurummusic.player.data.model.Artist
import com.aurummusic.player.data.model.AudioFolder
import com.aurummusic.player.data.model.Song
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class MediaStoreScanner(private val context: Context) {

    suspend fun scanSongs(): List<Song> = withContext(Dispatchers.IO) {
        val songsList = mutableListOf<Song>()
        val collection = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ARTIST_ID,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.YEAR,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.MIME_TYPE
        )

        // Select valid music longer than 5 seconds
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0 AND ${MediaStore.Audio.Media.DURATION} >= 5000"
        val sortOrder = "${MediaStore.Audio.Media.TITLE} ASC"

        try {
            context.contentResolver.query(
                collection,
                projection,
                selection,
                null,
                sortOrder
            )?.use { cursor ->
                val idCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
                val titleCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
                val artistCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
                val artistIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST_ID)
                val albumCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM)
                val albumIdCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ID)
                val durationCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
                val dataCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA)
                val trackCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TRACK)
                val yearCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.YEAR)
                val dateAddedCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATE_ADDED)
                val sizeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.SIZE)
                val mimeCol = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.MIME_TYPE)

                while (cursor.moveToNext()) {
                    val id = cursor.getLong(idCol)
                    val title = cursor.getString(titleCol) ?: "Unknown Track"
                    val artist = cursor.getString(artistCol) ?: "Unknown Artist"
                    val artistId = cursor.getLong(artistIdCol)
                    val album = cursor.getString(albumCol) ?: "Unknown Album"
                    val albumId = cursor.getLong(albumIdCol)
                    val duration = cursor.getLong(durationCol)
                    val data = cursor.getString(dataCol) ?: ""
                    val track = cursor.getInt(trackCol)
                    val year = cursor.getInt(yearCol)
                    val dateAdded = cursor.getLong(dateAddedCol)
                    val size = cursor.getLong(sizeCol)
                    val mimeType = cursor.getString(mimeCol) ?: "audio/mpeg"

                    val contentUri = ContentUris.withAppendedId(
                        MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                        id
                    )

                    val folderPath = if (data.isNotEmpty()) {
                        File(data).parent ?: ""
                    } else ""

                    songsList.add(
                        Song(
                            id = id,
                            title = title,
                            artist = if (artist == "<unknown>") "Unknown Artist" else artist,
                            artistId = artistId,
                            album = if (album == "<unknown>") "Unknown Album" else album,
                            albumId = albumId,
                            durationMs = duration,
                            dataUri = data,
                            contentUri = contentUri,
                            trackNumber = track,
                            year = year,
                            dateAdded = dateAdded,
                            sizeBytes = size,
                            mimeType = mimeType,
                            folderPath = folderPath
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        songsList
    }

    fun groupAlbums(songs: List<Song>): List<Album> {
        return songs.groupBy { it.albumId }.map { (albumId, songGroup) ->
            val first = songGroup.first()
            Album(
                id = albumId,
                title = first.album,
                artist = first.artist,
                artistId = first.artistId,
                songCount = songGroup.size,
                year = songGroup.map { it.year }.maxOrNull() ?: 0
            )
        }.sortedBy { it.title.lowercase() }
    }

    fun groupArtists(songs: List<Song>): List<Artist> {
        return songs.groupBy { it.artist }.map { (artistName, songGroup) ->
            val first = songGroup.first()
            val uniqueAlbums = songGroup.map { it.albumId }.toSet().size
            Artist(
                id = first.artistId,
                name = artistName,
                trackCount = songGroup.size,
                albumCount = uniqueAlbums
            )
        }.sortedBy { it.name.lowercase() }
    }

    fun groupFolders(songs: List<Song>): List<AudioFolder> {
        return songs.filter { it.folderPath.isNotEmpty() }
            .groupBy { it.folderPath }
            .map { (path, folderSongs) ->
                val name = File(path).name.ifEmpty { "Root" }
                AudioFolder(
                    path = path,
                    name = name,
                    songCount = folderSongs.size,
                    songs = folderSongs
                )
            }.sortedBy { it.name.lowercase() }
    }
}
