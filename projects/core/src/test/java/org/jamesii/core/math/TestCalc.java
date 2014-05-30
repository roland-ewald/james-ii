/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math;

import java.util.List;

import org.jamesii.core.math.Calc;

import junit.framework.TestCase;

/**
 * Tests for {@link Calc}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestCalc extends TestCase {

  /**
   * Tests faculty method.
   */
  public void testFaculty() {
    assertEquals(1, Calc.faculty(0));
    assertEquals(1, Calc.faculty(1));
    assertEquals(2, Calc.faculty(2));
    assertEquals(24, Calc.faculty(4));
    assertEquals(39916800, Calc.faculty(11));
  }

  /**
   * Tests sequence functions.
   */
  public void testSequence() {
    List<Integer> result = Calc.sequence(0, 5);

    assertEquals(5, result.size());
    assertTrue(0 == result.get(0));
    assertTrue(4 == result.get(4));

    result = Calc.sequence(5, 2);
    assertEquals(0, result.size());

    result = Calc.sequence(5, 2, -1);
    assertTrue(3 == result.size());
    assertTrue(5 == result.get(0));
    assertTrue(3 == result.get(2));
  }

}
