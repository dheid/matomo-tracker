package org.drjekyll.matomo.tracker;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class VisitorIdTest {

  @Test
  void hasCorrectFormat() {

    VisitorId visitorId = VisitorId.random();

    assertThat(visitorId.toString()).matches("^[a-z0-9]{16}$");

  }

  @Test
  void createsRandomVisitorId() {

    VisitorId first = VisitorId.random();
    VisitorId second = VisitorId.random();

    assertThat(first).doesNotHaveToString(second.toString());

  }

  @Test
  void fixedVisitorIdForLongHash() {

    VisitorId visitorId = VisitorId.fromHash(987654321098765432L);

    assertThat(visitorId).hasToString("78b5fa4c63dfba00");

  }

  @Test
  void fixedVisitorIdForIntHash() {

    VisitorId visitorId = VisitorId.fromHash(777777777);

    assertThat(visitorId).hasToString("71f35d3104050600");

  }

  @Test
  void sameVisitorIdForSameHash() {

    VisitorId first = VisitorId.fromHash(1234567890L);
    VisitorId second = VisitorId.fromHash(1234567890);

    assertThat(first).hasToString(second.toString());

  }

  @Test
  void alwaysTheSameToString() {

    VisitorId visitorId = VisitorId.random();

    assertThat(visitorId).hasToString(visitorId.toString());

  }

}
