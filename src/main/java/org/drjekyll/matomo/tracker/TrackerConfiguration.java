package org.drjekyll.matomo.tracker;

import java.net.URI;
import java.time.Duration;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Defines configuration settings for the Matomo tracking
 */
@Builder
@Value
public class TrackerConfiguration {

  /**
   * The Matomo Tracking HTTP API endpoint, e.g. https://your-matomo-domain.example/matomo.php
   */
  @NonNull URI apiEndpoint;

  /**
   * The default ID of the website that will be used if not specified explicitly.
   */
  Integer defaultSiteId;

  /**
   * The default token_auth if not specified explicitly.
   */
  String defaultTokenAuth;

  /**
   * The duration on how long the tracker will wait until the requests are sent out as bulk requests
   */
  @NonNull
  @Builder.Default
  Duration delay = Duration.ofSeconds(1L);

  /**
   * Allows to stop the tracker to send requests to the Matomo endpoint.
   */
  @Builder.Default
  boolean enabled = true;

  /**
   * The timeout until a connection is established.
   *
   * <p>A timeout value of zero is interpreted as an infinite timeout.
   * A `null` value is interpreted as undefined (system default if applicable).</p>
   *
   * <p>Default: 10 seconds</p>
   */
  @Builder.Default
  Duration connectTimeout = Duration.ofSeconds(10L);

  /**
   * The socket timeout ({@code SO_TIMEOUT}), which is the timeout for waiting for data or, put differently, a maximum
   * period inactivity between two consecutive data packets).
   *
   * <p>A timeout value of zero is interpreted as an infinite timeout.
   * A `null value is interpreted as undefined (system default if applicable).</p>
   *
   * <p>Default: 30 seconds</p>
   */
  @Builder.Default
  Duration socketTimeout = Duration.ofSeconds(30L);

  /**
   * The hostname or IP address of an optional HTTP proxy. {@code proxyPort} must be configured as well
   */
  @Nullable
  String proxyHost;

  /**
   * The port of an HTTP proxy. {@code proxyHost} must be configured as well.
   */
  int proxyPort;

  /**
   * If the HTTP proxy requires a user name for basic authentication, it can be configured here. Proxy host, port and
   * password must also be set.
   */
  @Nullable
  String proxyUserName;

  /**
   * The corresponding password for the basic auth proxy user. The proxy host, port and user name must be set as well.
   */
  @Nullable
  String proxyPassword;

  /**
   * A custom user agent to be set. Defaults to "MatomoJavaClient"
   */
  @Builder.Default
  @NonNull String userAgent = "MatomoJavaClient";

  /**
   * Logs if the Matomo Tracking API endpoint responds with a errornous HTTP code
   */
  boolean logFailedTracking;

}
