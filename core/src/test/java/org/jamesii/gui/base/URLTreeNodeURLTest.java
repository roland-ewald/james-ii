/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import static org.jamesii.gui.base.URLTreeNodePlacement.*;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.gui.base.URLTreeNodePlacement;
import org.jamesii.gui.base.URLTreeNodeURL;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class URLTreeNodeURLTest.
 */
public class URLTreeNodeURLTest extends TestCase {

  /**
   * The urls to test.
   */
  private List<String> urls = new ArrayList<>();

  /**
   * The paths to test.
   */
  private Map<String, String[]> paths = new HashMap<>();

  /**
   * The placements to test.
   */
  private Map<String, URLTreeNodePlacement> placements = new HashMap<>();

  /**
   * Sets the up.
   * 
   * @throws Exception
   *           the exception
   */
  @Override
  protected void setUp() throws Exception {
    super.setUp();
    String s;

    s = "main.menu/file";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "file" });
    placements.put(s, null);

    s = "main.menu/edit?after=main.menu/file";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "edit" });
    placements.put(s, new URLTreeNodePlacement(AFTER, "main.menu/file"));

    s = "main.menu/window?end";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "window" });
    placements.put(s, new URLTreeNodePlacement(END, null));

    s = "main.menu/help?last";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "help" });
    placements.put(s, new URLTreeNodePlacement(LAST, null));

    s = "main.menu/help?first";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "help" });
    placements.put(s, new URLTreeNodePlacement(FIRST, null));

    s = "main.menu/help?start";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "help" });
    placements.put(s, new URLTreeNodePlacement(START, null));

    s = "main.menu/window?before=main.menu/help";
    urls.add(s);
    paths.put(s, new String[] { "main.menu", "window" });
    placements.put(s, new URLTreeNodePlacement(BEFORE, "main.menu/help"));
  }

  /**
   * Test action url.
   */
  public final void testActionURL() {
    for (String s : urls) {
      try {
        assertNotNull(new URLTreeNodeURL(s));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test get path. Tests {@link URLTreeNodeURL#getPath()}
   */
  public final void testGetPath() {
    for (String s : urls) {
      try {
        URLTreeNodeURL url = new URLTreeNodeURL(s);
        assertTrue(Arrays.equals(url.getPath(), paths.get(s)));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test get placement.
   */
  public final void testGetPlacement() {
    for (String s : urls) {
      try {
        URLTreeNodePlacement p = new URLTreeNodeURL(s).getPlacement();
        assertTrue(p == placements.get(s)
            || p.getWhere().equalsIgnoreCase(placements.get(s).getWhere())
            || (p.getNodeId() == null && placements.get(s).getNodeId() == null)
            || p.getNodeId().equals(placements.get(s).getNodeId()));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test get url. Tests {@link URLTreeNodeURL#getURL()}
   */
  public final void testGetURL() {
    for (String s : urls) {
      try {
        assertEquals(s, new URLTreeNodeURL(s).getURL());
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

}
