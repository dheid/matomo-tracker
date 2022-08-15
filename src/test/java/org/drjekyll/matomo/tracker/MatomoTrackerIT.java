package org.drjekyll.matomo.tracker;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.drjekyll.matomo.tracker.TrackerConfiguration.TrackerConfigurationBuilder;
import org.drjekyll.matomo.tracker.parameters.AcceptLanguage;
import org.drjekyll.matomo.tracker.parameters.Country;
import org.drjekyll.matomo.tracker.parameters.CustomVariable;
import org.drjekyll.matomo.tracker.parameters.CustomVariables;
import org.drjekyll.matomo.tracker.parameters.DeviceResolution;
import org.drjekyll.matomo.tracker.parameters.EcommerceItem;
import org.drjekyll.matomo.tracker.parameters.EcommerceItems;
import org.drjekyll.matomo.tracker.parameters.UniqueId;
import org.drjekyll.matomo.tracker.parameters.UnixTimestamp;
import org.drjekyll.matomo.tracker.parameters.VisitorId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale.LanguageRange;
import java.util.concurrent.CompletableFuture;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.resetAllRequests;
import static com.github.tomakehurst.wiremock.client.WireMock.status;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@WireMockTest(httpPort = 8099)
class MatomoTrackerIT {

  private static final int SITE_ID = 42;

  private final TrackerConfigurationBuilder trackerConfigurationBuilder = TrackerConfiguration.builder().delay(Duration.ofMillis(1L));

  private final Action.ActionBuilder actionBuilder = Action.builder();

  private CompletableFuture<Void> future;

  @BeforeEach
  void givenStub() {
    stubFor(post(urlPathEqualTo("/matomo.php")).willReturn(status(204)));
  }

  @Test
  void requiresApiEndpoint() {

    assertThatThrownBy(() -> trackerConfigurationBuilder.defaultSiteId(SITE_ID).build()).isInstanceOf(
        NullPointerException.class)
      .hasMessage("apiEndpoint is marked non-null but is null");

  }

  @Test
  void requiresSiteId() {

    trackerConfigurationBuilder.apiEndpoint(URI.create("http://localhost:8099/matomo.php")).build();

    assertThatThrownBy(this::whenTracksAction).isInstanceOf(IllegalArgumentException.class).hasMessage(
      "No default site id and not action site id is given");

  }

  private void whenTracksAction() {
    future = new MatomoTracker(trackerConfigurationBuilder.build()).track(actionBuilder.build());
    try {
      Thread.sleep(3000L);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void usesDefaultSiteId() {

    givenTrackerConfigurationWithDefaultSiteId();

    whenTracksAction();

    thenPostsRequest("rec=1&idsite=42&send_image=0", "46", "MatomoJavaClient");

  }

  private void givenTrackerConfigurationWithDefaultSiteId() {
    trackerConfigurationBuilder.apiEndpoint(URI.create("http://localhost:8099/matomo.php")).defaultSiteId(SITE_ID);
  }

  private void thenPostsRequest(String expectedQuery, String contentLength, String userAgent) {
    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php"))
      .withHeader("Content-Length", equalTo(contentLength))
      .withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withHeader("User-Agent", equalTo(userAgent))
      .withRequestBody(WireMock.equalToJson("{\"requests\":[\"?" + expectedQuery + "\"]}")));
  }

  @Test
  void overridesDefaultSiteId() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.siteId(123);

    whenTracksAction();

    thenPostsRequest("rec=1&idsite=123&send_image=0", "47", "MatomoJavaClient");

  }

  @Test
  void validatesTokenAuth() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.tokenAuth("invalid-token-auth");

    assertThatThrownBy(this::whenTracksAction).isInstanceOf(IllegalArgumentException.class).hasMessage(
      "Invalid value for token_auth. Must match regex [a-z0-9]{32}");

  }

  @Test
  void convertsTrueBooleanTo1() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.flashPluginExists(true);

    whenTracksAction();

    thenPostsRequest("rec=1&idsite=42&fla=1&send_image=0", "52", "MatomoJavaClient");

  }

  @Test
  void convertsFalseBooleanTo0() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.javaPluginExists(false);

    whenTracksAction();

    thenPostsRequest("rec=1&idsite=42&java=0&send_image=0", "53", "MatomoJavaClient");

  }

  @Test
  void encodesUrl() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.url("https://www.daniel-heid.de/some/page?foo=bar");

    whenTracksAction();

    thenPostsRequest(
      "rec=1&idsite=42&url=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fpage%3Ffoo%3Dbar&send_image=0",
      "109", "MatomoJavaClient"
    );

  }

  @Test
  void encodesReferrerUrl() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.referrerUrl("https://www.daniel-heid.de/some/referrer?foo2=bar2");

    whenTracksAction();

    thenPostsRequest(
      "rec=1&idsite=42&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Freferrer%3Ffoo2%3Dbar2&send_image=0",
      "118", "MatomoJavaClient"
    );

  }

  @Test
  void encodesLink() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.link("https://www.daniel-heid.de/some/external/link#");

    whenTracksAction();

    thenPostsRequest(
      "rec=1&idsite=42&link=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fexternal%2Flink%23&send_image=0",
      "112", "MatomoJavaClient"
    );

  }

  @Test
  void encodesDownloadUrl() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.download("https://www.daniel-heid.de/some/download.pdf");

    whenTracksAction();

    thenPostsRequest(
      "rec=1&idsite=42&download=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fdownload.pdf&send_image=0",
      "110", "MatomoJavaClient"
    );

  }

  @Test
  void containsHeaders() {

    givenTrackerConfigurationWithDefaultSiteId();

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php")).withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withHeader("User-Agent", equalTo("MatomoJavaClient")));

  }

  @Test
  void allowsToOverrideUserAgent() {

    givenTrackerConfigurationWithDefaultSiteId();
    trackerConfigurationBuilder.userAgent("Mozilla/5.0");

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php")).withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withHeader("User-Agent", equalTo("Mozilla/5.0")));

  }

  @Test
  void tracksMinimalRequest() {

    givenTrackerConfigurationWithDefaultSiteId();
    actionBuilder.name("Help / Feedback")
      .url("https://www.daniel-heid.de/portfolio")
      .visitorId(VisitorId.fromHash(3434343434343434343L))
      .referrerUrl("https://www.daniel-heid.de/referrer")
      .visitScopeCustomVariables(CustomVariables.builder()
        .variable(new CustomVariable("customVariable1Key", "customVariable1Value"))
        .variable(new CustomVariable("customVariable2Key", "customVariable2Value"))
        .build())
      .visitCount(2)
      .generationTime(30000L)
      .visitTimestamp(UnixTimestamp.fromInstant(LocalDateTime.of(
        2022,
        8,
        9,
        18,
        34,
        12
      ).toInstant(ZoneOffset.UTC)))
      .deviceResolution(DeviceResolution.builder().width(1024).height(768).build())
      .language(AcceptLanguage.builder().languageRange(new LanguageRange("de")).languageRange(new LanguageRange(
        "de-DE",
        0.9
      )).languageRange(new LanguageRange("en", 0.8)).build())
      .pageViewId(UniqueId.fromValue(999999999999999999L))
      .revenue(12.34)
      .ecommerceItems(EcommerceItems.builder()
        .item(EcommerceItem.builder()
          .sku("SKU")
          .build())
        .item(EcommerceItem.builder().sku("SKU").name("NAME").category("CATEGORY").price(123.4).build())
        .build())
      .tokenAuth("fdf6e8461ea9de33176b222519627f78")
      .country(Country.fromLanguageRanges("en-GB;q=0.7,de,de-DE;q=0.9,en;q=0.8,en-US;q=0.6"));

    whenTracksAction();

    thenPostsRequest(
      "rec=1&idsite=42&action_name=Help+%2F+Feedback&url=https%3A%2F%2Fwww.daniel-heid.de%2Fportfolio&_id=6749be5b2c42af00&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Freferrer&_cvar=%7B%221%22%3A%5B%22customVariable1Key%22%2C%22customVariable1Value%22%5D%2C%222%22%3A%5B%22customVariable2Key%22%2C%22customVariable2Value%22%5D%7D&_idvc=2&_viewts=1660070052&res=1024x768&lang=de%2Cde-de%3Bq%3D0.9%2Cen%3Bq%3D0.8&pv_id=lbBbxG&revenue=12.34&gt_ms=30000&ec_items=%5B%5B%22SKU%22%2C%22%22%2C%22%22%2C0.000000%2C0%5D%2C%5B%22SKU%22%2C%22NAME%22%2C%22CATEGORY%22%2C123.400000%2C0%5D%5D&token_auth=fdf6e8461ea9de33176b222519627f78&country=de&send_image=0",
      "655",
      "MatomoJavaClient"
    );

  }

  @Test
  void doesNothingIfNotEnabled() {

    resetAllRequests();
    givenTrackerConfigurationWithDefaultSiteId();
    trackerConfigurationBuilder.enabled(false);

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(0, postRequestedFor(urlPathEqualTo("/matomo.php")));

  }

  @Test
  void exampleWorks() {

    TrackerConfiguration config = TrackerConfiguration.builder().apiEndpoint(URI.create(
        "https://your-domain.net/matomo/matomo.php")).defaultSiteId(42) // if not explicitly specified by action
      .build();

    // Prepare the tracker (stateless - can be used for multiple actions)
    MatomoTracker tracker = new MatomoTracker(config);

    // Track an action
    CompletableFuture<Void> future = tracker.track(Action.builder().name("User Profile / Upload Profile Picture").url(
        "https://your-domain.net/user/profile/picture").visitorId(VisitorId.fromHash("some@email-adress.org".hashCode()))
      // ...
      .build());

    // If you want to ensure the request has been handled:
    if (future.isCompletedExceptionally()) {
      // log, throw, ...
    }
  }

  @Test
  void reportsErrors() {

    stubFor(post(urlPathEqualTo("/failing")).willReturn(status(500)));
    trackerConfigurationBuilder.apiEndpoint(URI.create("http://localhost:8099/failing")).defaultSiteId(SITE_ID);

    whenTracksAction();

    assertThat(future).isCompletedExceptionally();

  }

  @Test
  void includesDefaultTokenAuth() {

    givenTrackerConfigurationWithDefaultSiteId();
    trackerConfigurationBuilder.defaultTokenAuth("fdf6e8461ea9de33176b222519627f78");

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php"))
      .withHeader("Content-Length", equalTo("138"))
      .withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withHeader("User-Agent", equalTo("MatomoJavaClient"))
      .withRequestBody(WireMock.equalToJson(
        "{\"requests\":[\"?rec=1&idsite=42&token_auth=fdf6e8461ea9de33176b222519627f78&send_image=0\"],\"token_auth\":\"fdf6e8461ea9de33176b222519627f78\"}")));

  }

  @Test
  void includesMultipleQueriesInBulkRequest() throws Exception {

    givenTrackerConfigurationWithDefaultSiteId();
    trackerConfigurationBuilder.delay(Duration.ofSeconds(1L));
    MatomoTracker tracker = new MatomoTracker(trackerConfigurationBuilder.build());

    CompletableFuture<Void> future1 = tracker.track(actionBuilder.name("First").build());
    CompletableFuture<Void> future2 = tracker.track(actionBuilder.name("Second").build());
    CompletableFuture<Void> future3 = tracker.track(actionBuilder.name("Third").build());
    Thread.sleep(3000L);

    assertThat(future1).isNotCompletedExceptionally();
    assertThat(future2).isNotCompletedExceptionally();
    assertThat(future3).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php"))
      .withHeader("Content-Length", equalTo("165"))
      .withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/json"))
      .withHeader("User-Agent", equalTo("MatomoJavaClient"))
      .withRequestBody(WireMock.equalToJson(
        "{\"requests\":[\"?rec=1&idsite=42&action_name=First&send_image=0\",\"?rec=1&idsite=42&action_name=Second&send_image=0\",\"?rec=1&idsite=42&action_name=Third&send_image=0\"]}")));

  }

}
