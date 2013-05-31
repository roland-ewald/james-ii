/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests;

import java.util.List;

/**
 * Standard interface for paired tests. These are tests that check whether two
 * samples belong to the same distribution (this is the null hypothesis).
 * 
 * @author Stefan Leye
 */
public interface IPairedTest {

  /**
   * Execute the test.
   * 
   * @param sample1
   *          the first sample.
   * @param sample2
   *          the second sample.
   * @return the confidence that both samples are from the same distribution
   *         (i.e., p-value)
   */
  double executeTest(List<? extends Number> sample1,
      List<? extends Number> sample2);

}
