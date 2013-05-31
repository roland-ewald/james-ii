/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.math.statistics.univariate.Frequencies;

import junit.framework.TestCase;

/**
 * @author Jan Himmelspach *
 */
public class FrequenciesTest extends TestCase {

  /**
   * @param name
   */
  public FrequenciesTest(String name) {
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

  public void testFrequencies() {
    double[] dt = new double[] { 2, 3, 2, 2, 4, 4, 5 };
    double[] vals = Frequencies.getValues(dt);
    assertTrue(vals.length == 4);
    Arrays.sort(vals);
    assertTrue(Double.compare(vals[0], 2.) == 0);
    assertTrue(Double.compare(vals[1], 3.) == 0);
    assertTrue(Double.compare(vals[2], 4.) == 0);
    assertTrue(Double.compare(vals[3], 5.) == 0);

    Map<Double, Integer> valFreqD = new HashMap<>();
    Frequencies.getValuesAndFrequencies(dt, valFreqD);

    assertTrue(valFreqD.get(vals[0]) == 3);
    assertTrue(valFreqD.get(vals[1]) == 1);
    assertTrue(valFreqD.get(vals[2]) == 2);
    assertTrue(valFreqD.get(vals[3]) == 1);

    int[] it = new int[] { 2, 3, 2, 2, 4, 4, 5 };
    int[] vals2 = Frequencies.getValues(it);
    assertTrue(vals.length == 4);
    Arrays.sort(vals);
    assertTrue(vals2[0] == 2);
    assertTrue(vals2[1] == 3);
    assertTrue(vals2[2] == 4);
    assertTrue(vals2[3] == 5);

    Map<Integer, Integer> valFreqI = new HashMap<>();
    Frequencies.getValuesAndFrequencies(it, valFreqI);

    assertTrue(valFreqI.get(vals2[0]) == 3);
    assertTrue(valFreqI.get(vals2[1]) == 1);
    assertTrue(valFreqI.get(vals2[2]) == 2);
    assertTrue(valFreqI.get(vals2[3]) == 1);

  }

}
