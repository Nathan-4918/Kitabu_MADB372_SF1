package com.nathan.kitabu.domain

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

private const val DAY_MS = 24L * 60 * 60 * 1000

fun daysRemaining(deadline: Long, now: Long = System.currentTimeMillis()): Int {
    return ceil((deadline - now).toDouble() / DAY_MS).toInt()
}

fun formatDate(timestamp: Long): String {
    return SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(timestamp))
}