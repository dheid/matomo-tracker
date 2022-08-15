package org.drjekyll.matomo.tracker.parameters;

import java.security.SecureRandom;
import java.util.Random;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

/**
 * A six character unique ID consisting of the characters [0-9a-Z].
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UniqueId {

  private static final String CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";

  private static final Random RANDOM = new SecureRandom();

  private final long value;

  /**
   * Static factory to generate a random unique id.
   *
   * @return A randomly generated unique id
   */
  public static UniqueId random() {
    return fromValue(RANDOM.nextLong());
  }

  /**
   * Creates a unique id from a number
   *
   * @param value A number to create this unique id from
   * @return The unique id for the given value
   */
  public static UniqueId fromValue(long value) {
    return new UniqueId(value);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder(6);
    for (int i = 0; i < 6; i++) {
      int codePoint = (int) (value >> i * 8);
      sb.append(CHARS.charAt(Math.abs(codePoint % CHARS.length())));
    }
    return sb.toString();
  }

}
