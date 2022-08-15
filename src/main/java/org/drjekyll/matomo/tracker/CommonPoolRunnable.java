package org.drjekyll.matomo.tracker;

import java.util.concurrent.Executor;
import java.util.concurrent.ForkJoinPool;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class CommonPoolRunnable implements Runnable {

  private final Runnable command;

  private final Executor executor = ForkJoinPool.commonPool();

  @Override
  public void run() {
    executor.execute(command);
  }

}
