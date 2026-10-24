package com.aurummusic.player

import android.app.Application
import com.aurummusic.player.data.local.AurumDatabase
import com.aurummusic.player.data.repository.MusicRepository
import com.aurummusic.player.playback.PlaybackManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AurumApplication : Application() {

    lateinit var database: AurumDatabase
        private set

    lateinit var repository: MusicRepository
        private set

    lateinit var playbackManager: PlaybackManager
        private set

    private val applicationScope = CoroutineScope(Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        database = AurumDatabase.getDatabase(this)
        repository = MusicRepository(this, database)
        playbackManager = PlaybackManager.getInstance(this)

        // Connect playback history tracking
        playbackManager.onSongPlayed = { songId ->
            applicationScope.launch {
                repository.recordSongPlayed(songId)
            }
        }
    }
}
