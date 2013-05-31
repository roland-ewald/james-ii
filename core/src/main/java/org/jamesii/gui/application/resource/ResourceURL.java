/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Class that represents a resource url used by
 * {@link ApplicationResourceManager} to access resources. It parses the url and
 * returns the domain, location and parameters encoded into the url.
 * <p>
 * <b>URL format:</b> <code>
 *  <pre>
 *  domain:location?parameter1=value1&amp;parameter2=value1&amp;parameter3=value3
 *  </pre>
 * </code> {@code location} and {@code value}s can be URL encoded using the
 * default system encoding
 * <p>
 * <b>Usage:</b> <code>
 *  <pre>
 *  ResourceURL r = new ResourceURL("image:/package/icon.jpg");
 *  String location = r.getLocation();
 *  String domain = r.getDomain();
 *  </pre>
 * </cod>
 * 
 * @author Stefan Rybacki
 */
public class ResourceURL {
  /**
   * the protocol of the url
   */
  private final String protocol;

  /**
   * the resource location
   */
  private final String location;

  /**
   * the parameters needed to access or create the resource
   */
  private final Map<String, String> params = new HashMap<>();

  /**
   * the url in string representation
   */
  private final String url;

  /**
   * Creates a new {@link ResourceURL} object. It parses the given {@code url}
   * and provides the {@code domain, location} and {@code parameters} given by
   * the url through its getter methods.
   * 
   * @param url
   *          the url to parse
   * @throws MalformedURLException
   * @throws UnsupportedEncodingException
   * 
   * @see #getDomain()
   * @see #getParameters()
   * @see #getDomain()
   */
  public ResourceURL(String url) throws MalformedURLException,
      UnsupportedEncodingException {
    this.url = url;
    // try to parse url
    String urlPattern = "^([^:]*)[:]([^?]*)[?]?(.*)?$";

    Pattern pattern = Pattern.compile(urlPattern);

    Matcher matcher = pattern.matcher(url);

    if (!matcher.find()) {
      throw new MalformedURLException("Provided resource URL is not valid!");
    }

    protocol = matcher.group(1);
    String encoding = System.getProperty("file.encoding");

    location = URLDecoder.decode(matcher.group(2), encoding);

    String parameters = matcher.group(3);

    // now match the parameters
    pattern = Pattern.compile("([^=]+)[=]([^&]*)[&]?");
    matcher = pattern.matcher(parameters);

    while (matcher.find()) {
      String name = matcher.group(1);
      String value = URLDecoder.decode(matcher.group(2), encoding);

      // add parameter and value to parameter map
      params.put(name, value);
    }

  }

  /**
   * @return the resource location
   */
  public final String getLocation() {
    return location;
  }

  /**
   * @return the resource domain
   */
  public final String getDomain() {
    return protocol;
  }

  /**
   * @return the parameters for the resource
   */
  public final Map<String, String> getParameters() {
    // defensive copying
    return new HashMap<>(params);
  }

  /**
   * @return the url as String representation
   */
  public final String getURL() {
    return url;
  }

  @Override
  public String toString() {
    return url;
  }
}
