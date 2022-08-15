package org.drjekyll.matomo.tracker.parameters;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CountryTest {

  @Test
  void createsCountryFromCode() {

    Country country = Country.fromCode("DE");

    assertThat(country).hasToString("de");

  }

  @Test
  void createsCountryFromAcceptLanguageHeader() {

    Country country = Country.fromLanguageRanges("en-GB;q=0.7,de,de-DE;q=0.9,en;q=0.8,en-US;q=0.6");

    assertThat(country).hasToString("de");

  }

}
