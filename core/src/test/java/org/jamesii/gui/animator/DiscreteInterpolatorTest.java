/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.animator;

import org.jamesii.gui.utils.animator.DiscreteInterpolator;
import org.jamesii.gui.utils.animator.IInterpolator;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 */
public class DiscreteInterpolatorTest extends TestCase {

  /**
   * The interpolator to use for testing.
   */
  private IInterpolator i;

  @Override
  protected void setUp() throws Exception {
    i = new DiscreteInterpolator(5);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.utils.animator.DiscreteInterpolator#interpolate(double)}
   * .
   */
  public void testInterpolate() {
    assertEquals(0.0, i.interpolate(0.1));
    assertEquals(0.2, i.interpolate(0.3));
    assertEquals(0.4, i.interpolate(0.5));
    assertEquals(0.6, i.interpolate(0.7));
    assertEquals(0.8, i.interpolate(0.9));
    assertEquals(1.0, i.interpolate(1.0));
  }

}
