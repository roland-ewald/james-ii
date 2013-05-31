/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.cmdparameters;

import org.jamesii.core.parameters.ParameterBlock;

import junit.framework.TestCase;

/**
 * Testing parameter block functionality.
 * 
 * @author Roland Ewald
 */
public class TestParameterBlock extends TestCase {

  /** The p1. */
  ParameterBlock p1 = null;

  /** The p2. */
  ParameterBlock p2 = null;

  /** The Constant TESTVAR1. */
  static final String TESTVAR1 = "Test1";

  /** The Constant TESTVAR2. */
  static final String TESTVAR2 = "Test2";

  /** The Constant TESTVAR3. */
  static final String TESTVAR3 = "Test3";

  @Override
  public void setUp() {
    p1 = new ParameterBlock(TESTVAR1);
    p2 = new ParameterBlock(TESTVAR2);
    p1.addSubBlock(TESTVAR3, p2);
  }

  /**
   * Tests if parameter blocks can be copied.
   */
  public void testCopy() {
    ParameterBlock pCopy = p1.getCopy();
    assertTrue(p1.compareTo(pCopy) == 0);
    assertTrue(new ParameterBlock().compareTo((new ParameterBlock()).getCopy()) == 0);
    assertEquals(TESTVAR2, pCopy.getSubBlockValue(TESTVAR3));
    assertEquals(TESTVAR1, pCopy.getValue());
  }

}
