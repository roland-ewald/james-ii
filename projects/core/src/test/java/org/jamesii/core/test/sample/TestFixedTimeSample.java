/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.sample;

import java.util.ArrayList;

import org.jamesii.core.test.samples.FixedTimeSample;

import junit.framework.TestCase;

public class TestFixedTimeSample extends TestCase {

  public static void testFixedTimeSample() {
    FixedTimeSample<Double> sample =
        new FixedTimeSample<>(Double.class, "test", 0.0);
    ArrayList<Double> list = new ArrayList<>();

    assertEquals(sample.getData(), list);
    assertEquals(sample.getSampleSimTime(), 0.0);
    assertEquals(sample.getVariableName(), "test");
    assertEquals(sample.getVariableValueClass(), Double.class);
  }
}
