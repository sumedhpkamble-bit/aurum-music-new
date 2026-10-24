package com.aurummusic.player.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.aurummusic.player.data.local.entity.FavoriteSongEntity
import com.aurummusic.player.data.local.entity.PlaybackHistoryEntity
import com.aurummusic.player.data.local.entity.PlaylistEntity
import com.aurummusic.player.data.local.entity.PlaylistSongCrossRef
import kotlinx.coroutines.flow.Flow

@Dao
interface MusicDao {

    // --- Favorites ---
    @Query("SELECT songId FROM favorites ORDER BY addedAt DESC")
    fun getAllFavoriteIds(): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favorite: FavoriteSongEntity)

    @Query("DELETE FROM favorites WHERE songId = :songId")
    suspend fun removeFavorite(songId: Long)

    @Query("SELECT EXISTS(SELECT 1 FROM favorites WHERE songId = :songId)")
    suspend fun isFavorite(songId: Long): Boolean

    // --- Playlists ---
    @Query("SELECT * FROM playlists ORDER BY name ASC")
    fun getAllPlaylists(): Flow<List<PlaylistEntity>>

    @Query("SELECT * FROM playlists WHERE id = :playlistId LIMIT 1")
    suspend fun getPlaylistById(playlistId: Long): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlaylist(playlist: PlaylistEntity): Long

    @Query("DELETE FROM playlists WHERE id = :playlistId")
    suspend fun deletePlaylist(playlistId: Long)

    @Query("UPDATE playlists SET name = :name, description = :description WHERE id = :id")
    suspend fun updatePlaylist(id: Long, name: String, description: String)

    // --- Playlist Songs ---
    @Query("SELECT songId FROM playlist_songs WHERE playlistId = :playlistId ORDER BY orderIndex ASC")
    fun getSongIdsForPlaylist(playlistId: Long): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addSongToPlaylist(crossRef: PlaylistSongCrossRef)

    @Query("DELETE FROM playlist_songs WHERE playlistId = :playlistId AND songId = :songId")
    suspend fun removeSongFromPlaylist(playlistId: Long, songId: Long)

    @Query("SELECT COUNT(*) FROM playlist_songs WHERE playlistId = :playlistId")
    fun getSongCountForPlaylist(playlistId: Long): Flow<Int>

    // --- Playback History ---
    @Query("SELECT songId FROM playback_history ORDER BY lastPlayedAt DESC LIMIT :limit")
    fun getRecentlyPlayedSongIds(limit: Int = 30): Flow<List<Long>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun recordPlayback(history: PlaybackHistoryEntity)

    @Transaction
    suspend fun recordSongPlayed(songId: Long) {
        val existing = getHistoryItem(songId)
        val playCount = (existing?.playCount ?: 0) + 1
        recordPlayback(PlaybackHistoryEntity(songId = songId, lastPlayedAt = System.currentTimeMillis(), playCount = playCount))
    }

    @Query("SELECT * FROM playback_history WHERE songId = :songId LIMIT 1")
    suspend fun getHistoryItem(songId: Long): PlaybackHistoryEntity?

    @Query("SELECT songId FROM playback_history ORDER BY playCount DESC LIMIT :limit")
    fun getMostPlayedSongIds(limit: Int = 25): Flow<List<Long>>
}
