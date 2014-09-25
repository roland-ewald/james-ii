/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

/**
 * Processor factory for cellular automata processors.
 * 
 * @author Jan Himmelspach
 */

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.Context;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;
import org.jamesii.model.carules.ICARulesModel;

/**
 * A factory for creating CAProcessor objects.
 */
@Plugin(name = "CARulesProcessor", version = "1.01")
public class CAProcessorFactory extends JamesProcessorFactory {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2331579421015480218L;

  /**
   * Constant for the "efficiency" of the computation algorithm. This is only a
   * rough estimate as the efficiency usually depends on the problem (model) to
   * be computed. However, we somehow need to decide which algorithm to use in
   * case that there is no pre-selection or no advances selection mechanism
   * (algorithm selection) being used.
   */
  private static final double EFFICIENCY = .4;

  @Override
  public IProcessor create(IModel model, IComputationTask simulation,
      Partition partition, ParameterBlock params, Context context) {
    CASimulator sim = new CASimulator(model);
    sim.setComputationTask(simulation);
    ProcessorInformation pi = new ProcessorInformation();
    pi.setLocal(sim);

    simulation.setProcessorInfo(pi);

    return sim;
  }

  @Override
  public double getEfficencyIndex() {
    return EFFICIENCY;
  }

  @Override
  public List<Class<?>> getSupportedInterfaces() {
    ArrayList<Class<?>> al = new ArrayList<>();
    al.add(ICARulesModel.class);
    return al;
  }

  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

}
