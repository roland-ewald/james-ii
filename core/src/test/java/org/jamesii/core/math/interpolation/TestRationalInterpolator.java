/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.interpolation.IInterpolator;
import org.jamesii.core.math.interpolation.RationalFunctionInterpolator;

import junit.framework.TestCase;

/**
 * The Class TestRationalInterpolator.
 */
public class TestRationalInterpolator extends TestCase {

  /**
   * Test trigonometric interpolator.
   */
  public static void testAllInterpolations() {
    IInterpolator inter = new RationalFunctionInterpolator();
    List<Double> xValues = new ArrayList<>();
    xValues.add(0.0);
    xValues.add(1.0);
    xValues.add(2.0);
    xValues.add(3.0);
    xValues.add(4.0);
    xValues.add(5.0);

    List<Double> yValues = new ArrayList<>();
    yValues.add(0.0);
    yValues.add(1.0);
    yValues.add(2.5);
    yValues.add(3.67);
    yValues.add(45.0);
    yValues.add(100.0);

    inter.setXValues(xValues);
    inter.setYValues(yValues);

    assertEquals(inter.getOrdinateAtPosition(0.0).compareTo(0.0), 0);
    assertEquals(inter.getOrdinateAtPosition(1.0).compareTo(1.0), 0);
    assertEquals(inter.getOrdinateAtPosition(2.0).compareTo(2.5), 0);
    assertEquals(inter.getOrdinateAtPosition(3.0).compareTo(3.67), 0);
    assertEquals(inter.getOrdinateAtPosition(4.0).compareTo(45.0), 0);
    assertEquals(inter.getOrdinateAtPosition(5.0).compareTo(100.0), 0);
  }
}
