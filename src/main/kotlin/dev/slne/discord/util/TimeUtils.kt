package dev.slne.discord.util

import java.time.ZoneId
import java.time.ZonedDateTime

val berlinZoneId: ZoneId = ZoneId.of("Europe/Berlin")

object TimeUtils {

    val berlinTimeProvider: TimeProvider = BerlinTimeProvider()

    interface TimeProvider {
        val currentTime: ZonedDateTime
    }

    private class BerlinTimeProvider : TimeProvider {
        override val currentTime: ZonedDateTime
            get() = ZonedDateTime.now(berlinZoneId)
    }
}
