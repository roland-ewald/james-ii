/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.base;

import static org.jamesii.gui.base.URLTreeNodePlacement.*;

import org.jamesii.gui.base.URLTreeNodePlacement;

import junit.framework.TestCase;

/**
 * The Class URLTreeNodePlacementTest.
 * 
 * @author Stefan Rybacki
 */
public class URLTreeNodePlacementTest extends TestCase {

  /**
   * The allowed parameters to test.
   */
  private String[] allowed = new String[] { START, END, FIRST, LAST, BEFORE,
      AFTER };

  /**
   * The not allowed parameters to test.
   */
  private String[] notAllowed = new String[] { "FOO", "BAR", "FOOBAR" };

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test action placement.
   */
  public final void testActionPlacement() {
    for (String s : allowed) {
      assertNotNull(new URLTreeNodePlacement(s, null));
    }
    for (String s : notAllowed) {
      try {
        new URLTreeNodePlacement(s, null);
        fail("Created placement with wrong modifier! (modifier: " + s + ")");
      } catch (Exception e) {
      }
    }
  }

  /**
   * Test get where. Tests {@link URLTreeNodePlacement#getWhere()}
   */
  public final void testGetWhere() {
    for (String s : allowed) {
      assertEquals(s.toLowerCase(), new URLTreeNodePlacement(s, null)
          .getWhere().toLowerCase());
    }
  }

  /**
   * Test get action id. Tests {@link URLTreeNodePlacement#getNodeId()}
   */
  public final void testGetActionId() {
    for (String s : allowed) {
      assertEquals(null, new URLTreeNodePlacement(s, null).getNodeId());
      assertEquals("123", new URLTreeNodePlacement(s, "123").getNodeId());
      String a = new String("Hello World");
      assertEquals(a, new URLTreeNodePlacement(s, a).getNodeId());
    }
  }

  /**
   * Test is placement. Tests {@link URLTreeNodePlacement#isPlacement(String)}
   */
  public final void testIsPlacement() {
    for (String s : allowed) {
      assertTrue(isPlacement(s));
    }
    for (String s : notAllowed) {
      assertFalse(isPlacement(s));
    }
  }

  /**
   * Test is end. Tests {@link URLTreeNodePlacement#isEnd()}
   */
  public final void testIsEnd() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isEnd() && s.equals(END))
          || (!s.equals(END) && !new URLTreeNodePlacement(s, null).isEnd()));
    }
  }

  /**
   * Test is last. Tests {@link URLTreeNodePlacement#isLast()}
   */
  public final void testIsLast() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isLast() && s.equals(LAST))
          || (!s.equals(LAST) && !new URLTreeNodePlacement(s, null).isLast()));
    }
  }

  /**
   * Test is after. Tests {@link URLTreeNodePlacement#isAfter()}
   */
  public final void testIsAfter() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isAfter() && s
          .equals(AFTER))
          || (!s.equals(AFTER) && !new URLTreeNodePlacement(s, null).isAfter()));
    }
  }

  /**
   * Test is first. Tests {@link URLTreeNodePlacement#isFirst()}
   */
  public final void testIsFirst() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isFirst() && s
          .equals(FIRST))
          || (!s.equals(FIRST) && !new URLTreeNodePlacement(s, null).isFirst()));
    }
  }

  /**
   * Test is before. Tests {@link URLTreeNodePlacement#isBefore()}
   */
  public final void testIsBefore() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isBefore() && s
          .equals(BEFORE))
          || (!s.equals(BEFORE) && !new URLTreeNodePlacement(s, null)
              .isBefore()));
    }
  }

  /**
   * Test is start. Tests {@link URLTreeNodePlacement#isStart()}
   */
  public final void testIsStart() {
    for (String s : allowed) {
      assertTrue((new URLTreeNodePlacement(s, null).isStart() && s
          .equals(START))
          || (!s.equals(START) && !new URLTreeNodePlacement(s, null).isStart()));
    }
  }

}
