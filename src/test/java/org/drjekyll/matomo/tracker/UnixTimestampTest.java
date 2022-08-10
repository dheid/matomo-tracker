package org.drjekyll.matomo.tracker;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;

class UnixTimestampTest {

  @Test
  void formatsSeconds() {

    UnixTimestamp unixTimestamp = UnixTimestamp.builder().seconds(123).build();

    assertThat(unixTimestamp).hasToString("123");

  }

  @Test
  void usesNow() {

    UnixTimestamp unixTimestamp = UnixTimestamp.now();

    assertThat(unixTimestamp.toString()).matches("\\d+");

  }

  @Test
  void createsFromInstant() {

    UnixTimestamp unixTimestamp = UnixTimestamp.fromInstant(Instant.ofEpochSecond(567));

    assertThat(unixTimestamp).hasToString("567");

  }

}
