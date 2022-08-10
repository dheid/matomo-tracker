package org.drjekyll.matomo.tracker;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

import lombok.Builder;
import lombok.Value;

@Builder
@Value
class TrackingParameterMethod {

  String parameterName;

  Method method;

  Pattern pattern;

  void validateParameterValue(Object parameterValue) {
    if (pattern != null && parameterValue instanceof CharSequence && !pattern.matcher((CharSequence) parameterValue).matches()) {
      throw new IllegalArgumentException(String.format("Invalid value for %s. Must match regex %s",
        parameterName,
        pattern
      ));
    }
  }

}
