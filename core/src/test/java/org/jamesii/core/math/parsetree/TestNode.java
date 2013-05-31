/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import junit.framework.TestCase;

/**
 * The Class TestNode.
 */
public abstract class TestNode extends TestCase {

  /**
   * Test calc.
   */
  public abstract void testCalc();

  /**
   * Test clone.
   */
  public abstract void testClone();

  /**
   * Test get children.
   */
  public abstract void testGetChildren();

  // public void testGetDependencies() {
  // fail("Not yet implemented");
  // }

}
