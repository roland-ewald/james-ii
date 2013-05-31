/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class ResourceURLTest extends TestCase {

  /**
   * The correct URLs to test.
   */
  private static final List<String> correctURLs = new ArrayList<>();

  /**
   * The domains to test.
   */
  private static final Map<String, String> domains = new HashMap<>();

  /**
   * The locations to test.
   */
  private static final Map<String, String> locations = new HashMap<>();

  /**
   * The parameters to test.
   */
  private static final Map<String, Map<String, String>> params =
      new HashMap<>();

  /**
   * The incorrect URLs to test.
   */
  private static final String[] incorrectURLs =
      new String[] { "/location/file" };

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    Map<String, String> map = new HashMap<>();

    String s = "domain:location";
    correctURLs.add(s);
    domains.put(s, "domain");
    locations.put(s, "location");
    params.put(s, map);

    s = "domain:location?";
    correctURLs.add(s);
    domains.put(s, "domain");
    locations.put(s, "location");
    params.put(s, map);

    s = "domain:location?param1=1";
    correctURLs.add(s);
    domains.put(s, "domain");
    locations.put(s, "location");

    map = new HashMap<>();
    map.put("param1", "1");
    params.put(s, map);

    s =
        "web:http://www.google.de/search?hl=de&q=James+II&btnG=Google-Suche&meta=";
    correctURLs.add(s);
    domains.put(s, "web");
    locations.put(s, "http://www.google.de/search");
    map = new HashMap<>();
    map.put("hl", "de");
    map.put("q", "James II");
    map.put("btnG", "Google-Suche");
    map.put("meta", "");
    params.put(s, map);

    String encoding = System.getProperty("file.encoding");
    s =
        "web:"
            + URLEncoder
                .encode(
                    "http://www.google.de/search?hl=de&q=org.jamesii+II&btnG=Google-Suche&meta=",
                    encoding) + "?param1=1&param2=2&param3=2+3";
    correctURLs.add(s);
    domains.put(s, "web");
    locations
        .put(s,
            "http://www.google.de/search?hl=de&q=org.jamesii+II&btnG=Google-Suche&meta=");
    map = new HashMap<>();
    map.put("param1", "1");
    map.put("param2", "2");
    map.put("param3", "2 3");
    params.put(s, map);

    s = "domain:location?param1";
    correctURLs.add(s);
    domains.put(s, "domain");
    locations.put(s, "location");
    map = new HashMap<>();
    map.put("param1", null);
    params.put(s, map);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.ResourceURL#ResourceURL(java.lang.String)}
   * .
   */
  public final void testResourceURL() {
    // test correct urls
    for (String s : correctURLs) {
      try {
        assertNotNull(new ResourceURL(s));
      } catch (Exception e) {
        fail(e.getMessage());
      }
    }

    // test incorrect urls
    for (String s : incorrectURLs) {
      try {
        new ResourceURL(s);
        fail("URL generated from incorrect url : " + s);
      } catch (Exception e) {
        assertTrue(e instanceof MalformedURLException);
      }
    }

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.ResourceURL#getLocation()}.
   */
  public final void testGetLocation() {
    for (String s : correctURLs) {
      try {
        ResourceURL resourceURL = new ResourceURL(s);
        assertEquals(locations.get(s), resourceURL.getLocation());
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.ResourceURL#getDomain()}.
   */
  public final void testGetDomain() {
    for (String s : correctURLs) {
      try {
        ResourceURL resourceURL = new ResourceURL(s);
        assertEquals(domains.get(s), resourceURL.getDomain());
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.ResourceURL#getParameters()}.
   */
  public final void testGetParameters() {
    for (String s : correctURLs) {
      try {
        ResourceURL resourceURL = new ResourceURL(s);
        Map<String, String> parameters = resourceURL.getParameters();
        for (String k : parameters.keySet()) {
          assertTrue(params.get(s).containsKey(k));
          assertEquals(params.get(s).get(k), parameters.get(k));
        }
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.ResourceURL#getURL()} .
   */
  public final void testGetURL() {
    for (String s : correctURLs) {
      try {
        ResourceURL resourceURL = new ResourceURL(s);
        assertEquals(s, resourceURL.getURL());
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

}
