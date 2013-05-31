/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.animator;

import org.jamesii.gui.utils.animator.IInterpolator;
import org.jamesii.gui.utils.animator.LinearInterpolator;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 */
public class LinearInterpolatorTest extends TestCase {
  /**
   * The interpolator to use.
   */
  private IInterpolator i;

  @Override
  protected void setUp() throws Exception {
    i = new LinearInterpolator();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.animator.LinearInterpolator#interpolate(double)}
   * .
   */
  public void testInterpolate() {
    for (int j = 0; j < 100; j++) {
      assertEquals((double) j / 100, i.interpolate((double) j / 100));
    }
  }

}
