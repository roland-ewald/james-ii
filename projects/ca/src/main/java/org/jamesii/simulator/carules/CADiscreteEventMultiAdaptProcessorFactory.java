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
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.plugins.annotations.Parameter;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;
import org.jamesii.model.carules.ICARulesModel;

/**
 * A factory for creating CAProcessor objects.
 */
@Plugin(parameters = {
    @Parameter(name = CADiscreteEventMultiAdaptProcessorFactory.MIN_CELLS_PER_THREAD, type = Integer.class, defaultValue = "20", description = "The minimal number of cells per thread. The number of threads used will be reduced if too few cells have to be computed."),
    @Parameter(name = CADiscreteEventMultiAdaptProcessorFactory.THREAD_COUNT_FACTOR, type = Double.class, defaultValue = "1.0", description = "Number of threads to be created per physical unit. Greater 1.0 means that we'll have more threads than physical units, lower 1.0 means less.")
 }, version = "1.01")
public class CADiscreteEventMultiAdaptProcessorFactory extends
    JamesProcessorFactory {

  public static final String THREAD_COUNT_FACTOR = "threadCountFactor";

  public static final String MIN_CELLS_PER_THREAD = "minCellsPerThread";

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2331579421015480218L;

  /**
   * Constant for the "efficiency" of the computation algorithm. This is only a
   * rough estimate as the efficiency usually depends on the problem (model) to
   * be computed. However, we somehow need to decide which algorithm to use in
   * case that there is no pre-selection or no advances selection mechanism
   * (algorithm selection) being used.
   */
  private static final double EFFICIENCY = .9;

  @Override
  public IProcessor create(IModel model, IComputationTask simulation,
      Partition partition, ParameterBlock params, Context context) {
    Integer minCellsPerThread =
        ParameterBlocks.getSubBlockValue(params, MIN_CELLS_PER_THREAD);
    Double threadCountFactor =
        ParameterBlocks.getSubBlockValue(params, THREAD_COUNT_FACTOR);
    CADiscreteEventMultiAdaptSimulator processor = null;
    if (minCellsPerThread == null) {
      processor = new CADiscreteEventMultiAdaptSimulator(model);
    } else {
      processor =
          new CADiscreteEventMultiAdaptSimulator(model, minCellsPerThread,
              threadCountFactor);
    }

    processor.setComputationTask(simulation);
    ProcessorInformation pi = new ProcessorInformation();
    pi.setLocal(processor);

    simulation.setProcessorInfo(pi);

    return processor;
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

  @Override
  public boolean supportsModel(IModel model) {
    if (model instanceof ICARulesModel) {
      // only support reactive rule CAs
      return ((ICARulesModel) model).getBaseRules().isReactiveRules();
    }
    return false;
  }

}
