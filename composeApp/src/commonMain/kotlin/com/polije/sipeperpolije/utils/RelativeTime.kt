package com.polije.sipeperpolije.utils

import kotlinx.datetime.TimeZone
import kotlin.time.Clock
import kotlin.time.Instant

fun Instant.toRelativeTime(
    now: Instant = Clock.System.now(),
    timeZone: TimeZone = TimeZone.currentSystemDefault()
): String {
    val duration = now - this
    val seconds = duration.inWholeSeconds

    return when {
        seconds < 60 -> "baru saja"
        seconds < 3600 -> "${seconds / 60} menit yang lalu"
        seconds < 86_400 -> "${seconds / 3600} jam yang lalu"
        seconds < 2_592_000 -> "${seconds / 86_400} hari yang lalu"
        seconds < 31_536_000 -> "${seconds / 2_592_000} bulan yang lalu"
        else -> "${seconds / 31_536_000} tahun yang lalu"
    }
}
