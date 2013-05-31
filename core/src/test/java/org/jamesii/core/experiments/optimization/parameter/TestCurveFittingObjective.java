/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter;

import org.jamesii.core.experiments.optimization.parameter.CurveFittingObjective;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * The Class TestCurveFittingObjective.
 * 
 * @author Roland Ewald
 */
public class TestCurveFittingObjective extends
    SimpleSerializationTest<CurveFittingObjective> {

  /** The object. */
  CurveFittingObjective obj;

  @Override
  public void setUp() {
    obj =
        new CurveFittingObjective("testName", new Long[] { 1L, 2L, 3L },
            new Double[] { 1., 2., 3. });
  }

  @Override
  public void assertEquality(CurveFittingObjective original,
      CurveFittingObjective deserialisedVersion) {
    assertEquals(original.getName(), deserialisedVersion.getName());
    for (int i = 0; i < original.getRealData().length; i++) {
      assertEquals(original.getRealData()[i],
          deserialisedVersion.getRealData()[i]);
    }
    for (int i = 0; i < original.getRealTimes().length; i++) {
      assertEquals(original.getRealTimes()[i],
          deserialisedVersion.getRealTimes()[i]);
    }
  }

  @Override
  public CurveFittingObjective getTestObject() throws Exception {
    return obj;
  }

}
