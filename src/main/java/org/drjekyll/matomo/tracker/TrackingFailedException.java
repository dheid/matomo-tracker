package org.drjekyll.matomo.tracker;

/**
 * The Matomo tracking endpoint returned an HTTP error status
 */
public class TrackingFailedException extends RuntimeException {

  private static final long serialVersionUID = 6940609241958574388L;

  TrackingFailedException(String message) {
    super(message);
  }

  TrackingFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
