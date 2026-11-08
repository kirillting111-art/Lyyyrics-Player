package com.lyyyyrics.player.player

import androidx.compose.runtime.*

@Stable
class PlayerLibraryState(
    private val viewModel: PlayerViewModel
) {
    val songs: List<Song>
        get() = viewModel.songs

    val isLoading: Boolean
        get() = viewModel.isLoading

    val error: String?
        get() = viewModel.error

    fun load(context: android.content.Context) {
        viewModel.loadSongs(context)
    }

    fun select(song: Song) {
        viewModel.selectSong(song)
    }
}
