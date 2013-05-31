package org.jamesii.core.experiments.taskrunner;

import org.jamesii.core.experiments.BaseExperiment;

/**
 * Simple test to check whether an (ill-defined, becaue empty) experiment fails
 * gracefully.
 * 
 * @author Roland Ewald
 * 
 */
public class TestEmptyExperimentErrorHandling {

  public static void main(String[] args) {
    BaseExperiment be = new BaseExperiment();
    be.execute();
  }

}
