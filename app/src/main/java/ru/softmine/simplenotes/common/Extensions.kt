package ru.softmine.simplenotes.common

import android.content.Context
import ru.softmine.simplenotes.R
import java.text.SimpleDateFormat
import java.util.*

const val DATETIME_FORMAT = "dd MMM yyyy HH:mm"

fun Date.format(): String = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(this)

fun Color.getColorInt(context: Context): Int = context.getColor(this.getColorResource())

fun Color.getColorResource(): Int = when (this) {
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

fun Context.dip(value: Int): Int = (value * resources.displayMetrics.density).toInt()