package com.lyyyyrics.player.widgets

data class WidgetScene(
    val startTime: Long,
    val endTime: Long,
    val title: String,
    val symbols: List<String>,
    val captions: List<String>,
    val accentColor: Long
)

object WidgetScenes {

    val scenes = listOf(
        WidgetScene(
            0, 15000,
            "Начало",
            listOf("✦", "◌", "♡"),
            listOf("DREAM", "MOTION", "FEELING"),
            0xFF9B8CFF
        ),
        WidgetScene(
            15000, 35000,
            "Эмоции",
            listOf("♡", "✧", "∞"),
            listOf("LOVE", "LIGHT", "INFINITY"),
            0xFFFF8FA3
        ),
        WidgetScene(
            35000, 55000,
            "Расстояние",
            listOf("●", "↗", "◌"),
            listOf("DISTANCE", "PATH", "SPACE"),
            0xFF79C8FF
        ),
        WidgetScene(
            55000, 80000,
            "Время",
            listOf("◷", "⌁", "✦"),
            listOf("TIME", "MEMORY", "MOMENT"),
            0xFFFFC477
        ),
        WidgetScene(
            80000, 110000,
            "Финал",
            listOf("♡", "✦", "∞"),
            listOf("HEART", "DREAM", "FOREVER"),
            0xFFB99AFF
        ),
        WidgetScene(
            110000, 140000,
            "Завершение",
            listOf("◌", "✧", "♡"),
            listOf("SILENCE", "SOUL", "FEELING"),
            0xFF8FAEFF
        )
    )

    fun getScene(position: Long): WidgetScene {
        return scenes.firstOrNull {
            position >= it.startTime && position < it.endTime
        } ?: scenes.last()
    }
}
