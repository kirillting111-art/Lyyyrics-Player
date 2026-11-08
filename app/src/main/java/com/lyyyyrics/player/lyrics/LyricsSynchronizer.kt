package com.lyyyyrics.player.lyrics

object LyricsSynchronizer {

    fun getCurrentLine(
        position: Long,
        lines: List<LyricLine>
    ): Int {
        return lines.indexOfLast {
            position >= it.time
        }
    }

    fun getNextLine(
        position: Long,
        lines: List<LyricLine>
    ): LyricLine? {
        return lines.firstOrNull {
            it.time > position
        }
    }
}
