package org.drjekyll.matomo.tracker;

/**
 * Occurs if a connection to the Matomo Tracking API endpoint could not be established
 */
public class ConnectionFailedException extends RuntimeException {

  private static final long serialVersionUID = 9117817573798567640L;

  ConnectionFailedException(Throwable cause) {
    super(cause);
  }

}
