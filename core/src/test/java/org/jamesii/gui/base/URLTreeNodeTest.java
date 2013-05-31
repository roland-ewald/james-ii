/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.jamesii.gui.base.URLTreeNode;
import org.jamesii.gui.base.URLTreeNodePlacement;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class URLTreeNodeTest extends TestCase {

  /**
   * The children.
   */
  private List<URLTreeNode<Object>> children = new ArrayList<>();

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    for (int i = 0; i < 100; i++) {
      URLTreeNode<Object> o = new URLTreeNode<>("nodeID" + i, null, null);
      assertNotNull(o);
      children.add(o);
    }

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#URLTreeNode(java.lang.String, org.jamesii.gui.base.URLTreeNode)}
   * .
   */
  public final void testURLTreeNodeStringURLTreeNodeOfE() {
    assertNotNull(new URLTreeNode<>("nodeID", null));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#URLTreeNode(java.lang.String, org.jamesii.gui.base.URLTreeNode, java.lang.Object)}
   * .
   */
  public final void testURLTreeNodeStringURLTreeNodeOfEE() {
    assertNotNull(new URLTreeNode<>("nodeID", null, null));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#getAttachedObject()}.
   */
  public final void testGetAttachedObject() {
    Object object = new Object();
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, object);
    assertNotNull(o);
    assertEquals(object, o.getAttachedObject());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#setAttachedObject(java.lang.Object)}
   * .
   */
  public final void testSetAttachedObject() {
    Object object = new Object();
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, object);
    assertNotNull(o);
    assertEquals(object, o.getAttachedObject());
    object = new Object();
    o.setAttachedObject(object);
    assertEquals(object, o.getAttachedObject());
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#getId()}.
   */
  public final void testGetId() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    assertEquals("nodeID", o.getId());
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#children()}.
   */
  public final void testChildren() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
    }

    // check for equivalent lists
    Enumeration<URLTreeNode<Object>> enu = o.children();
    Iterator<URLTreeNode<Object>> it = children.iterator();

    while (enu.hasMoreElements()) {
      try {
        assertEquals(it.next(), enu.nextElement());
      } catch (Exception e) {
        fail(e.getMessage());
      }
    }
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#getChildAt(int)}.
   */
  public final void testGetChildAt() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
    }

    for (int i = 0; i < o.getChildCount(); i++) {
      assertEquals(children.get(i), o.getChildAt(i));
    }
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#getChildCount()}.
   */
  public final void testGetChildCount() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
    }

    assertEquals(children.size(), o.getChildCount());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#getIndex(javax.swing.tree.TreeNode)}
   * .
   */
  public final void testGetIndex() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
    }

    for (int i = 0; i < children.size(); i++) {
      assertEquals(i, o.getIndex(children.get(i)));
    }
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#getParent()}.
   */
  public final void testGetParent() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    assertNull(o.getParent());

    o = new URLTreeNode<>("nodeID", children.get(0), null);
    assertNotNull(o);
    assertEquals(children.get(0), o.getParent());
  }

  /**
   * Test method for {@link org.jamesii.gui.base.URLTreeNode#isLeaf()}.
   */
  public final void testIsLeaf() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    assertTrue(o.isLeaf());

    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
    }

    assertFalse(o.isLeaf());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#addNode(org.jamesii.gui.base.URLTreeNode, org.jamesii.gui.base.URLTreeNodePlacement)}
   * .
   */
  public final void testAddNode() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);
    for (URLTreeNode<Object> c : children) {
      o.addNode(c, null);
      assertTrue(o.getIndex(c) >= 0);
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.base.URLTreeNode#getPlacement(org.jamesii.gui.base.URLTreeNode)}
   * .
   */
  public final void testGetPlacement() {
    URLTreeNode<Object> o = new URLTreeNode<>("nodeID", null, null);
    assertNotNull(o);

    for (URLTreeNode<Object> c : children) {
      URLTreeNodePlacement p = new URLTreeNodePlacement("after", "nodeID");
      o.addNode(c, p);
      assertEquals(p, o.getPlacement(c));
    }
  }

}
