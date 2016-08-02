package com.rsicms.rsuite.utils.webService;

import java.io.IOException;

import com.rsicms.rsuite.utils.messsageProps.LibraryMessageProperties;

/**
 * Serves up formatted messages from messages.properties.
 */
public class WebServiceUtilsMessageProperties extends LibraryMessageProperties {

  public WebServiceUtilsMessageProperties() throws IOException {
    super(WebServiceUtilsMessageProperties.class);
  }

}
