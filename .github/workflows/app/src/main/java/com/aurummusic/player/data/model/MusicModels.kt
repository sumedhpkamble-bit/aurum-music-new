package com.aurummusic.player.data.model

import android.net.Uri

data class Song(
    val id: Long,
    val title: String,
    val artist: String,
    val artistId: Long = 0L,
    val album: String,
    val albumId: Long = 0L,
    val durationMs: Long,
    val dataUri: String,
    val contentUri: Uri,
    val trackNumber: Int = 0,
    val year: Int = 0,
    val genre: String = "Unknown",
    val dateAdded: Long = 0L,
    val sizeBytes: Long = 0L,
    val mimeType: String = "audio/mpeg",
    val isFavorite: Boolean = false,
    val folderPath: String = ""
) {
    val formattedDuration: String
        get() {
            val totalSeconds = durationMs / 1000
            val minutes = totalSeconds / 60
            val seconds = totalSeconds % 60
            return "%d:%02d".format(minutes, seconds)
        }

    val artworkUri: Uri
        get() = Uri.parse("content://media/external/audio/albumart/$albumId")
}

data class Album(
    val id: Long,
    val title: String,
    val artist: String,
    val artistId: Long = 0L,
    val songCount: Int = 0,
    val year: Int = 0,
    val artworkUri: Uri = Uri.parse("content://media/external/audio/albumart/$id")
)

data class Artist(
    val id: Long,
    val name: String,
    val trackCount: Int = 0,
    val albumCount: Int = 0
)

data class AudioFolder(
    val path: String,
    val name: String,
    val songCount: Int = 0,
    val songs: List<Song> = emptyList()
)

data class Playlist(
    val id: Long,
    val name: String,
    val description: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val songCount: Int = 0
)

data class Genre(
    val id: Long,
    val name: String,
    val trackCount: Int = 0
)

enum class RepeatMode {
    OFF,
    ALL,
    ONE
}

data class EqualizerBand(
    val index: Short,
    val centerFreqHz: Int,
    val minLevelMb: Short,
    val maxLevelMb: Short,
    val currentLevelMb: Short
)

data class EqualizerPreset(
    val name: String,
    val bandLevels: List<Short>,
    val bassBoost: Short = 0,
    val virtualizer: Short = 0
)
