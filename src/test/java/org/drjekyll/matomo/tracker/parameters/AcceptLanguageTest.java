package org.drjekyll.matomo.tracker.parameters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;

import static org.assertj.core.api.Assertions.assertThat;

class AcceptLanguageTest {

  @Test
  void fromHeader() {

    AcceptLanguage acceptLanguage = AcceptLanguage.fromHeader("de,de-DE;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6");

    assertThat(acceptLanguage).hasToString("de,de-de;q=0.9,de-dd;q=0.9,en;q=0.8,en-gb;q=0.7,en-us;q=0.6");

  }

  @ParameterizedTest
  @NullAndEmptySource
  void fromHeaderToleratesNull(String header) {

    AcceptLanguage acceptLanguage = AcceptLanguage.fromHeader(header);

    assertThat(acceptLanguage).isNull();

  }

}
