package com.lyyyyrics.player.player

import androidx.compose.runtime.*
import kotlinx.coroutines.delay

@Composable
fun rememberPlayerState(): PlayerState {
    val state = remember { PlayerState() }

    LaunchedEffect(state.isPlaying) {
        while (state.isPlaying) {
            delay(100)
            state.updatePosition(state.position + 100L)
        }
    }

    return state
}
