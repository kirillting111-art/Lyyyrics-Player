package com.lyyyyrics.player.widgets

class WidgetEngine {

    fun getWidgets(position: Long): WidgetScene {
        return WidgetScenes.getScene(position)
    }
}
