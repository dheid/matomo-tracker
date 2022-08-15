package org.drjekyll.matomo.tracker;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ProtocolException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
class Sender {

  private final TrackerConfiguration trackerConfiguration;

  private final QueryCreator queryCreator;

  private final Collection<String> queries = new ArrayList<>(16);

  private final Executor executor;

  CompletableFuture<Void> send(Action action) {
    synchronized (queries) {
      String query = queryCreator.createQuery(action);
      queries.add(query);
    }
    return CompletableFuture.supplyAsync(this::sendRequest, executor);
  }

  private Void sendRequest() {
    synchronized (queries) {
      if (!queries.isEmpty()) {
        HttpURLConnection connection = openConnection();
        prepareConnection(connection);
        log.debug("Sending bulk request using URI {} asynchronously", trackerConfiguration.getApiEndpoint());
        OutputStream outputStream = null;
        try {
          connection.connect();
          outputStream = connection.getOutputStream();
          outputStream.write(createPayload());
          outputStream.flush();
          if (connection.getResponseCode() > 399) {
            if (trackerConfiguration.isLogFailedTracking()) {
              log.error("Received error code {}", connection.getResponseCode());
            }
            throw new TrackingFailedException("Tracking endpoint responded with code " + connection.getResponseCode());
          }
          queries.clear();
        } catch (IOException e) {
          throw new ConnectionFailedException(e);
        } finally {
          if (outputStream != null) {
            try {
              outputStream.close();
            } catch (IOException e) {
              // ignore
            }
          }
          connection.disconnect();
        }
      }
      return null;
    }
  }

  private HttpURLConnection openConnection() {
    try {
      if (isEmpty(trackerConfiguration.getProxyHost()) || trackerConfiguration.getProxyPort() <= 0) {
        log.debug("Proxy host or proxy port not configured. Will create connection without proxy");
        return (HttpURLConnection) trackerConfiguration.getApiEndpoint().toURL().openConnection();
      }
      InetSocketAddress proxyAddress = new InetSocketAddress(
        trackerConfiguration.getProxyHost(),
        trackerConfiguration.getProxyPort()
      );
      Proxy proxy = new Proxy(Proxy.Type.HTTP, proxyAddress);
      if (!isEmpty(trackerConfiguration.getProxyUserName()) && !isEmpty(trackerConfiguration.getProxyPassword())) {
        Authenticator.setDefault(new ProxyAuthenticator(
          trackerConfiguration.getProxyUserName(),
          trackerConfiguration.getProxyPassword()
        ));
      }
      return (HttpURLConnection) trackerConfiguration.getApiEndpoint().toURL().openConnection(proxy);
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
    connection.setRequestProperty("Content-Type", "application/json");
    connection.setRequestProperty("User-Agent", trackerConfiguration.getUserAgent());

    if (trackerConfiguration.getConnectTimeout() != null) {
      connection.setConnectTimeout((int) trackerConfiguration.getConnectTimeout().toMillis());
    }
    if (trackerConfiguration.getSocketTimeout() != null) {
      connection.setReadTimeout((int) trackerConfiguration.getSocketTimeout().toMillis());
    }
  }

  private byte[] createPayload() {
    StringBuilder payload = new StringBuilder("{\"requests\":[");
    Iterator<String> iterator = queries.iterator();
    while (iterator.hasNext()) {
      String query = iterator.next();
      payload.append("\"?").append(query).append('"');
      if (iterator.hasNext()) {
        payload.append(',');
      }
    }
    payload.append(']');
    if (trackerConfiguration.getDefaultTokenAuth() != null) {
      payload.append(",\"token_auth\":\"").append(trackerConfiguration.getDefaultTokenAuth()).append('"');
    }
    payload.append('}');
    return payload.toString().getBytes(StandardCharsets.UTF_8);
  }

  private static boolean isEmpty(@Nullable String str) {
    return str == null || str.trim().isEmpty();
  }

}
