package ru.softmine.simplenotes.common

import ru.softmine.simplenotes.R

enum class Color {
    WHITE,
    RED,
    ORANGE,
    YELLOW,
    GREEN,
    BLUE,
    VIOLET,
    PINK,
    BROWN
}

fun getColorResource(color: Color?): Int {
    return when (color) {
        Color.WHITE -> R.color.color_white
        Color.RED -> R.color.color_red
        Color.ORANGE -> R.color.color_orange
        Color.YELLOW -> R.color.color_yellow
        Color.GREEN -> R.color.color_green
        Color.BLUE -> R.color.color_blue
        Color.VIOLET -> R.color.color_violet
        Color.PINK -> R.color.color_pink
        Color.BROWN -> R.color.color_brown
        else -> R.color.color_white
    }
}