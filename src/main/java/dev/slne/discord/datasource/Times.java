package dev.slne.discord.datasource;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class Times {

    /**
     * Private constructor to hide the implicit public one.
     */
    private Times() {

    }

    /**
     * The current time.
     */
    public static LocalDateTime now() {
        return LocalDateTime.now(ZoneId.of("Europe/Berlin"));
    }
}
