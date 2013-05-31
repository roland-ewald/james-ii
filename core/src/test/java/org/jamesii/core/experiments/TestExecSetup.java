/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * Represents a simple parameter setup to be executed.
 * 
 * @author Roland Ewald
 */
public class TestExecSetup extends Pair<Map<String, Object>, ParameterBlock> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5569948815271546776L;

  /**
   * Instantiates a new test exec setup.
   * 
   * @param e1
   *          the e1
   * @param e2
   *          the e2
   */
  public TestExecSetup(Map<String, Object> e1, ParameterBlock e2) {
    super(e1, e2);
  }

  /**
   * Gets the map list.
   * 
   * @param setups
   *          the setups
   * 
   * @return the map list
   */
  public static List<Map<String, Object>> getMapList(List<TestExecSetup> setups) {
    List<Map<String, Object>> result = new ArrayList<>();
    for (TestExecSetup setup : setups) {
      result.add(setup.getFirstValue());
    }
    return result;
  }

}
