package ru.softmine.simplenotes.common

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

const val DATETIME_FORMAT = "dd MMM yyyy HH:mm"

fun Date.format() : String = SimpleDateFormat(DATETIME_FORMAT, Locale.getDefault()).format(this)

fun Color.getColorInt(context: Context): Int = context.getColor(getColorResource(this))