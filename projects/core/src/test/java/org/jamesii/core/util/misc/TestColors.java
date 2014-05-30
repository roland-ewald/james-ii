/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.awt.Color;

import org.jamesii.core.util.misc.Colors;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class TestColors.
 * 
 * @author Johannes Rössel
 */
public class TestColors extends TestCase {

  /**
   * Test get inverted color.
   */
  public void testGetInvertedColor() {
    Color a = new Color(0, 0, 0);
    Color b = Colors.getInvertedColor(a);
    assertEquals(b.getRed(), 255);
    assertEquals(b.getGreen(), 255);
    assertEquals(b.getBlue(), 255);

    a = new Color(123, 142, 12);
    b = Colors.getInvertedColor(a);
    assertEquals(b.getRed(), 132);
    assertEquals(b.getGreen(), 113);
    assertEquals(b.getBlue(), 243);
  }

}
