/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import java.awt.Color;

/**
 * Helper functions for colours.
 * 
 * @author Roland Ewald
 * @author Johannes Rössel
 */
public final class Colors {

  /**
   * Hidden constructor.
   */
  private Colors() {
  }

  /**
   * Converts integer number to hex number, adding a leading '0' if necessary.
   * 
   * @param colorNum
   *          the number of the colour
   * 
   * @return a string containing the hexa-decimal number
   */
  public static String convertIntegerToColorHex(int colorNum) {
    String stringVal = Integer.toHexString(colorNum);
    if (stringVal.length() == 1) {
      stringVal = "0" + stringVal;
    }
    return stringVal;
  }

  /**
   * Inverts a colour.
   * 
   * @param original
   *          the original colour
   * 
   * @return inverted colour [ (RGB + 255) % 256 ]
   */
  public static Color getInvertedColor(Color original) {

    Color inverted =
        new Color(255 - original.getRed(), 255 - original.getGreen(),
            255 - original.getBlue());

    return inverted;
  }

  /**
   * Returns the arithmetic mean of two colours, basically the colour you get if
   * you mix them in equal amounts.
   * 
   * @param a
   *          The first colour.
   * @param b
   *          The second colour.
   * 
   * @return A colour that is between both specified colours.
   */
  public static Color getIntermediateColor(Color a, Color b) {
    return new Color((a.getRed() + b.getRed()) / 2,
        (a.getGreen() + b.getGreen()) / 2, (a.getBlue() + b.getBlue()) / 2);
  }

}
