package org.drjekyll.matomo.tracker;

import java.util.concurrent.Executor;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class DelayedExecutor implements Executor {

  private final ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

  private final long delayInMillis;

  @Override
  public void execute(Runnable command) {
    scheduledThreadPoolExecutor.schedule(new CommonPoolRunnable(command), delayInMillis, TimeUnit.MILLISECONDS);
  }

}
