package dev.slne.discord.message;

import dev.slne.discord.util.TimeUtils;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import lombok.experimental.UtilityClass;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Utility class for formatting time-related operations.
 * <p>This class provides methods to format durations between two {@link ZonedDateTime} instances,
 * convert a given {@link ZonedDateTime} to the Europe/Berlin time zone, and format a
 * {@link ZonedDateTime} in the Europe/Berlin time zone. The methods are designed to handle null
 * values gracefully and ensure that the time zone conversions are performed accurately.</p>
 *
 * <p>Since this class is annotated with {@link UtilityClass}, it cannot be instantiated and all
 * methods and fields are static, reinforcing its utility nature.</p>
 *
 * @author twisti
 */
@UtilityClass
public class TimeFormatter {

  // @formatter:off
  /**
   * The {@link DateTimeFormatter} for formatting dates in the pattern "dd.MM.yyyy HH:mm:ss".
   * <p>This formatter is specifically configured for the Europe/Berlin time zone, ensuring that
   * dates are consistently formatted in this regional pattern.</p>
   */
  public final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");
  // @formatter:on

  /**
   * Formats the duration between the given start and end {@link ZonedDateTime}.
   * <p>This method calculates the difference between the start and end times, and returns a
   * formatted string that describes the duration in terms of days, hours, minutes, and
   * seconds.</p>
   *
   * @param start the start {@link ZonedDateTime}, passed by value
   * @param end   the end {@link ZonedDateTime}, passed by value
   * @return the formatted duration as a {@link String}
   */
  public String formatDuration(ZonedDateTime start, ZonedDateTime end) {
    final long days = ChronoUnit.DAYS.between(start, end);
    start = start.plusDays(days);

    final long hours = ChronoUnit.HOURS.between(start, end);
    start = start.plusHours(hours);

    final long minutes = ChronoUnit.MINUTES.between(start, end);
    start = start.plusMinutes(minutes);

    final long seconds = ChronoUnit.SECONDS.between(start, end);

    final StringBuilder result = new StringBuilder();

    if (days > 0) {
      result.append(days)
          .append(days == 1 ? " Tag" : " Tage");
    }
    if (hours > 0) {
      if (!result.isEmpty()) {
        result.append(", ");
      }
      result.append(hours)
          .append(hours == 1 ? " Stunde" : " Stunden");
    }
    if (minutes > 0) {
      if (!result.isEmpty()) {
        result.append(", ");
      }
      result.append(minutes)
          .append(minutes == 1 ? " Minute" : " Minuten");
    }
    if (seconds > 0) {
      if (!result.isEmpty()) {
        result.append(", ");
      }
      result.append(seconds)
          .append(seconds == 1 ? " Sekunde" : " Sekunden");
    }

    return result.toString();
  }

  /**
   * Formats the duration between two {@link ZonedDateTime} instances after converting them to the
   * Europe/Berlin time zone.
   * <p>This method takes two {@link ZonedDateTime} objects, converts them to the Europe/Berlin
   * time zone using {@link #toEuropeBerlin(ZonedDateTime)}, and then formats the duration between
   * them using {@link #formatDuration(ZonedDateTime, ZonedDateTime)}.</p>
   *
   * @param start the start {@link ZonedDateTime}, passed by reference; can be null
   * @param end   the end {@link ZonedDateTime}, passed by reference; can be null
   * @return the formatted duration as a {@link String}, or an empty string if both dates are null
   */
  public String formatEuropeBerlinDuration(
      @Nullable ZonedDateTime start,
      @Nullable ZonedDateTime end
  ) {
    return formatDuration(toEuropeBerlin(start), toEuropeBerlin(end));
  }

  /**
   * Converts the given {@link ZonedDateTime} to the Europe/Berlin time zone.
   * <p>If the provided {@link ZonedDateTime} is null, this method returns the current time in
   * the Europe/Berlin time zone. Otherwise, it adjusts the time to match the Europe/Berlin zone,
   * maintaining the same instant on the timeline.</p>
   *
   * @param zonedDateTime the {@link ZonedDateTime} to convert, passed by reference; can be null
   * @return the converted {@link ZonedDateTime}, never null
   */
  public @NotNull ZonedDateTime toEuropeBerlin(@Nullable ZonedDateTime zonedDateTime) {
    return zonedDateTime == null ? TimeUtils.berlinTimeProvider().getCurrentTime()
        : zonedDateTime.withZoneSameInstant(TimeUtils.BERLIN_ID);
  }

  /**
   * Formats the given {@link ZonedDateTime} to a string using the Europe/Berlin time zone.
   * <p>This method first converts the provided {@link ZonedDateTime} to the Europe/Berlin time
   * zone using {@link #toEuropeBerlin(ZonedDateTime)} and then formats it using the
   * {@link #DATE_FORMATTER}.</p>
   *
   * @param zonedDateTime the {@link ZonedDateTime} to format, passed by reference; can be null
   * @return the formatted date and time as a {@link String}
   * @see #toEuropeBerlin(ZonedDateTime)
   */
  public String formatEuropeBerlin(@Nullable ZonedDateTime zonedDateTime) {
    return DATE_FORMATTER.format(toEuropeBerlin(zonedDateTime));
  }
}
