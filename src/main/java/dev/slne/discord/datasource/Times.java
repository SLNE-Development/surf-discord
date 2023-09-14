package dev.slne.discord.datasource;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class Times {

    /**
     * Convert local date time to zoned date time.
     *
     * @param timestamp the timestamp
     *
     * @return the zoned date time
     */
    public static ZonedDateTime convertFromLocalDateTime(LocalDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }

        ZoneId utc = ZoneId.of("UTC");
        ZoneId berlin = ZoneId.of("Europe/Berlin");

        return timestamp.atZone(utc).withZoneSameInstant(berlin);
    }


    /**
     * Convert to local date time from local date time.
     *
     * @param timestamp the timestamp
     *
     * @return the local date time
     */
    public static LocalDateTime convertToLocalDateTime(ZonedDateTime timestamp) {
        if (timestamp == null) {
            return null;
        }

        return timestamp.toLocalDateTime();
    }
}
