package com.lyyyyrics.player.lyrics

data class LyricLine(
    val time: Long,
    val text: String
)

object SongLyrics {

    val lines = listOf(
        LyricLine(2000, "Весь этот мир..."),
        LyricLine(5000, "И мы снова ищем ответы"),
        LyricLine(9000, "Среди миллионов дорог"),
        LyricLine(14000, "Оставляя прошлое где-то"),
        LyricLine(20000, "За пределами наших тревог")
    )
}
