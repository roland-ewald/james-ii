/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.LinearAxis;

import junit.framework.TestCase;

/**
 * Test that tests the implemented methods of {@link LinearAxis}.
 * 
 * @author Stefan Rybacki
 * @author Enrico Seib
 * 
 */
public class LinearAxisTest extends TestCase {
  /**
   * axis to test
   */
  private IAxis axis;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    axis = new LinearAxis();
    assertNotNull(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.LinearAxis#transform(double)}
   * .
   */
  public final void testTransform() {
    axis.setMinimum(0);
    axis.setMaximum(1000);

    assertTrue(axis.transform(1000) == 1);
    assertTrue(axis.transform(0) == 0);
    assertTrue(axis.transform(1001) > 1);
    assertTrue(axis.transform(-1) < 0);

    for (int i = 0; i < 100; i++) {
      double value = Math.random() * 1000;
      assertEquals(value / 1000, axis.transform(value), 0.0000001);
    }

    axis.setMinimum(1);
    axis.setMaximum(1000);

    assertTrue(axis.transform(1000) == 1);
    assertTrue(axis.transform(1) == 0);
    assertTrue(axis.transform(1001) > 1);
    assertTrue(axis.transform(0) < 0);

    for (int i = 0; i < 100; i++) {
      double value = Math.random() * 999;
      assertEquals((value - 1) / 999, axis.transform(value), 0.0000001);
    }

  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.LinearAxis#getTickMarks(int)}
   * .
   */
  public final void testGetTickMarks() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.getTickMarkTest(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.LinearAxis#transformInv(double)}
   */
  public final void testTransformInv() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.transformInvTest(axis);
  }
}
