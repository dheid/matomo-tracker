# :mag: Matomo Tracking API Java Client

[![Maven Central](https://img.shields.io/maven-central/v/org.drjekyll/matomo-tracker.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.drjekyll%22%20AND%20a:%22matomo-tracker%22)
[![Java CI with Maven](https://github.com/dheid/matomo-tracker/actions/workflows/build.yml/badge.svg)](https://github.com/dheid/matomo-tracker/actions/workflows/build.yml)
[![ko-fi](https://ko-fi.com/img/githubbutton_sm.svg)](https://ko-fi.com/W7W3EER56)

A lightweight client library for JVM-based applications that helps you to track page views, events, visits using the
Matomo Tracking API:

* Automatically performs bulk requests with a configurable delay (default: 1 second)
* Supports nearly all tracking parameters
* The request API is designed with fluent builders and immutability.
* Performs asynchronous calls so that your application is not influenced by the Matomo endpoint speed
* Easy to use
* Well documented with Javadoc
* Ensures correct values are sent to Matomo Tracking API
* Includes debug logging
* Minimal dependencies (only SLF4J and JSR-305 annotations)
* Easy to integrate in frameworks, e.g. Spring: Just create the MatomoTracker Spring bean and use it in other beans

## :wrench: Usage

Include the dependency using Maven

```xml

<dependency>
  <groupId>org.drjekyll</groupId>
  <artifactId>matomo-tracker</artifactId>
  <version>1.1.1</version>
</dependency>
```

or Gradle with Groovy DSL:

```groovy
implementation 'org.drjekyll:matomo-tracker:1.1.1'
```

or Gradle with Kotlin DSL:

```kotlin
implementation("org.drjekyll:matomo-tracker:1.1.1")
```

Run your build tool and add the tracker like in the following example:

```java

TrackerConfiguration config=TrackerConfiguration.builder()
  .apiEndpoint(URI.create("https://your-domain.net/matomo/matomo.php"))
  .defaultSiteId(42) // if not explicitly specified by action
  .build();

// Prepare the tracker (stateless - can be used for multiple actions)
  MatomoTracker tracker=new MatomoTracker(config);

// Track an action
  CompletableFuture<Void> future=tracker.track(Action.builder()
  .name("User Profile / Upload Profile Picture")
  .url("https://your-domain.net/user/profile/picture")
  .visitorId(VisitorId.fromHash("some@email-adress.org".hashCode()))
  // ...
  .build());

// If you want to ensure the request has been handled:
if(future.isCompletedExceptionally()){
  // log, throw, ...
}
```

This example sends a request to a Matomo endpoint. Usually many more parameters should be set.

## :gear: Tracker Parameters

The Matomo Tracker currently supports the following builder methods:

* `.apiEndpoint(...)` An `URI` object that points to the Matomo Tracking API endpoint of your Matomo installation. Must be set.
* `.defaultSiteId(...)` If you provide a default site id, it will be taken if the action does not contain a site id.
* `.defaultTokenAuth(...)` If you provide a default token auth, it will be taken if the action does not contain a token
  auth.
* `.delay(...)` The duration on how long the tracker collects actions until they will be sent out as a bulk request.
  Default: 1 seconds
* `.enabled(...)` The tracker is enabled per default. You can disable it per configuration with this flag.
* `.logFailedTracking(...)` Will send errors to the log if the Matomo Tracking API responds with an errornous HTTP code
* `.connectTimeout(...)` allows you to change the default connection timeout of 10 seconds. 0 is
  interpreted as infinite, null uses the system default
* `.socketTimeout(...)` allows you to change the default socket timeout of 10 seconds. 0 is
  interpreted as infinite, null uses the system default
* `.userAgent(...)` used by the request made to the endpoint is `MatomoJavaClient` per default. You can change it by using this builder method.
* `.proxyHost(...)` The hostname or IP address of an optional HTTP proxy. `proxyPort` must be
  configured as well
* `.proxyPort(...)` The port of an HTTP proxy. `proxyHost` must be configured as well.
* `.proxyUserName(...)` If the HTTP proxy requires a user name for basic authentication, it can be
  configured with this method. Proxy host, port and password must also be set.
* `.proxyPassword(...)` The corresponding password for the basic auth proxy user. The proxy host,
  port and user name must be set as well.

## :sunglasses: Development

To build and locally install the library and run the tests, just call

    mvn install

## :handshake: Contributing

Please read [the contribution document](CONTRIBUTING.md) for details on our code of conduct, and the
process for submitting pull requests to us.

## :notebook: Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see
the [tags on this repository](https://github.com/dheid/matomo-tracker/tags).

## :scroll: License

This project is licensed under the LGPL License - see the [license](LICENSE) file for details.

## :loudspeaker: Release Notes

### 1.1.1

Requests will be sent as bulk requests now. The tracker waits a configurable duration (default: 1 second) until it sends
out the collected requests.

### 1.0.3

Allow to enable connection error logging

### 1.0.2

First public version
