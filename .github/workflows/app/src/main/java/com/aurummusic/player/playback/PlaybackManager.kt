package com.aurummusic.player.playback

import android.content.Context
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.aurummusic.player.data.model.RepeatMode
import com.aurummusic.player.data.model.Song
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class PlaybackManager private constructor(private val context: Context) {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var progressJob: Job? = null

    val exoPlayer: ExoPlayer by lazy {
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(C.USAGE_MEDIA)
            .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
            .build()

        ExoPlayer.Builder(context)
            .setAudioAttributes(audioAttributes, true)
            .setHandleAudioBecomingNoisy(true)
            .setWakeMode(C.WAKE_MODE_LOCAL)
            .build().apply {
                addListener(playerListener)
            }
    }

    val audioEffectManager = AudioEffectManager()
    val sleepTimerManager = SleepTimerManager {
        pause()
    }

    private val _currentSong = MutableStateFlow<Song?>(null)
    val currentSong: StateFlow<Song?> = _currentSong.asStateFlow()

    private val _isPlaying = MutableStateFlow(false)
    val isPlaying: StateFlow<Boolean> = _isPlaying.asStateFlow()

    private val _currentPosition = MutableStateFlow(0L)
    val currentPosition: StateFlow<Long> = _currentPosition.asStateFlow()

    private val _duration = MutableStateFlow(0L)
    val duration: StateFlow<Long> = _duration.asStateFlow()

    private val _queue = MutableStateFlow<List<Song>>(emptyList())
    val queue: StateFlow<List<Song>> = _queue.asStateFlow()

    private val _currentIndex = MutableStateFlow(-1)
    val currentIndex: StateFlow<Int> = _currentIndex.asStateFlow()

    private val _isShuffle = MutableStateFlow(false)
    val isShuffle: StateFlow<Boolean> = _isShuffle.asStateFlow()

    private val _repeatMode = MutableStateFlow(RepeatMode.OFF)
    val repeatMode: StateFlow<RepeatMode> = _repeatMode.asStateFlow()

    var onSongPlayed: ((Long) -> Unit)? = null

    private var originalQueue: List<Song> = emptyList()

    private val playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(playing: Boolean) {
            _isPlaying.value = playing
            if (playing) {
                startProgressTracker()
                _currentSong.value?.let { onSongPlayed?.invoke(it.id) }
            } else {
                stopProgressTracker()
            }
        }

        override fun onPlaybackStateChanged(playbackState: Int) {
            when (playbackState) {
                Player.STATE_READY -> {
                    _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                    audioEffectManager.attachAudioSession(exoPlayer.audioSessionId)
                }
                Player.STATE_ENDED -> {
                    when (_repeatMode.value) {
                        RepeatMode.ONE -> {
                            exoPlayer.seekTo(0)
                            exoPlayer.play()
                        }
                        RepeatMode.ALL -> {
                            skipToNext()
                        }
                        RepeatMode.OFF -> {
                            if (_currentIndex.value < _queue.value.size - 1) {
                                skipToNext()
                            } else {
                                pause()
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    private fun startProgressTracker() {
        progressJob?.cancel()
        progressJob = scope.launch {
            while (isActive) {
                _currentPosition.value = exoPlayer.currentPosition.coerceAtLeast(0L)
                _duration.value = exoPlayer.duration.coerceAtLeast(0L)
                delay(400)
            }
        }
    }

    private fun stopProgressTracker() {
        progressJob?.cancel()
        progressJob = null
    }

    fun playSongList(songs: List<Song>, startIndex: Int = 0) {
        if (songs.isEmpty()) return
        originalQueue = songs
        val targetQueue = if (_isShuffle.value) {
            val selected = songs.getOrNull(startIndex) ?: songs.first()
            listOf(selected) + songs.filter { it.id != selected.id }.shuffled()
        } else {
            songs
        }
        _queue.value = targetQueue
        val actualStart = if (_isShuffle.value) 0 else startIndex.coerceIn(0, targetQueue.size - 1)
        playTrackAtIndex(actualStart)
    }

    fun playTrackAtIndex(index: Int) {
        val q = _queue.value
        if (index !in q.indices) return

        _currentIndex.value = index
        val song = q[index]
        _currentSong.value = song

        val mediaMetadata = MediaMetadata.Builder()
            .setTitle(song.title)
            .setArtist(song.artist)
            .setAlbumTitle(song.album)
            .setArtworkUri(song.artworkUri)
            .build()

        val mediaItem = MediaItem.Builder()
            .setUri(song.contentUri)
            .setMediaId(song.id.toString())
            .setMediaMetadata(mediaMetadata)
            .build()

        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        exoPlayer.setMediaItem(mediaItem)
        exoPlayer.prepare()
        exoPlayer.play()
    }

    fun togglePlayPause() {
        if (exoPlayer.isPlaying) {
            exoPlayer.pause()
        } else {
            if (exoPlayer.playbackState == Player.STATE_IDLE || exoPlayer.currentMediaItem == null) {
                if (_queue.value.isNotEmpty()) {
                    playTrackAtIndex((_currentIndex.value).coerceAtLeast(0))
                }
            } else {
                exoPlayer.play()
            }
        }
    }

    fun pause() {
        exoPlayer.pause()
    }

    fun play() {
        exoPlayer.play()
    }

    fun seekTo(positionMs: Long) {
        exoPlayer.seekTo(positionMs)
        _currentPosition.value = positionMs
    }

    fun skipToNext() {
        val q = _queue.value
        if (q.isEmpty()) return

        val nextIndex = _currentIndex.value + 1
        if (nextIndex < q.size) {
            playTrackAtIndex(nextIndex)
        } else if (_repeatMode.value == RepeatMode.ALL) {
            playTrackAtIndex(0)
        }
    }

    fun skipToPrevious() {
        if (exoPlayer.currentPosition > 3000) {
            // Seek to start if already past 3 seconds
            seekTo(0)
            return
        }
        val prevIndex = _currentIndex.value - 1
        if (prevIndex >= 0) {
            playTrackAtIndex(prevIndex)
        } else {
            seekTo(0)
        }
    }

    fun toggleShuffle() {
        val next = !_isShuffle.value
        _isShuffle.value = next
        val cur = _currentSong.value
        if (next) {
            val remaining = _queue.value.filter { it.id != cur?.id }.shuffled()
            _queue.value = listOfNotNull(cur) + remaining
            _currentIndex.value = 0
        } else {
            _queue.value = originalQueue
            _currentIndex.value = originalQueue.indexOfFirst { it.id == cur?.id }.coerceAtLeast(0)
        }
    }

    fun toggleRepeatMode() {
        val nextMode = when (_repeatMode.value) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        _repeatMode.value = nextMode
        when (nextMode) {
            RepeatMode.OFF -> exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> exoPlayer.repeatMode = Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    fun addToQueue(song: Song) {
        _queue.value = _queue.value + song
    }

    fun playNext(song: Song) {
        val currentIdx = _currentIndex.value
        val list = _queue.value.toMutableList()
        list.add((currentIdx + 1).coerceAtMost(list.size), song)
        _queue.value = list
    }

    fun removeFromQueue(index: Int) {
        if (index !in _queue.value.indices) return
        val list = _queue.value.toMutableList()
        list.removeAt(index)
        _queue.value = list
        if (index < _currentIndex.value) {
            _currentIndex.value = _currentIndex.value - 1
        }
    }

    fun clearQueue() {
        _queue.value = emptyList()
        _currentIndex.value = -1
        exoPlayer.stop()
        exoPlayer.clearMediaItems()
        _currentSong.value = null
    }

    companion object {
        @Volatile
        private var INSTANCE: PlaybackManager? = null

        fun getInstance(context: Context): PlaybackManager {
            return INSTANCE ?: synchronized(this) {
                val instance = PlaybackManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
