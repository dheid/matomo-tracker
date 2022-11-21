package org.drjekyll.matomo.tracker.parameters;

import java.util.List;
import java.util.Locale;
import java.util.Locale.LanguageRange;

import javax.annotation.Nullable;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * A two letter country code representing a country
 */
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class Country {

  @NonNull
  private final String code;

  /**
   * Creates a country from a given code.
   *
   * @param code Must consist of two lower letters or simply null. Case is ignored
   * @return The country or null if code was null
   */
  @Nullable
  public static Country fromCode(@Nullable String code) {
    if (code == null || code.isEmpty() || code.trim().isEmpty()) {
      return null;
    }
    if (code.length() == 2) {
      return new Country(code.toLowerCase(Locale.ROOT));
    }
    throw new IllegalArgumentException("Invalid country code");
  }

  /**
   * Extracts the country from the given accept language header.
   *
   * @param ranges A language range list. See {@link LanguageRange#parse(String)}
   * @return The country or null if ranges was null
   */
  @Nullable
  public static Country fromLanguageRanges(@Nullable String ranges) {
    if (ranges == null || ranges.isEmpty() || ranges.trim().isEmpty()) {
      return null;
    }
    List<LanguageRange> languageRanges = LanguageRange.parse(ranges);
    for (LanguageRange languageRange : languageRanges) {
      String range = languageRange.getRange();
      String[] split = range.split("-");
      if (split.length == 2 && split[1].length() == 2) {
        return new Country(split[1].toLowerCase(Locale.ROOT));
      }
    }
    throw new IllegalArgumentException("Invalid country code");
  }

  @Override
  public String toString() {
    return code;
  }

}
