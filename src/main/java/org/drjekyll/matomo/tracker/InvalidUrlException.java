package org.drjekyll.matomo.tracker;

/**
 * Thrown if the tracking URL could not be built
 */
public class InvalidUrlException extends RuntimeException {

  private static final long serialVersionUID = -3834905404592106423L;

  InvalidUrlException(String message, Throwable cause) {
    super(message, cause);
  }

}
