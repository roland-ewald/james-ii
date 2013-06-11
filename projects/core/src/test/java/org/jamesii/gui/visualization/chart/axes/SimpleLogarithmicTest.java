/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import org.jamesii.gui.visualization.chart.axes.IAxis;
import org.jamesii.gui.visualization.chart.axes.SimpleLogarithmicAxis;

import junit.framework.TestCase;

/**
 * Test that tests the {@link IAxis#transform(double)} and
 * {@link IAxis#getTickMarks(int)} method for {@link SimpleLogarithmicAxis}.
 * 
 * @author Stefan Rybacki
 */
public class SimpleLogarithmicTest extends TestCase {
  /**
   * Axis to test
   */
  private IAxis axis;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    axis = new SimpleLogarithmicAxis();
    assertNotNull(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.SimpleLogarithmicAxis#transform(double)}
   */
  public final void testTransform() {
    axis.setMinimum(0);
    axis.setMaximum(10);

    for (int i = -10; i <= 100; i++) {
      // since we chose a scale from 0 to 10 the following needs to be true:
      // y = 10^x
      if (i / 10d < axis.getMinimum()) {
        assertTrue(axis.transform(i / 10d) < 0);
      } else {
        assertEquals(Math.log(i / 10d + 1) / Math.log(11),
            axis.transform(i / 10d), 0.00000000001);
      }
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.SimpleLogarithmicAxis#getTickMarks(int)}
   */
  public final void testGetTickMarks() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.getTickMarkTest(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.SimpleLogarithmicAxis#transformInv(double)}
   */
  public final void testTransformInv() {
    axis.setMinimum(Math.random() * 50 - 25);
    axis.setMaximum(axis.getMinimum() + Math.random() * 1000 + 1);
    AbstractAxisTest.transformInvTest(axis);
  }
}
