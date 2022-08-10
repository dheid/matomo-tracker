package org.drjekyll.matomo.tracker;

/**
 * Thrown if a parameter could not be read
 */
public class ParameterException extends RuntimeException {

  private static final long serialVersionUID = 4283919018502412682L;

  ParameterException(Throwable cause) {
    super(cause);
  }

}
