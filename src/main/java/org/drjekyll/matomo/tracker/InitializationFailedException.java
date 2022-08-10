package org.drjekyll.matomo.tracker;

/**
 * Happens if there was problem during the initialization of the tracker
 */
public class InitializationFailedException extends RuntimeException {

  private static final long serialVersionUID = 6287226409401522999L;

  InitializationFailedException(String message, Throwable cause) {
    super(message, cause);
  }

}
