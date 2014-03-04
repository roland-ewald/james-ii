/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integrationtest.bogus.application.simulator;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.integrationtest.bogus.application.model.IBogusModel;
import org.jamesii.core.algoselect.UnderDevelopment;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;

/**
 * The factory of the best simulator in the world. Seriously.
 * 
 * @author Roland Ewald
 */
@UnderDevelopment
public class FlexibleBogusSimulatorFactory extends JamesProcessorFactory {

  /**
   * The default bogus model properties.
   */
  private static final class IBogusSimulatorPropertiesImpl implements
      IBogusSimulatorProperties {
    private static final long serialVersionUID = -2576770996260933903L;

    @Override
    public int getLoadPerSteps(Map<String, Serializable> modelContent) {
      return 1;
    }
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 161644189636036801L;

  /** The properties of the simulator. Type: {@link IBogusSimulatorProperties}. */
  public static final String SIM_PROPERTIES = "SimulatorProperties";

  @Override
  public final IProcessor<Double> create(IModel model,
      IComputationTask simulation,
      Partition partition, ParameterBlock params) {
    IBogusSimulatorProperties properties = determineProperties(params);
    FlexibleBogusSimulator sim = new FlexibleBogusSimulator(model, properties);
    sim.setComputationTask(simulation);
    ProcessorInformation pi = new ProcessorInformation();
    pi.setLocal(sim);
    simulation.setProcessorInfo(pi);
    return sim;
  }

  /**
   * Determine properties.
   * 
   * @param params
   *          the params
   * 
   * @return the i bogus simulator properties
   */
  protected IBogusSimulatorProperties determineProperties(ParameterBlock params) {
    IBogusSimulatorProperties properties =
        params.getSubBlockValue(SIM_PROPERTIES,
            new IBogusSimulatorPropertiesImpl());
    return properties;
  }

  @Override
  public double getEfficencyIndex() {
    return Double.MAX_VALUE;
  }

  @Override
  public List<Class<?>> getSupportedInterfaces() {
    List<Class<?>> result = new ArrayList<>();
    result.add(IBogusModel.class);
    return result;
  }

  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

}
