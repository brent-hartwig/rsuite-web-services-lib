package com.rsicms.rsuite.utils.webService;

public interface WebServiceUtilsConstants {

  /**
   * Property name identifying the how long a notification should be displayed, in seconds.
   */
  String PROP_NAME_NOTIFICATION_DURATION_IN_SECONDS = "rsuite.wsu.notification.duration.in.seconds";

  /**
   * Default value for the {@link #PROP_NAME_NOTIFICATION_DURATION_IN_SECONDS} property.
   */
  int DEFAULT_NOTIFICATION_DURATION_IN_SECONDS = 10;

  /**
   * Property name specifying whether less severe messages should also be displayed in the CMS UI
   * when an error or warning message is displayed.
   */
  String PROP_NAME_ALSO_DISPLAY_LESS_SEVERE_MESSAGES = "rsuite.wsu.also.display.less.severe.messages";

  /**
   * Default value for the {@link #PROP_NAME_ALSO_DISPLAY_LESS_SEVERE_MESSAGES} property.
   */
  boolean DEFAULT_ALSO_DISPLAY_LESS_SEVERE_MESSAGES = false;

}
