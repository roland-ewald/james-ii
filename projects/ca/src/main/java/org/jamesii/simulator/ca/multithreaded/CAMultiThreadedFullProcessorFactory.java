/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.multithreaded;

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
import org.jamesii.model.ca.grid.ICAGrid;

/**
 * @author st019
 * 
 */
@Plugin(version = "1.0",
  parameters={
    @Parameter(name = CAMultiThreadedFullProcessorFactory.WRITE_BACK_INTERVAL, type = Integer.class, defaultValue = "1"),
    @Parameter(name = CAMultiThreadedFullProcessorFactory.MAX_THREADS, type = Integer.class, defaultValue = "3"),
    @Parameter(name = CAMultiThreadedFullProcessorFactory.INDEX_CACHE, type = Boolean.class, defaultValue = "true"), }
)
public class CAMultiThreadedFullProcessorFactory extends JamesProcessorFactory {

  private static final long serialVersionUID = 6290386823840531662L;

  public static final String WRITE_BACK_INTERVAL = "writeBackInterval";

  public static final String MAX_THREADS = "maxThreads";

  public static final String INDEX_CACHE = "indexCache";

  public CAMultiThreadedFullProcessorFactory() {
    super();
  }

  @Override
  public IProcessor create(IModel model, IComputationTask simulation,
      Partition partition, ParameterBlock parameters, Context context) {
    CAMultiThreadedFullProcessor sim = null;
    Integer writeBack =
        ParameterBlocks.getSubBlockValueOrDefault(parameters,
            WRITE_BACK_INTERVAL, 1);
    Integer maxThreads =
        ParameterBlocks.getSubBlockValueOrDefault(parameters, MAX_THREADS,
            Runtime.getRuntime().availableProcessors() - 1);
    Boolean indexCache =
        ParameterBlocks
            .getSubBlockValueOrDefault(parameters, INDEX_CACHE, true);

    sim =
        new CAMultiThreadedFullProcessor(model, writeBack, maxThreads,
            indexCache);

    sim.setComputationTask(simulation);
    ProcessorInformation pi = new ProcessorInformation();
    pi.setLocal(sim);

    simulation.setProcessorInfo(pi);

    return sim;
  }

  @Override
  public double getEfficencyIndex() {
    return 2;
  }

  @Override
  public List<Class<?>> getSupportedInterfaces() {
    List<Class<?>> al = new ArrayList<>();
    al.add(ICAGrid.class);
    return al;
  }

  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

}
