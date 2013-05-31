/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.statistics.univariate.StandardDeviation;

import junit.framework.TestCase;

/**
 * @author Jan Himmelspach
 */
public class StandardDeviationTest extends TestCase {

  /**
   * @param name
   */
  public StandardDeviationTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  public void testStdDev() {

    double dt1[] = new double[] { 2., 2., 2., 2., 2. };
    assertTrue(Double.compare(StandardDeviation.standardDeviation(dt1), 0.) == 0);

    double dt2[] = new double[] { 2., 1. };
    assertTrue("" + StandardDeviation.standardDeviation(dt2), Double.compare(
        StandardDeviation.standardDeviation(dt2), 0.7071067811865476) == 0);

    int it1[] = new int[] { 2, 2, 2, 2, 2 };
    assertTrue(Double.compare(StandardDeviation.standardDeviation(it1), 0.) == 0);

    int it2[] = new int[] { 2, 1 };
    assertTrue(Double.compare(StandardDeviation.standardDeviation(it2),
        0.7071067811865476) == 0);

    List<Double> d = new ArrayList<>();
    for (int i = 0; i < dt1.length; i++) {
      d.add(dt1[i]);
    }
    assertTrue(Double.compare(StandardDeviation.standardDeviation(d), 0.) == 0);

    d = new ArrayList<>();
    for (int i = 0; i < dt2.length; i++) {
      d.add(dt2[i]);
    }
    assertTrue(Double.compare(StandardDeviation.standardDeviation(d),
        0.7071067811865476) == 0);

  }

}
