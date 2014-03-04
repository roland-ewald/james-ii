/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;

import org.jamesii.asf.integrationtest.bogus.application.model.IBogusModel;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.core.util.misc.LoadGenerator;


/**
 * The Class FlexibleBogusSimulator.
 * 
 * @author Roland Ewald
 */
public class FlexibleBogusSimulator extends RunnableProcessor<Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1522528567112296665L;

  /** The current step count. */
  private double count = 0.0;

  /** The number of steps until simulation. */
  private final int loadPerSteps;

  /**
   * Instantiates a new flexible bogus simulator.
   * 
   * @param model
   *          the model
   */
  public FlexibleBogusSimulator(IModel model,
      IBogusSimulatorProperties properties) {
    super(model);
    loadPerSteps =
        properties.getLoadPerSteps(((IBogusModel) model).getContent());
  }

  @Override
  public Double getTime() {
    return count;
  }

  @Override
  protected void nextStep() {
    count++;
    (new LoadGenerator()).generateLoad(loadPerSteps);
  }

}
