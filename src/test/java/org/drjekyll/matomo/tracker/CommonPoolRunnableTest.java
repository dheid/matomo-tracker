package org.drjekyll.matomo.tracker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CommonPoolRunnableTest {

  private boolean result;

  @Test
  void runsCommandInCommonPool() throws Exception {

    Runnable commonPoolRunnable = new CommonPoolRunnable(() -> result = true);

    commonPoolRunnable.run();
    Thread.sleep(1000L);

    assertThat(result).isTrue();

  }

}
