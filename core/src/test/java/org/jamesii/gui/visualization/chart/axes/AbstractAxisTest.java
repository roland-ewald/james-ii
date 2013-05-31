/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.chart.axes;

import java.util.List;

import org.jamesii.gui.visualization.chart.axes.AbstractAxis;
import org.jamesii.gui.visualization.chart.axes.IAxis;

import junit.framework.TestCase;

/**
 * Test that tests the implemented methods of {@link AbstractAxis}.
 * 
 * @author Stefan Rybacki
 * 
 */
public class AbstractAxisTest extends TestCase {
  /**
   * A test implementation of {@link AbstractAxis}
   * 
   * @author Stefan Rybacki
   * 
   */
  private static class TestAxis extends AbstractAxis {

    @Override
    public double transform(double value) {
      return 0;
    }

    @Override
    public double transformInv(double value) {
      // TODO Auto-generated method stub
      return 0;
    }

  }

  /**
   * the axis to test on
   */
  private IAxis axis;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    axis = new TestAxis();
    assertNotNull(axis);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AbstractAxis#getMaximum()}
   * . Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AbstractAxis#setMaximum(double)}
   * .
   */
  public final void testGetMaximum() {
    axis.setMinimum(0);
    for (int i = 0; i < 100; i++) {
      double max = Math.random() * 1000 + axis.getMinimum() + 1;
      axis.setMaximum(max);
      assertEquals(max, axis.getMaximum());
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AbstractAxis#getMinimum()}
   * . Test method for
   * {@link org.jamesii.gui.visualization.chart.axes.AbstractAxis#setMinimum(double)}
   * .
   */
  public final void testGetSetMinimum() {
    axis.setMaximum(1000000);
    for (int i = 0; i < 100; i++) {
      double min = axis.getMaximum() - Math.random() * 1000 - 1;
      axis.setMinimum(min);
      assertEquals(min, axis.getMinimum());
    }
  }

  /**
   * Standard test method can be used by any axis test to test their
   * {@link IAxis#getTickMarks(int)} method.
   * <p>
   * <b>Note:</b> you have to initialize your axis accordingly which means you
   * have to set minimum and maximum values
   * 
   * @param axis
   *          the axis to test
   */
  public static final void getTickMarkTest(IAxis axis) {
    List<Double> list = axis.getTickMarks(10);
    assertNotNull(list);

    for (int i = 0; i < list.size(); i++) {
      assertEquals((double) i / (list.size() - 1), axis.transform(list.get(i)),
          0.00001);
    }
  }

  /**
   * General test for the methods {@link IAxis#transform(double)} and
   * {@link IAxis#transformInv(double)} whether they are invertible properly.
   * Just specify any axis and the test is being automatically done for that
   * {@link IAxis}.
   * <p>
   * <b>Note:</b> you have to initialize your axis accordingly which means you
   * have to set minimum and maximum values
   * 
   * @param axis
   *          the axis to test
   */
  public static final void transformInvTest(IAxis axis) {
    for (int i = 0; i < 1000; i++) {
      // forward
      double value =
          Math.random() * (axis.getMaximum() - axis.getMinimum())
              + axis.getMinimum();
      double tValue = axis.transform(value);
      assertEquals(value, axis.transformInv(tValue), 0.00001);

      // backward
      tValue = Math.random();
      value = axis.transformInv(tValue);
      assertEquals(tValue, axis.transform(value), 0.00001);
    }

  }

}
