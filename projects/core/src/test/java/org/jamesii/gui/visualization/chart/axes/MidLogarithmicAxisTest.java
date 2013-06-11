/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.MidLogarithmicAxis;

import junit.framework.TestCase;

/**
 * The Class MidLogarithmicAxisTest.
 */
public class MidLogarithmicAxisTest extends TestCase {

  /**
   * Test that tests the implemented methods of {@link MidLogarithmicAxis}.
   * 
   * @author Enrico Seib
   */

  private IAxis axis;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    axis = new MidLogarithmicAxis();
    assertNotNull(axis);

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.MidLogarithmicAxis#transform(double)}
   * .
   */
  public final void testTransform() {
    long roundFactor = 1000000000;
    axis.setMinimum(0);
    axis.setMaximum(10);

    assertEquals(0.0, axis.transform(0), 0.000000001);
    assertEquals(1, axis.transform(10), 0.000000001);

    for (int i = 0; i <= 100; i++) {

      assertEquals(
          Math.round(Math.abs(0.5 - (axis.transform(5 - (i / 10d))))
              * roundFactor)
              / roundFactor,
          Math.round((Math.abs(axis.transform(5 + (i / 10d)) - 0.5))
              * roundFactor)
              / roundFactor);
    }

    axis = new MidLogarithmicAxis();
    assertNotNull(axis);

    axis.setMinimum(10);
    axis.setMaximum(60);
    assertEquals(0.5, axis.transform(35));

    assertEquals(0.0, axis.transform(10));
    assertEquals(1.0, axis.transform(60));

    for (int i = 0; i <= 100; i++) {

      assertEquals(
          Math.round(Math.abs(0.5 - (axis.transform(5 - (i / 10d))))
              * roundFactor)
              / roundFactor,
          Math.round(Math.abs(axis.transform(5 + (i / 10d)) - 0.5)
              * roundFactor)
              / roundFactor);
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.MidLogarithmicAxis#transformInv(double)}
   */
  public final void testTransformInv() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.transformInvTest(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.MidLogarithmicAxis#getTickMarks(int)}
   */
  public final void testGetTickMarks() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.getTickMarkTest(axis);
  }

}
