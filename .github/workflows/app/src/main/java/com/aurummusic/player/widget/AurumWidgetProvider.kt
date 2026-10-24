package com.aurummusic.player.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.aurummusic.player.MainActivity
import com.aurummusic.player.R
import com.aurummusic.player.playback.PlaybackManager

class AurumWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_PLAY_PAUSE = "com.aurummusic.player.ACTION_PLAY_PAUSE"
        const val ACTION_NEXT = "com.aurummusic.player.ACTION_NEXT"
        const val ACTION_PREVIOUS = "com.aurummusic.player.ACTION_PREVIOUS"

        fun updateAllWidgets(context: Context) {
            val intent = Intent(context, AurumWidgetProvider::class.java).apply {
                action = AppWidgetManager.ACTION_APPWIDGET_UPDATE
            }
            val ids = AppWidgetManager.getInstance(context).getAppWidgetIds(
                ComponentName(context, AurumWidgetProvider::class.java)
            )
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids)
            context.sendBroadcast(intent)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val playbackManager = PlaybackManager.getInstance(context)

        when (intent.action) {
            ACTION_PLAY_PAUSE -> {
                playbackManager.togglePlayPause()
                updateAllWidgets(context)
            }
            ACTION_NEXT -> {
                playbackManager.skipToNext()
                updateAllWidgets(context)
            }
            ACTION_PREVIOUS -> {
                playbackManager.skipToPrevious()
                updateAllWidgets(context)
            }
        }
    }

    override fun onUpdate(context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray) {
        val playbackManager = PlaybackManager.getInstance(context)
        val currentSong = playbackManager.currentSong.value
        val isPlaying = playbackManager.isPlaying.value

        for (appWidgetId in appWidgetIds) {
            val views = RemoteViews(context.packageName, R.layout.widget_aurum_playback)

            // Open app on tap
            val appIntent = Intent(context, MainActivity::class.java)
            val appPendingIntent = PendingIntent.getActivity(
                context, 0, appIntent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
            views.setOnClickPendingIntent(R.id.widget_root, appPendingIntent)

            // Setup buttons
            views.setOnClickPendingIntent(
                R.id.widget_btn_prev,
                getBroadcastIntent(context, ACTION_PREVIOUS, 1)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_play_pause,
                getBroadcastIntent(context, ACTION_PLAY_PAUSE, 2)
            )
            views.setOnClickPendingIntent(
                R.id.widget_btn_next,
                getBroadcastIntent(context, ACTION_NEXT, 3)
            )

            // Song Info
            if (currentSong != null) {
                views.setTextViewText(R.id.widget_title, currentSong.title)
                views.setTextViewText(R.id.widget_artist, currentSong.artist)
            } else {
                views.setTextViewText(R.id.widget_title, "Aurum Music")
                views.setTextViewText(R.id.widget_artist, "Tap to open library")
            }

            // Play / Pause Icon
            val playPauseIcon = if (isPlaying) {
                android.R.drawable.ic_media_pause
            } else {
                android.R.drawable.ic_media_play
            }
            views.setImageViewResource(R.id.widget_btn_play_pause, playPauseIcon)

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    private fun getBroadcastIntent(context: Context, action: String, requestCode: Int): PendingIntent {
        val intent = Intent(context, AurumWidgetProvider::class.java).apply {
            this.action = action
        }
        return PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
}
