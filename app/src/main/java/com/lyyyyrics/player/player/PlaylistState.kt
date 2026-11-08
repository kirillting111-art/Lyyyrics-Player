package com.lyyyyrics.player.player

import androidx.compose.runtime.*

@Stable
class PlaylistState(
    private val audioState: AudioPlayerState
) {
    private val manager = PlaylistManager()

    fun setPlaylist(songs: List<Song>, selectedSong: Song) {
        manager.setPlaylist(songs, selectedSong)
        audioState.playSong(selectedSong)
    }

    fun next() {
        manager.next()?.let { song ->
            audioState.playSong(song)
        }
    }

    fun previous() {
        manager.previous()?.let { song ->
            audioState.playSong(song)
        }
    }
}
