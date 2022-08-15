package org.drjekyll.matomo.tracker.parameters;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DeviceResolutionTest {

  @Test
  void formatsDeviceResolution() {

    DeviceResolution deviceResolution = DeviceResolution.builder().width(1280).height(1080).build();

    assertThat(deviceResolution).hasToString("1280x1080");

  }

}
