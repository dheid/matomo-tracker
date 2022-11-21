package org.drjekyll.matomo.tracker;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
class QueryCreator {

  private static final List<TrackingParameterMethod> TRACKING_PARAMETER_METHODS = initializeTrackingParameterMethods();

  private final TrackerConfiguration trackerConfiguration;

  public String createQuery(Action action) {
    StringBuilder query = new StringBuilder("rec=1");
    if (action.getSiteId() == null) {
      query.append("&idsite=").append(trackerConfiguration.getDefaultSiteId());
    }
    if (trackerConfiguration.getDefaultTokenAuth() != null && (action.getTokenAuth() == null || action.getTokenAuth()
      .trim()
      .isEmpty())) {
      query.append("&token_auth=").append(trackerConfiguration.getDefaultTokenAuth());
    }
    for (TrackingParameterMethod method : TRACKING_PARAMETER_METHODS) {
      appendParameter(method, action, query);
    }
    return query.toString();
  }

  private static void appendParameter(TrackingParameterMethod method, Action action, StringBuilder query) {
    try {
      Object parameterValue = method.getMethod().invoke(action);
      if (parameterValue != null) {
        method.validateParameterValue(parameterValue);
        query.append('&').append(method.getParameterName()).append('=');
        if (parameterValue instanceof Boolean) {
          query.append((boolean) parameterValue ? '1' : '0');
        } else {
          query.append(URLEncoder.encode(parameterValue.toString(), "UTF-8"));
        }
      }
    } catch (IllegalAccessException | InvocationTargetException | UnsupportedEncodingException e) {
      throw new ParameterException(e);
    }
  }

  private static List<TrackingParameterMethod> initializeTrackingParameterMethods() {
    List<TrackingParameterMethod> methods = new ArrayList<>(32);
    for (Field field : Action.class.getDeclaredFields()) {
      if (field.isAnnotationPresent(TrackingParameter.class)) {
        addMethods(methods, field, field.getAnnotation(TrackingParameter.class));
      }
    }
    return methods;
  }

  private static void addMethods(
    Collection<TrackingParameterMethod> methods, Member member, TrackingParameter trackingParameter
  ) {
    try {
      for (PropertyDescriptor pd : Introspector.getBeanInfo(Action.class).getPropertyDescriptors()) {
        if (member.getName().equals(pd.getName())) {
          String regex = trackingParameter.regex();
          methods.add(TrackingParameterMethod.builder()
            .parameterName(trackingParameter.name())
            .method(pd.getReadMethod())
            .pattern(regex == null || regex.isEmpty() || regex.trim().isEmpty() ? null : Pattern.compile(trackingParameter.regex()))
            .build());
        }
      }
    } catch (IntrospectionException e) {
      throw new InitializationFailedException("Could not initialize read methods", e);
    }
  }

}
