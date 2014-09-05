/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.simulator;


import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.algoselect.UnderDevelopment;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;

/**
 * The factory of the best simulator in the world.
 * 
 * This dubious piece of code is used to test the algorithm selection registry
 * :-)
 * 
 * @author Roland Ewald
 */
@UnderDevelopment
public class BestSimulatorInTheWorldFactory extends JamesProcessorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 161644189636036801L;

  @Override
  public IProcessor<Double> create(IModel model, IComputationTask simulation,
      Partition partition, ParameterBlock params, Context context) {
    throw new UnsupportedOperationException(
        "Re-calibration of flux capacitor failed.");
  }

  @Override
  public double getEfficencyIndex() {
    return Double.MAX_VALUE;
  }

  @Override
  public List<Class<?>> getSupportedInterfaces() {
    List<Class<?>> result = new ArrayList<>();
    result.add(IModel.class);
    return result;
  }

  @Override
  public boolean supportsSubPartitions() {
    return true;
  }

}
