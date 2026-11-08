package com.lyyyyrics.player.player

import android.content.Context
import androidx.compose.runtime.*

@Stable
class PlayerViewModel(context: Context) {

    val audioState = AudioPlayerState(context)
    val playlistState = PlaylistState(audioState)

    var songs by mutableStateOf<List<Song>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    var error by mutableStateOf<String?>(null)
        private set

    fun loadSongs(context: Context) {
        isLoading = true
        error = null

        try {
            songs = SongRepository(context).getSongs()

            if (songs.isEmpty()) {
                error = "Музыка на устройстве не найдена"
            }
        } catch (exception: Exception) {
            error = "Не удалось загрузить музыку"
        }

        isLoading = false
    }

    fun selectSong(song: Song) {
        playlistState.setPlaylist(songs, song)
    }

    fun nextSong() {
        playlistState.next()
    }

    fun previousSong() {
        playlistState.previous()
    }

    fun release() {
        audioState.release()
    }
}

@Composable
fun rememberPlayerViewModel(): PlayerViewModel {
    val context = androidx.compose.ui.platform.LocalContext.current

    val viewModel = remember(context) {
        PlayerViewModel(context)
    }

    DisposableEffect(viewModel) {
        onDispose {
            viewModel.release()
        }
    }

    return viewModel
}
