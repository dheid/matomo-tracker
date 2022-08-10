package org.drjekyll.matomo.tracker;

import java.util.List;

import lombok.Builder;
import lombok.Singular;
import lombok.Value;

/**
 * A bunch of key-value pairs that represent a custom information. See <a href="https://matomo.org/faq/how-to/guide-to-using-custom-variables-deprecated/">How do I use Custom Variables?</a>
 *
 * @deprecated Should not be used according to the Matomo FAQ: <a href="https://matomo.org/faq/how-to/guide-to-using-custom-variables-deprecated/">How do I use Custom Variables?</a>
 */
@Builder
@Value
public class CustomVariables {

  @Singular
  List<CustomVariable> variables;

  @Override
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder("{");
    for (int i = 0; i < variables.size(); i++) {
      CustomVariable variable = variables.get(i);
      stringBuilder.append('"')
        .append(i + 1)
        .append("\":[\"")
        .append(variable.getKey())
        .append("\",\"")
        .append(variable.getValue())
        .append("\"]");
      if (i != variables.size() - 1) {
        stringBuilder.append(',');
      }
    }
    stringBuilder.append('}');
    return stringBuilder.toString();
  }

}
