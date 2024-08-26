package dev.slne.discord.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.experimental.UtilityClass;

@UtilityClass
public class TimeUtils {

  // @formatter:off
  /**
   * The {@link ZoneId} for the Europe/Berlin time zone.
   * <p>This is a constant attribute used to convert or format {@link ZonedDateTime} instances
   * to/from the Europe/Berlin time zone.</p>
   */
  public final ZoneId BERLIN_ID = ZoneId.of("Europe/Berlin");
  private final TimeProvider BERLIN_TIME_PROVIDER = new BerlinTimeProvider();
  // @formatter:on

  public TimeProvider berlinTimeProvider() {
    return BERLIN_TIME_PROVIDER;
  }

  public interface TimeProvider {

    ZonedDateTime getCurrentTime();
  }

  private static final class BerlinTimeProvider implements TimeProvider {

    @Override
    public ZonedDateTime getCurrentTime() {
      return ZonedDateTime.now(BERLIN_ID);
    }
  }
}
