package org.drjekyll.matomo.tracker.parameters;

import java.security.SecureRandom;
import java.util.Random;

/**
 * The unique visitor ID, must be a 16 characters hexadecimal string. Every unique visitor must be assigned a different
 * ID and this ID must not change after it is assigned. If this value is not set Matomo will still track visits, but the
 * unique visitors metric might be less accurate.
 */
public class VisitorId {

  private static final Random RANDOM = new SecureRandom();

  private final byte[] representation = new byte[8];

  /**
   * Static factory to generate a random visitor id.
   * <p>
   * Please consider creating a fixed id for each visitor by getting a hash code from e.g. the username and using {@link #fromHash(long)}
   *
   * @return A randomly generated visitor id
   */
  public static VisitorId random() {
    VisitorId visitorId = new VisitorId();
    RANDOM.nextBytes(visitorId.representation);
    return visitorId;
  }

  /**
   * Creates always the same visitor id for the given input.
   *
   * <p>
   * You can use e.g. {@link Object#hashCode()} to generate a hash code for an object, e.g. a username string as input.
   *
   * @param hash A number (e.g. a hash code) to create the visitor id from
   * @return Always the same visitor id for the same input
   */
  public static VisitorId fromHash(long hash) {
    VisitorId visitorId = new VisitorId();
    for (int i = 0; i < 7; i++) {
      visitorId.representation[i] = (byte) (i + hash & 0xFF );
      hash >>= 8;
    }
    return visitorId;
  }

  @Override
  public String toString() {
    StringBuilder result = new StringBuilder();
    for (byte b : representation) {
      result.append(String.format("%02x", b));
    }
    return result.toString();
  }

}
