package com.lyyyyrics.player.player

import androidx.compose.runtime.*

@Stable
class PlayerScreenState(
    val viewModel: PlayerViewModel
) {
    val audioState: AudioPlayerState
        get() = viewModel.audioState

    val songs: List<Song>
        get() = viewModel.songs

    val isLoading: Boolean
        get() = viewModel.isLoading

    val error: String?
        get() = viewModel.error

    fun selectSong(song: Song) {
        viewModel.selectSong(song)
    }

    fun next() {
        viewModel.nextSong()
    }

    fun previous() {
        viewModel.previousSong()
    }
}
