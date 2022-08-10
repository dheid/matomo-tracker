package org.drjekyll.matomo.tracker;

import lombok.Builder;
import lombok.RequiredArgsConstructor;

/**
 * The resolution (width and height) of the user's output device (monitor / phone)
 */
@Builder
@RequiredArgsConstructor
public class DeviceResolution {

  private final int width;

  private final int height;

  @Override
  public String toString() {
    return String.format("%dx%d", width, height);
  }

}
