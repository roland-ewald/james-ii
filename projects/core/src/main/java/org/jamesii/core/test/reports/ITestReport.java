/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.test.reports;

import java.io.Serializable;
import java.util.List;

/**
 * Standard interface for a data structure containing information which have
 * been produced by a test.
 * 
 * @author Stefan Leye
 */
public interface ITestReport extends Serializable {

  /**
   * Returns a representation of the results as string.
   * 
   * @return information as String
   */
  String resultToString();

  /**
   * Get the variables the test was about.
   * 
   * @return list of variable names
   */
  List<String> getInvolvedVariables();

  /**
   * The type of the test, which produced this result. false
   * 
   * @return the type of the test
   */
  Class<?> getTestType();

  /**
   * Flag determines, whether the test has been finished or additional
   * information are required. IF THIS IS NOT SET TO TRUE AFTER THE TEST IS
   * FINISHED THE EXPERIMENT MIGHT NOT STOP!
   * 
   * @return true, if test is finished
   */
  boolean finished();
}
