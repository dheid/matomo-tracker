package org.drjekyll.matomo.tracker;

import java.io.IOException;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URI;
import java.net.URL;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;

/**
 * Allows you to send requests to the Matomo Tracking API endpoint
 */
@Builder
@Value
@Slf4j
public class MatomoTracker {

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

  /**
   * Executes a POST call to the specified Matomo Tracking HTTP API endpoint.
   *
   * @param action Contains the required request parameters of the action to be tracked
   * @return A {@link CompletableFuture} that can be used to ensure whether the request is done or was errornous
   */
  public CompletableFuture<Void> track(@NonNull Action action) {
    if (enabled) {
      validateSiteId(action);
      URL actionTrackingUrl = createActionTrackingUrl(action);
      HttpURLConnection connection = openConnection(actionTrackingUrl);
      prepareConnection(connection);
      log.debug("Sending action {} using URL {} asynchronously", action, actionTrackingUrl);
      return CompletableFuture.supplyAsync(() -> connect(connection));
    }
    return CompletableFuture.completedFuture(null);
  }

  private void validateSiteId(Action action) {
    if (defaultSiteId == null && action.getSiteId() == null) {
      throw new IllegalArgumentException("No default site id and not action site id is given");
    }

    if (action.getSiteId() != null && action.getSiteId() < 0) {
      throw new IllegalArgumentException("Site ID must not be negative");
    }
  }

  private URL createActionTrackingUrl(Action action) {
    String query = new QueryCreator(action).createQuery(defaultSiteId, defaultTokenAuth);
    try {
      return new URL(
        apiEndpoint.getScheme(), apiEndpoint.getHost(), apiEndpoint.getPort(), apiEndpoint.getPath() + '?' + query
      );
    } catch (MalformedURLException e) {
      throw new InvalidUrlException("Error creating the tracking URL", e);
    }
  }

  private HttpURLConnection openConnection(URL actionTrackingUrl) {
    try {
      if (isEmpty(proxyHost) || proxyPort <= 0) {
        return (HttpURLConnection) actionTrackingUrl.openConnection();
      }
      InetSocketAddress proxyAddress = new InetSocketAddress(proxyHost, proxyPort);
      Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
      if (!isEmpty(proxyUserName) && !isEmpty(proxyPassword)) {
        Authenticator.setDefault(new ProxyAuthenticator(proxyUserName, proxyPassword));
      }
      return (HttpURLConnection) actionTrackingUrl.openConnection(proxy);
    } catch (IOException e) {
      throw new ConnectionFailedException(e);
    }
  }

  private void prepareConnection(HttpURLConnection connection) {
    try {
      connection.setRequestMethod("POST");
    } catch (ProtocolException e) {
      throw new TrackingFailedException("Could not set request method", e);
    }
    connection.setDoOutput(true);
    connection.setUseCaches(false);
    connection.setRequestProperty("Accept", "*/*");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
    connection.setRequestProperty("User-Agent", userAgent);
    connection.setRequestProperty("Content-Length", "0");

    if (connectTimeout != null) {
      connection.setConnectTimeout((int) connectTimeout.toMillis());
    }
    if (socketTimeout != null) {
      connection.setReadTimeout((int) socketTimeout.toMillis());
    }
  }

  private Void connect(HttpURLConnection connection) {
    log.debug("Establishing {}", connection);
    try {
      connection.connect();
      if (connection.getResponseCode() > 399) {
        if (logFailedTracking) {
          log.error("Received error code {}", connection.getResponseCode());
        }
        throw new TrackingFailedException("Tracking endpoint responded with code " + connection.getResponseCode());
      }
    } catch (IOException e) {
      throw new ConnectionFailedException(e);
    } finally {
      connection.disconnect();
    }
    return null;
  }

  private static boolean isEmpty(@Nullable String str) {
    return str == null || str.trim().isEmpty();
  }


}
