/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.interpolation;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.interpolation.IInterpolator;
import org.jamesii.core.math.interpolation.plugintype.AbstractInterpolationFactory;
import org.jamesii.core.math.interpolation.plugintype.InterpolationFactory;

import junit.framework.TestCase;

/**
 * This class plots a sinusoidal oscillation and interpolates it by means of
 * different methods.
 * 
 * @author Thomas Flisgen
 */
public class TestInterpolations extends TestCase {

  /**
   * Test all interpolations.
   */
  public static void testAllInterpolations() {
    List<InterpolationFactory> list =
        SimSystem.getRegistry().getFactoryList(
            AbstractInterpolationFactory.class, null);
    for (int i = 0; i < list.size(); i++) {
      IInterpolator inter = list.get(i).create(null);
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
      yValues.add(2.0);
      yValues.add(3.0);
      yValues.add(4.0);
      yValues.add(5.0);

      inter.setXValues(xValues);
      inter.setYValues(yValues);

      assertEquals(inter.getOrdinateAtPosition(0.0).compareTo(0.0), 0);
      assertEquals(inter.getOrdinateAtPosition(1.0).compareTo(1.0), 0);
      assertEquals(inter.getOrdinateAtPosition(2.0).compareTo(2.0), 0);
      assertEquals(inter.getOrdinateAtPosition(3.0).compareTo(3.0), 0);
      assertEquals(inter.getOrdinateAtPosition(4.0).compareTo(4.0), 0);
      assertEquals(inter.getOrdinateAtPosition(5.0).compareTo(5.0), 0);
    }
  }

}
