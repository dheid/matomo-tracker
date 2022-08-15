package org.drjekyll.matomo.tracker.parameters;

import java.time.Instant;

import lombok.Builder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * The number of seconds that have elapsed since 00:00:00 UTC on 1 January 1970
 */
@Builder
@RequiredArgsConstructor
public class UnixTimestamp {

  private final long seconds;

  /**
   * @return the Unix timestamp of the current time
   */
  public static UnixTimestamp now() {
    return new UnixTimestamp(Instant.now().getEpochSecond());
  }

  /**
   * Creates a Unix timestamp of the given instant
   *
   * @param instant The Instant that should be converted
   * @return the Unix timestamp of the given instant
   */
  public static UnixTimestamp fromInstant(@NonNull Instant instant) {
    return new UnixTimestamp(instant.getEpochSecond());
  }

  @Override
  public String toString() {
    return Long.toString(seconds);
  }

}
