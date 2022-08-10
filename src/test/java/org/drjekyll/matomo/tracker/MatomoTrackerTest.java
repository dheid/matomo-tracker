package org.drjekyll.matomo.tracker;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
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
class MatomoTrackerTest {

  private static final int SITE_ID = 42;

  private final MatomoTracker.MatomoTrackerBuilder trackerBuilder = MatomoTracker.builder();

  private final Action.ActionBuilder actionBuilder = Action.builder();

  private CompletableFuture<Void> future;

  @BeforeEach
  void givenStub() {
    stubFor(post(urlPathEqualTo("/matomo.php")).willReturn(status(204)));
  }

  @Test
  void requiresApiEndpoint() {

    assertThatThrownBy(() -> trackerBuilder.defaultSiteId(SITE_ID).build())
      .isInstanceOf(NullPointerException.class)
      .hasMessage("apiEndpoint is marked non-null but is null");

  }

  @Test
  void requiresSiteId() {

    trackerBuilder.apiEndpoint(URI.create("http://localhost:8099/matomo.php")).build();

    assertThatThrownBy(this::whenTracksAction).isInstanceOf(IllegalArgumentException.class).hasMessage(
      "No default site id and not action site id is given");

  }

  @Test
  void usesDefaultSiteId() {

    givenTrackerWithDefaultSiteId();

    whenTracksAction();

    thenPostsRequest("/matomo.php?rec=1&idsite=42&send_image=0");

  }

  @Test
  void overridesDefaultSiteId() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.siteId(123);

    whenTracksAction();

    thenPostsRequest("/matomo.php?rec=1&idsite=123&send_image=0");

  }

  @Test
  void validatesTokenAuth() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.tokenAuth("invalid-token-auth");

    assertThatThrownBy(this::whenTracksAction).isInstanceOf(IllegalArgumentException.class).hasMessage(
      "Invalid value for token_auth. Must match regex [a-z0-9]{32}");

  }

  @Test
  void convertsTrueBooleanTo1() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.flashPluginExists(true);

    whenTracksAction();

    thenPostsRequest("/matomo.php?rec=1&idsite=42&fla=1&send_image=0");

  }

  @Test
  void convertsFalseBooleanTo0() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.javaPluginExists(false);

    whenTracksAction();

    thenPostsRequest("/matomo.php?rec=1&idsite=42&java=0&send_image=0");

  }

  @Test
  void encodesUrl() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.url("https://www.daniel-heid.de/some/page?foo=bar");

    whenTracksAction();

    thenPostsRequest(
      "/matomo.php?rec=1&idsite=42&url=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fpage%3Ffoo%3Dbar&send_image=0");

  }

  @Test
  void encodesReferrerUrl() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.referrerUrl("https://www.daniel-heid.de/some/referrer?foo2=bar2");

    whenTracksAction();

    thenPostsRequest(
      "/matomo.php?rec=1&idsite=42&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Freferrer%3Ffoo2%3Dbar2&send_image=0");

  }

  @Test
  void encodesLink() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.link("https://www.daniel-heid.de/some/external/link#");

    whenTracksAction();

    thenPostsRequest(
      "/matomo.php?rec=1&idsite=42&link=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fexternal%2Flink%23&send_image=0");

  }

  @Test
  void encodesDownloadUrl() {

    givenTrackerWithDefaultSiteId();
    actionBuilder.download("https://www.daniel-heid.de/some/download.pdf");

    whenTracksAction();

    thenPostsRequest(
      "/matomo.php?rec=1&idsite=42&download=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fdownload.pdf&send_image=0");

  }

  @Test
  void containsHeaders() {

    givenTrackerWithDefaultSiteId();

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php?rec=1&idsite=42&send_image=0")).withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded; charset=UTF-8"))
      .withHeader("User-Agent", equalTo("MatomoJavaClient")));

  }

  @Test
  void allowsToOverrideUserAgent() {

    givenTrackerWithDefaultSiteId();
    trackerBuilder.userAgent("Mozilla/5.0");

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo("/matomo.php?rec=1&idsite=42&send_image=0")).withHeader("Accept", equalTo("*/*"))
      .withHeader("Content-Type", equalTo("application/x-www-form-urlencoded; charset=UTF-8"))
      .withHeader("User-Agent", equalTo("Mozilla/5.0")));

  }

  @Test
  void tracksMinimalRequest() {

    givenTrackerWithDefaultSiteId();
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
      .visitTimestamp(UnixTimestamp.fromInstant(LocalDateTime.of(2022, 8, 9, 18, 34, 12).toInstant(ZoneOffset.UTC)))
      .deviceResolution(DeviceResolution.builder().width(1024).height(768).build())
      .language(AcceptLanguage.builder().languageRange(new LanguageRange("de")).languageRange(new LanguageRange(
        "de-DE",
        0.9
      )).languageRange(new LanguageRange("en", 0.8)).build())
      .pageViewId(UniqueId.fromValue(999999999999999999L))
      .revenue(12.34)
      .ecommerceItems(EcommerceItems.builder()
        .item(EcommerceItem.builder().sku("SKU").build())
        .item(EcommerceItem.builder().sku("SKU").name("NAME").category("CATEGORY").price(123.4).build()).build())
      .tokenAuth("fdf6e8461ea9de33176b222519627f78")
      .country(Country.fromLanguageRanges("en-GB;q=0.7,de,de-DE;q=0.9,en;q=0.8,en-US;q=0.6"));

    whenTracksAction();

    thenPostsRequest(
      "/matomo.php?rec=1&idsite=42&action_name=Help+%2F+Feedback&url=https%3A%2F%2Fwww.daniel-heid.de%2Fportfolio&_id=6749be5b2c42af00&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Freferrer&_cvar=%7B%221%22%3A%5B%22customVariable1Key%22%2C%22customVariable1Value%22%5D%2C%222%22%3A%5B%22customVariable2Key%22%2C%22customVariable2Value%22%5D%7D&_idvc=2&_viewts=1660070052&res=1024x768&lang=de%2Cde-de%3Bq%3D0.9%2Cen%3Bq%3D0.8&pv_id=lbBbxG&revenue=12.34&gt_ms=30000&ec_items=%5B%5B%22SKU%22%2C%22%22%2C%22%22%2C0%2C000000%2C0%5D%2C%5B%22SKU%22%2C%22NAME%22%2C%22CATEGORY%22%2C123%2C400000%2C0%5D%5D&token_auth=fdf6e8461ea9de33176b222519627f78&country=de&send_image=0");

  }

  @Test
  void doesNothingIfNotEnabled() {

    resetAllRequests();
    givenTrackerWithDefaultSiteId();
    trackerBuilder.enabled(false);

    whenTracksAction();

    assertThat(future).isNotCompletedExceptionally();
    verify(0, postRequestedFor(urlPathEqualTo("/matomo.php")));

  }

  @Test
  void exampleWorks() {

    MatomoTracker tracker = MatomoTracker.builder()
      .apiEndpoint(URI.create("https://your-domain.net/matomo/matomo.php"))
      .defaultSiteId(42) // if not explicitly specified by action
      .build();

    CompletableFuture<Void> future = tracker.track(Action.builder()
      .name("User Profile / Upload Profile Picture")
      .url("https://your-domain.net/user/profile/picture")
      .visitorId(VisitorId.fromHash("some@email-adress.org".hashCode()))
      .build());

// if you want to ensure the request has been handled:
    if (future.isCompletedExceptionally()) {
      // log, throw, ...
    }

  }

  @Test
  void reportsErrors() {

    stubFor(post(urlPathEqualTo("/failing")).willReturn(status(500)));
    trackerBuilder.apiEndpoint(URI.create("http://localhost:8099/failing")).defaultSiteId(SITE_ID);

    whenTracksAction();

    assertThat(future).isCompletedExceptionally();

  }

  private void givenTrackerWithDefaultSiteId() {
    trackerBuilder.apiEndpoint(URI.create("http://localhost:8099/matomo.php")).defaultSiteId(SITE_ID);
  }

  private void whenTracksAction() {
    future = trackerBuilder.build().track(actionBuilder.build());
    while (!future.isDone()) {
      try {
        Thread.sleep(10L);
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }
    }
  }

  private void thenPostsRequest(String testUrl) {
    assertThat(future).isNotCompletedExceptionally();
    verify(postRequestedFor(urlEqualTo(testUrl)));
  }

}
