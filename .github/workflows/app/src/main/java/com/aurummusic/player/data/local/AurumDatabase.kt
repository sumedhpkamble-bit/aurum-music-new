package com.aurummusic.player.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.aurummusic.player.data.local.dao.MusicDao
import com.aurummusic.player.data.local.entity.FavoriteSongEntity
import com.aurummusic.player.data.local.entity.PlaybackHistoryEntity
import com.aurummusic.player.data.local.entity.PlaylistEntity
import com.aurummusic.player.data.local.entity.PlaylistSongCrossRef

@Database(
    entities = [
        FavoriteSongEntity::class,
        PlaylistEntity::class,
        PlaylistSongCrossRef::class,
        PlaybackHistoryEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AurumDatabase : RoomDatabase() {
    abstract fun musicDao(): MusicDao

    companion object {
        @Volatile
        private var INSTANCE: AurumDatabase? = null

        fun getDatabase(context: Context): AurumDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AurumDatabase::class.java,
                    "aurum_music.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
