package org.drjekyll.matomo.tracker;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

class DelayedExecutorTest {

  private Instant executionTime;

  @Test
  void delaysExecution() throws Exception {

    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1);
    Executor delayedExecutor = new DelayedExecutor(scheduledThreadPoolExecutor, 1000L);

    Instant startTime = Instant.now();
    delayedExecutor.execute(() -> executionTime = Instant.now());
    Thread.sleep(2000L);

    assertThat(executionTime)
      .isAfter(startTime.plusMillis(1000L))
      .isBefore(startTime.plusMillis(1500L));

  }

}
