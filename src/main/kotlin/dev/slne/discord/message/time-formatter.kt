package dev.slne.discord.message

import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

val DATE_FORMATTER: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss")

fun formatDuration(startTime: ZonedDateTime?, endTime: ZonedDateTime?): String {
    var start = startTime

    val days = ChronoUnit.DAYS.between(start, endTime)
    if (start != null) {
        start = start.plusDays(days)
    }

    val hours = ChronoUnit.HOURS.between(start, endTime)
    if (start != null) {
        start = start.plusHours(hours)
    }

    val minutes = ChronoUnit.MINUTES.between(start, endTime)
    if (start != null) {
        start = start.plusMinutes(minutes)
    }

    val seconds = ChronoUnit.SECONDS.between(start, endTime)

    val result = StringBuilder()

    if (days > 0) {
        result.append(days).append(if (days == 1L) " Tag" else " Tage")
    }

    if (hours > 0) {
        if (result.isNotEmpty()) {
            result.append(", ")
        }

        result.append(hours).append(if (hours == 1L) " Stunde" else " Stunden")
    }

    if (minutes > 0) {
        if (result.isNotEmpty()) {
            result.append(", ")
        }

        result.append(minutes).append(if (minutes == 1L) " Minute" else " Minuten")
    }

    if (seconds > 0) {
        if (result.isNotEmpty()) {
            result.append(", ")
        }

        result.append(seconds).append(if (seconds == 1L) " Sekunde" else " Sekunden")
    }

    return result.toString()
}


//fun ZonedDateTime.toEuropeBerlin(): ZonedDateTime = withZoneSameInstant(berlinZoneId)
//
fun ZonedDateTime?.format(): String = this?.format(DATE_FORMATTER) ?: "???"

