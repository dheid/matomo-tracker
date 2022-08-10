package org.drjekyll.matomo.tracker;

import java.util.List;
import java.util.stream.Collectors;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * Multiple things that you can buy online
 */
@Builder
@Value
public class EcommerceItems {

  @Singular
  List<EcommerceItem> items;

  public String toString() {
    return items.stream().map(String::valueOf).collect(Collectors.joining(",", "[", "]"));
  }

}
