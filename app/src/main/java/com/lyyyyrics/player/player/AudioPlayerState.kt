package com.lyyyyrics.player.player

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*

@Stable
class AudioPlayerState(context: Context) {

    private val audioPlayer = AudioPlayer(context)

    var currentSong by mutableStateOf<Song?>(null)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    var position by mutableLongStateOf(0L)
        private set

    var duration by mutableLongStateOf(0L)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun playSong(song: Song) {
        currentSong = song
        position = 0L
        duration = 0L
        isPlaying = false
        error = null

        audioPlayer.onPrepared = {
            duration = audioPlayer.getDuration()
            position = 0L
            isPlaying = true
        }

        audioPlayer.onCompletion = {
            isPlaying = false
            position = duration
        }

        audioPlayer.onError = {
            isPlaying = false
            error = "Не удалось воспроизвести песню"
        }

        audioPlayer.play(song.uri)
    }

    fun togglePlayback() {
        if (audioPlayer.isPlaying()) {
            audioPlayer.pause()
            isPlaying = false
        } else if (currentSong != null) {
            if (position >= duration && duration > 0L) {
                audioPlayer.seekTo(0L)
                position = 0L
            }
            audioPlayer.resume()
            isPlaying = audioPlayer.isPlaying()
        }
    }

    fun seekTo(value: Long) {
        audioPlayer.seekTo(value)
        position = value.coerceAtLeast(0L)
    }

    fun updatePosition() {
        if (audioPlayer.isPlaying()) {
            position = audioPlayer.getCurrentPosition()
        }
    }

    fun release() {
        audioPlayer.release()
        isPlaying = false
    }
}

@Composable
fun rememberAudioPlayerState(): AudioPlayerState {
    val context = androidx.compose.ui.platform.LocalContext.current
    val state = remember(context) {
        AudioPlayerState(context)
    }

    LaunchedEffect(state.isPlaying) {
        while (state.isPlaying) {
            state.updatePosition()
            kotlinx.coroutines.delay(250)
        }
    }

    DisposableEffect(state) {
        onDispose {
            state.release()
        }
    }

    return state
}
