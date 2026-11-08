package com.lyyyyrics.player.player

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri

class AudioPlayer(context: Context) {

    private val appContext = context.applicationContext

    private var mediaPlayer: MediaPlayer? = null

    var onPrepared: (() -> Unit)? = null
    var onCompletion: (() -> Unit)? = null
    var onError: (() -> Unit)? = null

    fun play(uri: Uri) {
        release()

        val player = MediaPlayer()
        mediaPlayer = player

        player.setOnPreparedListener {
            it.start()
            onPrepared?.invoke()
        }

        player.setOnCompletionListener {
            onCompletion?.invoke()
        }

        player.setOnErrorListener { _, _, _ ->
            onError?.invoke()
            true
        }

        try {
            player.setDataSource(appContext, uri)
            player.prepareAsync()
        } catch (exception: Exception) {
            release()
            onError?.invoke()
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.coerceAtLeast(0L).toInt())
    }

    fun getCurrentPosition(): Long {
        return try {
            mediaPlayer?.currentPosition?.toLong() ?: 0L
        } catch (exception: IllegalStateException) {
            0L
        }
    }

    fun getDuration(): Long {
        return try {
            mediaPlayer?.duration?.toLong() ?: 0L
        } catch (exception: IllegalStateException) {
            0L
        }
    }

    fun isPlaying(): Boolean {
        return try {
            mediaPlayer?.isPlaying == true
        } catch (exception: IllegalStateException) {
            false
        }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
