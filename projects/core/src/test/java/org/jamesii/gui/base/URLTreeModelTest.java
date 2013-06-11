/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.gui.base.URLTreeModel;
import org.jamesii.gui.base.URLTreeNode;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class URLTreeModelTest extends TestCase {

  /**
   * The nodes to test.
   */
  private Map<String, URLTreeNode<Object>> nodes = new HashMap<>();

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    nodes.put("menu.main", new URLTreeNode<>("id", null));
    nodes.put("menu.main/file", new URLTreeNode<>("id", null));
    nodes.put("menu.main/edit", new URLTreeNode<>("id", null));
    nodes.put("menu.main/help", new URLTreeNode<>("id", null));
    nodes.put("toolbar.main", new URLTreeNode<>("id", null));
    nodes.put("toolbar.main/help", new URLTreeNode<>("id", null));
    nodes.put("toolbar.main/edit/save", new URLTreeNode<>("id", null));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeModel#URLTreeModel(org.jamesii.gui.base.URLTreeNode)}
   * .
   */
  public final void testURLTreeModel() {
    assertNotNull(new URLTreeModel<>(new URLTreeNode<>("root", null)));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeModel#addNode(java.lang.String, org.jamesii.gui.base.URLTreeNode)}
   * .
   */
  public final void testAddNode() {
    URLTreeModel<Object> m =
        new URLTreeModel<>(new URLTreeNode<>("root", null));
    for (String k : nodes.keySet()) {
      try {
        m.addNode(k, nodes.get(k));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeModel#getNode(java.lang.String)}.
   */
  public final void testGetNode() {
    URLTreeModel<Object> m =
        new URLTreeModel<>(new URLTreeNode<>("root", null));
    for (String k : nodes.keySet()) {
      try {
        m.addNode(k, nodes.get(k));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }

    for (String k : nodes.keySet()) {
      try {
        assertEquals(nodes.get(k), m.getNode(k + "/" + nodes.get(k).getId()));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeModel#createPath(java.lang.String)}.
   */
  public final void testCreatePath() {
    URLTreeModel<Object> m =
        new URLTreeModel<>(new URLTreeNode<>("root", null));
    String[] n =
        new String[] { "test", "path", "with", "alot", "sub", "nodes", "to",
            "create" };
    String path = "";
    for (String s : n) {
      path += "/" + s;
    }

    path = path.substring(1);

    try {
      m.createPath(path);
    } catch (MalformedURLException | UnsupportedEncodingException e) {
      fail(e.getMessage());
    }

    // check whether all nodes exist
    path = "";
    for (String s : n) {
      path += "/" + s;
      try {
        assertNotNull(m.getNode(path.substring(1)));
      } catch (MalformedURLException | UnsupportedEncodingException e) {
        fail(e.getMessage());
      }
    }
  }

}
