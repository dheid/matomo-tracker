package org.drjekyll.matomo.tracker;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Locale.LanguageRange;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class QueryCreatorTest {

  private final Action.ActionBuilder actionBuilder = Action.builder();

  private String query;

  private String defaultTokenAuth;

  @Test
  void usesDefaultSiteId() {


    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&send_image=0");

  }

  @Test
  void overridesDefaultSiteId() {

    actionBuilder.siteId(123);

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=123&send_image=0");

  }

  @Test
  void usesDefaultTokenAuth() {

    defaultTokenAuth = "f123bfc9a46de0bb5453afdab6f93200";

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&token_auth=f123bfc9a46de0bb5453afdab6f93200&send_image=0");

  }

  @Test
  void overridesDefaultTokenAuth() {

    defaultTokenAuth = "f123bfc9a46de0bb5453afdab6f93200";
    actionBuilder.tokenAuth("e456bfc9a46de0bb5453afdab6f93200");

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&token_auth=e456bfc9a46de0bb5453afdab6f93200&send_image=0");

  }

  @Test
  void validatesTokenAuth() {

    actionBuilder.tokenAuth("invalid-token-auth");

    assertThatThrownBy(this::whenCreatesQuery)
      .isInstanceOf(IllegalArgumentException.class)
      .hasMessage("Invalid value for token_auth. Must match regex [a-z0-9]{32}");

  }

  @Test
  void convertsTrueBooleanTo1() {

    actionBuilder.flashPluginExists(true);

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&fla=1&send_image=0");

  }

  @Test
  void convertsFalseBooleanTo0() {

    actionBuilder.javaPluginExists(false);

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&java=0&send_image=0");

  }

  @Test
  void encodesUrl() {

    actionBuilder.url("https://www.daniel-heid.de/some/page?foo=bar");

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&url=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fpage%3Ffoo%3Dbar&send_image=0");

  }

  @Test
  void encodesReferrerUrl() {

    actionBuilder.referrerUrl("https://www.daniel-heid.de/some/referrer?foo2=bar2");

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Freferrer%3Ffoo2%3Dbar2&send_image=0");

  }

  @Test
  void encodesLink() {

    actionBuilder.link("https://www.daniel-heid.de/some/external/link#");

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&link=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fexternal%2Flink%23&send_image=0");

  }

  @Test
  void encodesDownloadUrl() {

    actionBuilder.download("https://www.daniel-heid.de/some/download.pdf");

    whenCreatesQuery();

    assertThat(query).isEqualTo("rec=1&idsite=42&download=https%3A%2F%2Fwww.daniel-heid.de%2Fsome%2Fdownload.pdf&send_image=0");

  }

  @Test
  void tracksMinimalRequest() {

    actionBuilder
      .name("Help / Feedback")
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
      .language(AcceptLanguage.builder().languageRange(new LanguageRange("de")).languageRange(new LanguageRange("de-DE", 0.9)).languageRange(new LanguageRange("en", 0.8)).build())
      .pageViewId(UniqueId.fromValue(999999999999999999L))
      .revenue(12.34)
      .ecommerceItems(EcommerceItems.builder().item(EcommerceItem.builder().sku("SKU").build()).item(EcommerceItem.builder().sku("SKU").name("NAME").category("CATEGORY").price(123.4).build()).build())
      .tokenAuth("fdf6e8461ea9de33176b222519627f78")
      .country(Country.fromLanguageRanges("en-GB;q=0.7,de,de-DE;q=0.9,en;q=0.8,en-US;q=0.6"))
    ;

    whenCreatesQuery();

    assertThat(query).isEqualTo(
      "rec=1&idsite=42&action_name=Help+%2F+Feedback&url=https%3A%2F%2Fwww.daniel-heid.de%2Fportfolio&_id=6749be5b2c42af00&urlref=https%3A%2F%2Fwww.daniel-heid.de%2Freferrer&_cvar=%7B%221%22%3A%5B%22customVariable1Key%22%2C%22customVariable1Value%22%5D%2C%222%22%3A%5B%22customVariable2Key%22%2C%22customVariable2Value%22%5D%7D&_idvc=2&_viewts=1660070052&res=1024x768&lang=de%2Cde-de%3Bq%3D0.9%2Cen%3Bq%3D0.8&pv_id=lbBbxG&revenue=12.34&gt_ms=30000&ec_items=%5B%5B%22SKU%22%2C%22%22%2C%22%22%2C0.000000%2C0%5D%2C%5B%22SKU%22%2C%22NAME%22%2C%22CATEGORY%22%2C123.400000%2C0%5D%5D&token_auth=fdf6e8461ea9de33176b222519627f78&country=de&send_image=0");

  }

  private void whenCreatesQuery() {
    query = new QueryCreator(actionBuilder.build()).createQuery(42, defaultTokenAuth);
  }

}
