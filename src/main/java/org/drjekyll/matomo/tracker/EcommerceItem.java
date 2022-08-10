package org.drjekyll.matomo.tracker;

import lombok.Builder;
import lombok.NonNull;
import lombok.Value;

/**
 * Represents something that could be bought by someone on a website
 */
@Builder
@Value
public class EcommerceItem {

  @NonNull
  String sku;

  @NonNull
  @Builder.Default
  String name = "";

  @NonNull
  @Builder.Default
  String category = "";

  @NonNull
  @Builder.Default
  Double price = 0.0;

  @NonNull
  @Builder.Default
  Long quantity = 0L;

  public String toString() {
    return String.format("[\"%s\",\"%s\",\"%s\",%f,%d]", sku, name, category, price, quantity);
  }
}
