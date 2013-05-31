/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 * 
 */
package org.jamesii.gui.visualization.chart.axes;

import org.jamesii.gui.visualization.chart.axes.AnyLogarithmicAxis;
import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.MidLogarithmicAxis;
import org.jamesii.gui.visualization.chart.axes.SimpleLogarithmicAxis;

import junit.framework.TestCase;

/**
 * Test that tests the implemented methods of {@link AnyLogarithmicAxis}.
 * 
 * @author Enrico Seib
 */
public class AnyLogarithmicAxisTest extends TestCase {

  /**
   * The axis.
   */
  private AnyLogarithmicAxis axis;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    axis = new AnyLogarithmicAxis();
    assertNotNull(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AnyLogarithmicAxis#transform(double)}
   * .
   */
  public void testTransform() {
    for (int i = 0; i < 1000; i++) {
      double min = Math.random() * 100000 - 50000;
      double max = Math.random() * 100000 + min;

      axis.setMinimumMaximum(min, max);
      axis.setLogStartPointValue(Math.random() * (max - min) + min);

      assertEquals(0, axis.transform(min), 0.00000001);
      assertEquals(1, axis.transform(max), 0.00000001);
    }

    // special cases
    for (int i = 0; i < 100; i++) {
      double min = Math.random() * 100000 - 50000;
      double max = Math.random() * 100000 + min;

      IAxis referenceAxis = new MidLogarithmicAxis();
      axis.setMinimumMaximum(min, max);
      axis.setLogStartPointValue((max - min) / 2 + min);

      referenceAxis.setMinimum(min);
      referenceAxis.setMaximum(max);

      for (int j = 0; j < 1000; j++) {
        double v = Math.random() * (max - min) + min;
        assertEquals(referenceAxis.transform(v), axis.transform(v), 0.00000001);
      }
    }

    for (int i = 0; i < 100; i++) {
      double min = Math.random() * 100000 - 50000;
      double max = Math.random() * 100000 + min;

      IAxis referenceAxis = new SimpleLogarithmicAxis();
      axis.setMinimumMaximum(min, max);
      axis.setLogStartPointValue(min);

      referenceAxis.setMinimum(min);
      referenceAxis.setMaximum(max);

      for (int j = 0; j < 1000; j++) {
        double v = Math.random() * (max - min) + min;
        assertEquals(referenceAxis.transform(v), axis.transform(v), 0.00000001);
      }
    }

    // small test for symmetry around the basePointValue

    double min = 0;
    double max = 100;

    axis.setMinimumMaximum(min, max);
    for (int logCenter = (int) min; logCenter < max; logCenter++) {
      axis.setLogStartPointValue(logCenter);
      for (int k = 0; k < (max - logCenter); k++) {
        assertEquals(axis.transform(logCenter) - axis.transform(logCenter - k),
            axis.transform(logCenter + k) - axis.transform(logCenter),
            0.0000000001);
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AnyLogarithmicAxis#getTickMarks(int)}
   */
  public final void testGetTickMarks() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    axis.setLogStartPointValue(Math.random()
        * (axis.getMaximum() - axis.getMinimum()) + axis.getMinimum());
    AbstractAxisTest.getTickMarkTest(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AnyLogarithmicAxis#transformInv(double)}
   */
  public final void testTransformInv() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    axis.setLogStartPointValue(Math.random()
        * (axis.getMaximum() - axis.getMinimum()) + axis.getMinimum());
    AbstractAxisTest.transformInvTest(axis);
  }
}
