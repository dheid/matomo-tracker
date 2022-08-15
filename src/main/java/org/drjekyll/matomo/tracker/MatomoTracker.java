package org.drjekyll.matomo.tracker;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

/**
 * Allows you to send requests to the Matomo Tracking API endpoint
 */
@Slf4j
public class MatomoTracker {

  private final TrackerConfiguration trackerConfiguration;

  private final Sender sender;

  /**
   * Creates a new Matomo Tracker instance
   *
   * @param trackerConfiguration Configurations parameters (you can use a builder)
   */
  public MatomoTracker(TrackerConfiguration trackerConfiguration) {
    this.trackerConfiguration = trackerConfiguration;
    DaemonThreadFactory threadFactory = new DaemonThreadFactory();
    ScheduledThreadPoolExecutor scheduledThreadPoolExecutor = new ScheduledThreadPoolExecutor(1, threadFactory);
    scheduledThreadPoolExecutor.setRemoveOnCancelPolicy(true);
    DelayedExecutor delayedExecutor = new DelayedExecutor(
      scheduledThreadPoolExecutor,
      trackerConfiguration.getDelay().toMillis()
    );
    sender = new Sender(trackerConfiguration, new QueryCreator(trackerConfiguration), delayedExecutor);
  }

  /**
   * Executes a POST call to the specified Matomo Tracking HTTP API endpoint.
   *
   * @param action Contains the required request parameters of the action to be tracked
   * @return A {@link CompletableFuture} that can be used to ensure whether the request is done or was errornous
   */
  public CompletableFuture<Void> track(@NonNull Action action) {
    if (trackerConfiguration.isEnabled()) {
      validateSiteId(action);
      return sender.send(action);
    }
    return CompletableFuture.completedFuture(null);
  }

  private void validateSiteId(Action action) {
    if (trackerConfiguration.getDefaultSiteId() == null && action.getSiteId() == null) {
      throw new IllegalArgumentException("No default site id and not action site id is given");
    }

    if (action.getSiteId() != null && action.getSiteId() < 0) {
      throw new IllegalArgumentException("Site ID must not be negative");
    }
  }

}
