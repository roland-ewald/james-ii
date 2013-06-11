/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

/**
 * Tests multi-steering capabilities of experimentation layer.
 * 
 * @author Roland Ewald
 */
public class SimpleMultiSteererTestExperiment extends
    MultiSteererTestExperiment<SimpleExperimentSteerer> {

  @Override
  protected SimpleExperimentSteerer createExperimentSteerer() {
    return new SimpleExperimentSteerer(NUM_VAR_ASSIGNMENTS, NUM_REPLICATIONS);
  }

  @Override
  protected Class<SimpleExperimentSteerer> getSteererClass() {
    return SimpleExperimentSteerer.class;
  }

}
