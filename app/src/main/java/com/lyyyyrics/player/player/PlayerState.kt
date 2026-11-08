package com.lyyyyrics.player.player

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class PlayerState {
    var position by mutableStateOf(0L)
        private set

    var isPlaying by mutableStateOf(false)
        private set

    fun updatePosition(value: Long) {
        position = value.coerceAtLeast(0L)
    }

    fun play() {
        isPlaying = true
    }

    fun pause() {
        isPlaying = false
    }

    fun seekTo(value: Long) {
        position = value.coerceAtLeast(0L)
    }
}
