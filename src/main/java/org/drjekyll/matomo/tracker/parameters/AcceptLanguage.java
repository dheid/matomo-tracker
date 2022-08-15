package org.drjekyll.matomo.tracker.parameters;

import java.util.List;
import java.util.Locale.LanguageRange;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import lombok.Builder;
import lombok.NonNull;
import lombok.Singular;
import lombok.Value;

/**
 * Describes the content for the Accept-Language header field that can be overriden by a custom parameter. The format is
 * specified in the corresponding <a href="http://tools.ietf.org/html/rfc4647">RFC 4647 Matching of Language Tags</a>
 */
@Builder
@Value
public class AcceptLanguage {

  @Singular
  List<LanguageRange> languageRanges;

  public String toString() {
    return languageRanges.stream()
      .filter(Objects::nonNull)
      .map(AcceptLanguage::format)
      .collect(Collectors.joining(","));
  }

  private static String format(@NonNull LanguageRange languageRange) {
    return languageRange.getWeight() == LanguageRange.MAX_WEIGHT ? languageRange.getRange() : languageRange.getRange() + ";q=" + languageRange.getWeight();
  }

  /**
   * Creates the Accept-Language definition for a given header.
   * <p>
   * This parses the value.
   *
   * @param header A header that can be null
   * @return The parsed header (probably reformatted). null if the header is null.
   */
  @Nullable
  public static AcceptLanguage fromHeader(@Nullable String header) {
    if (header == null || header.trim().isEmpty()) {
      return null;
    }
    return new AcceptLanguage(LanguageRange.parse(header));
  }

}
