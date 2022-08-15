package org.drjekyll.matomo.tracker;

import java.util.concurrent.ThreadFactory;

import javax.annotation.Nullable;

class DaemonThreadFactory implements ThreadFactory {

  @Override
  public Thread newThread(@Nullable Runnable runnable) {
    Thread thread = new Thread(runnable);
    thread.setDaemon(true);
    thread.setName("MatomoTrackerDelayScheduler");
    return thread;
  }

}
