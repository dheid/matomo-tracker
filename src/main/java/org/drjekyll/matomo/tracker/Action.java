package org.drjekyll.matomo.tracker;

import org.drjekyll.matomo.tracker.parameters.AcceptLanguage;
import org.drjekyll.matomo.tracker.parameters.Country;
import org.drjekyll.matomo.tracker.parameters.CustomVariables;
import org.drjekyll.matomo.tracker.parameters.DeviceResolution;
import org.drjekyll.matomo.tracker.parameters.EcommerceItems;
import org.drjekyll.matomo.tracker.parameters.UniqueId;
import org.drjekyll.matomo.tracker.parameters.UnixTimestamp;
import org.drjekyll.matomo.tracker.parameters.VisitorId;

import lombok.Builder;
import lombok.Value;

/**
 * Describes the action that should be tracked by Matomo
 */
@Builder
@Value
public class Action {

  /**
   * The ID of the website we're tracking a visit/action for. Only needed, if no default site id is configured
   */
  @TrackingParameter(name = "idsite")
  Integer siteId;

  /**
   * The title of the action being tracked. It is possible to use slashes / to set one or several categories for this
   * action. For example, Help / Feedback will create the Action Feedback in the category Help.
   */
  @TrackingParameter(name = "action_name")
  String name;

  /**
   * The full URL for the current action.
   */
  @TrackingParameter(name = "url")
  String url;

  /**
   * The unique visitor ID. See {@link VisitorId}
   */
  @TrackingParameter(name = "_id")
  VisitorId visitorId;

  /**
   * The full HTTP Referrer URL. This value is used to determine how someone got to your website (ie, through a website,
   * search engine or campaign)
   */
  @TrackingParameter(name = "urlref")
  String referrerUrl;

  /**
   * Custom variables are custom name-value pairs that you can assign to your visitors (or page views).
   */
  @TrackingParameter(name = "_cvar")
  CustomVariables visitScopeCustomVariables;

  /**
   * The current count of visits for this visitor. To set this value correctly, it would be required to store the value
   * for each visitor in your application (using sessions or persisting in a database). Then you would manually
   * increment the counts by one on each new visit or "session", depending on how you choose to define a visit.
   */
  @TrackingParameter(name = "_idvc")
  Integer visitCount;

  /**
   * The UNIX timestamp of this visitor's previous visit. This parameter is used to populate the report Visitors >
   * Engagement > Visits by days since last visit.
   */
  @TrackingParameter(name = "_viewts")
  UnixTimestamp visitTimestamp;

  /**
   * The UNIX timestamp of this visitor's first visit. This could be set to the date where the user first started using
   * your software/app, or when he/she created an account.
   */
  @TrackingParameter(name = "_idts")
  UnixTimestamp firstVisitTimestamp;

  /**
   * The campaign name. This parameter will only be used for the first pageview of a visit.
   */
  @TrackingParameter(name = "_rcn")
  String campaignName;

  /**
   * The campaign keyword. This parameter will only be used for the first pageview of a visit.
   */
  @TrackingParameter(name = "_rck")
  String campaignKeyword;

  /**
   * The resolution of the device the visitor is using.
   */
  @TrackingParameter(name = "res")
  DeviceResolution deviceResolution;

  /**
   * The current hour (local time).
   */
  @TrackingParameter(name = "h")
  Integer hour;

  /**
   * The current minute (local time).
   */
  @TrackingParameter(name = "m")
  Integer minute;

  /**
   * The current second (local time).
   */
  @TrackingParameter(name = "s")
  Integer second;

  /**
   * Does the visitor use the Adobe Flash Plugin.
   */
  @TrackingParameter(name = "fla")
  Boolean flashPluginExists;

  /**
   * Does the visitor use the Java plugin.
   */
  @TrackingParameter(name = "java")
  Boolean javaPluginExists;

  /**
   * Does the visitor use Director plugin.
   */
  @TrackingParameter(name = "dir")
  Boolean directorPluginExists;

  /**
   * Does the visitor use Quicktime plugin.
   */
  @TrackingParameter(name = "qt")
  Boolean quicktimePluginExists;

  /**
   * Does the visitor use Realplayer plugin.
   */
  @TrackingParameter(name = "realp")
  Boolean realplayerPluginExists;

  /**
   * Does the visitor use a PDF plugin.
   */
  @TrackingParameter(name = "pdf")
  Boolean pdfPluginExists;

  /**
   * Does the visitor use a Windows Media plugin.
   */
  @TrackingParameter(name = "wma")
  Boolean wmaPluginExists;

  /**
   * Does the visitor use a Gears plugin.
   */
  @TrackingParameter(name = "gears")
  Boolean gearsPluginExists;

  /**
   * Does the visitor use a Silverlight plugin.
   */
  @TrackingParameter(name = "ag")
  Boolean silverlightPluginExists;

  /**
   * Does the visitor's client is known to support cookies.
   */
  @TrackingParameter(name = "cookie")
  Boolean supportsCookies;

  /**
   * An override value for the User-Agent HTTP header field. Which can also be set on the
   * {@link MatomoTracker.MatomoTrackerBuilder#userAgent(String)} directly.
   */
  @TrackingParameter(name = "ua")
  String userAgent;

  /**
   * An override value for the Accept-Language HTTP header field. This value is used to detect the visitor's country if
   * GeoIP is not enabled.
   */
  @TrackingParameter(name = "lang")
  AcceptLanguage language;

  /**
   * Defines the User ID for this request. User ID is any non-empty unique string identifying the user (such as an email
   * address or an username). When specified, the User ID will be "enforced". This means that if there is no recent
   * visit with this User ID, a new one will be created. If a visit is found in the last 30 minutes with your specified
   * User ID, then the new action will be recorded to this existing visit.
   */
  @TrackingParameter(name = "uid")
  String userId;

  /**
   * defines the visitor ID for this request.
   */
  @TrackingParameter(name = "cid")
  VisitorId customerId;

  /**
   * will force a new visit to be created for this action.
   */
  @TrackingParameter(name = "new_visit")
  Boolean newVisit;

  /**
   * Custom variables are custom name-value pairs that you can assign to your visitors (or page views).
   */
  @TrackingParameter(name = "cvar")
  CustomVariables pageScopeCustomVariables;

  /**
   * An external URL the user has opened. Used for tracking outlink clicks. We recommend to also set the url parameter
   * to this same value.
   */
  @TrackingParameter(name = "link")
  String link;

  /**
   * URL of a file the user has downloaded. Used for tracking downloads. We recommend to also set the url parameter to
   * this same value.
   */
  @TrackingParameter(name = "download")
  String download;

  /**
   * The Site Search keyword. When specified, the request will not be tracked as a normal pageview but will instead be
   * tracked as a Site Search request
   */
  @TrackingParameter(name = "search")
  String search;

  /**
   * When search is specified, you can optionally specify a search category with this parameter.
   */
  @TrackingParameter(name = "search_cat")
  String searchCategory;

  /**
   * When search is specified, we also recommend setting the search_count to the number of search results displayed on
   * the results page. When keywords are tracked with &search_count=0 they will appear in the "No Result Search Keyword"
   * report.
   */
  @TrackingParameter(name = "search_count")
  Integer searchCount;

  /**
   * Accepts a six character unique ID that identifies which actions were performed on a specific page view. When a page
   * was viewed, all following tracking requests (such as events) during that page view should use the same pageview ID.
   * Once another page was viewed a new unique ID should be generated. Use [0-9a-Z] as possible characters for the
   * unique ID.
   */
  @TrackingParameter(name = "pv_id")
  UniqueId pageViewId;

  /**
   * If specified, the tracking request will trigger a conversion for the goal of the website being tracked with this
   * ID.
   */
  @TrackingParameter(name = "idgoal")
  String goalId;

  /**
   * A monetary value that was generated as revenue by this goal conversion. Only used if idgoal is specified in the
   * request.
   */
  @TrackingParameter(name = "revenue")
  Double revenue;

  /**
   * The amount of time it took the server to generate this action, in milliseconds.
   */
  @TrackingParameter(name = "gt_ms")
  Long generationTime;

  /**
   * The charset of the page being tracked. Specify the charset if the data you send to Matomo is encoded in a different
   * character set than the default utf-8
   */
  @TrackingParameter(name = "cs")
  String charset;

  /**
   * can be optionally sent along any tracking request that isn't a page view. For example it can be sent together with
   * an event tracking request. The advantage being that should you ever disable the event plugin, then the event
   * tracking requests will be ignored vs if the parameter is not set, a page view would be tracked even though it isn't
   * a page view.
   */
  @TrackingParameter(name = "ca")
  Boolean customAction;

  /**
   * How long it took to connect to server.
   */
  @TrackingParameter(name = "pf_net")
  Long networkTime;

  /**
   * How long it took the server to generate page.
   */
  @TrackingParameter(name = "pf_srv")
  Long serverTime;

  /**
   * How long it takes the browser to download the response from the server.
   */
  @TrackingParameter(name = "pf_tfr")
  Long transferTime;

  /**
   * How long the browser spends loading the webpage after the response was fully received until the user can starting
   * interacting with it.
   */
  @TrackingParameter(name = "pf_dm1")
  Long domProcessingTime;

  /**
   * How long it takes for the browser to load media and execute any Javascript code listening for the DOMContentLoaded
   * event.
   */
  @TrackingParameter(name = "pf_dm2")
  Long domCompletionTime;

  /**
   * How long it takes the browser to execute Javascript code waiting for the window.load event.
   */
  @TrackingParameter(name = "pf_onl")
  Long onloadTime;

  /**
   * eg. Videos, Music, Games...
   */
  @TrackingParameter(name = "e_c")
  String eventCategory;

  /**
   * eg. Play, Pause, Duration, Add Playlist, Downloaded, Clicked...
   */
  @TrackingParameter(name = "e_a")
  String eventAction;

  /**
   * eg. a Movie name, or Song name, or File name...
   */
  @TrackingParameter(name = "e_n")
  String eventName;

  /**
   * Some numeric value that represents the event value.
   */
  @TrackingParameter(name = "e_n")
  Double eventValue;

  /**
   * The name of the content. For instance 'Ad Foo Bar'
   */
  @TrackingParameter(name = "c_n")
  String contentName;

  /**
   * The actual content piece. For instance the path to an image, video, audio, any text
   */
  @TrackingParameter(name = "c_p")
  String contentPiece;

  /**
   * The target of the content. For instance the URL of a landing page
   */
  @TrackingParameter(name = "c_t")
  String contentTarget;

  /**
   * The name of the interaction with the content. For instance a 'click'
   */
  @TrackingParameter(name = "c_i")
  String contentInteraction;

  /**
   * he unique string identifier for the ecommerce order (required when tracking an ecommerce order). you must set
   * &idgoal=0 in the request to track an ecommerce interaction: cart update or an ecommerce order.
   */
  @TrackingParameter(name = "ec_id")
  String ecommerceId;

  /**
   * Items in the Ecommerce order.
   */
  @TrackingParameter(name = "ec_items")
  EcommerceItems ecommerceItems;

  /**
   * The sub total of the order; excludes shipping.
   */
  @TrackingParameter(name = "ec_st")
  Double subTotal;

  /**
   * Tax amount of the order.
   */
  @TrackingParameter(name = "ec_tx")
  Double taxAmount;

  /**
   * Shipping cost of the order.
   */
  @TrackingParameter(name = "ec_sh")
  Double shippingCost;

  /**
   * Discount offered.
   */
  @TrackingParameter(name = "ec_dt")
  Double discount;

  /**
   * The UNIX timestamp of this customer's last ecommerce order. This value is used to process the "Days since last
   * order" report.
   */
  @TrackingParameter(name = "_ects")
  UnixTimestamp ecommerceTimestamp;

  /**
   * 32 character authorization key used to authenticate the API request. We recommend to create a user specifically for
   * accessing the Tracking API, and give the user only write permission on the website(s).
   */
  @TrackingParameter(name = "token_auth", regex = "[a-z0-9]{32}")
  String tokenAuth;

  /**
   * Override value for the visitor IP (both IPv4 and IPv6 notations supported).
   */
  @TrackingParameter(name = "cip")
  String customIp;

  /**
   * Override for the datetime of the request (normally the current time is used). This can be used to record visits and
   * page views in the past.
   */
  @TrackingParameter(name = "cdt")
  UnixTimestamp customDatetime;

  /**
   * An override value for the country. Must be a two letter ISO 3166 Alpha-2 country code.
   */
  @TrackingParameter(name = "country")
  Country country;

  /**
   * An override value for the region. Should be set to a ISO 3166-2 region code, which are used by MaxMind's and
   * DB-IP's GeoIP2 databases. See here for a list of them for every country.
   */
  @TrackingParameter(name = "region")
  String region;

  /**
   * An override value for the city. The name of the city the visitor is located in, eg, Tokyo.
   */
  @TrackingParameter(name = "city")
  String city;

  /**
   * An override value for the visitor's latitude, eg 22.456.
   */
  @TrackingParameter(name = "lat")
  Double latitude;

  /**
   * An override value for the visitor's longitude, eg 22.456.
   */
  @TrackingParameter(name = "long")
  Double longitude;

  /**
   * When set to false, the queued tracking handler won't be used and instead the tracking request will be executed
   * directly. This can be useful when you need to debug a tracking problem or want to test that the tracking works in
   * general.
   */
  @TrackingParameter(name = "queuedtracking")
  Boolean queuedTracking;

  /**
   * If set to 0 (send_image=0) Matomo will respond with a HTTP 204 response code instead of a GIF image. This improves
   * performance and can fix errors if images are not allowed to be obtained directly (eg Chrome Apps). Available since
   * Matomo 2.10.0
   * <p>
   * Default is {@code false}
   */
  @TrackingParameter(name = "send_image")
  @Builder.Default
  Boolean sendImage = false;

  /**
   * If set to true, the request will be a Heartbeat request which will not track any new activity (such as a new visit,
   * new action or new goal). The heartbeat request will only update the visit's total time to provide accurate "Visit
   * duration" metric when this parameter is set. It won't record any other data. This means by sending an additional
   * tracking request when the user leaves your site or app with &ping=1, you fix the issue where the time spent of the
   * last page visited is reported as 0 seconds.
   */
  @TrackingParameter(name = "ping")
  Boolean ping;

  /**
   * By default Matomo does not track bots. If you use the Tracking HTTP API directly, you may be interested in tracking
   * bot requests.
   */
  @TrackingParameter(name = "bots")
  Boolean bots;

}
