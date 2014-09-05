/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.parallel;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;

/**
 * A factory for creating ParallelComputationTaskRunner objects.
 * 
 * @author Stefan Leye
 */
public class ParallelComputationTaskRunnerFactory extends TaskRunnerFactory {

  /**
   * Maximal number of threads that can be spawned. Type: {@link Integer}.
   * Default is -1 (= number of cores).
   */
  public static final String NUM_CORES = "Number of used cores";

  /** Serialisation ID. */
  private static final long serialVersionUID = -6979525624297810746L;

  /**
   * Instantiates a new parallel simulation runner factory.
   */
  public ParallelComputationTaskRunnerFactory() {
    super();
  }

  @Override
  public ITaskRunner create(ParameterBlock parameter, Context context) {
    ParallelComputationTaskRunner psr = null;
    try {
      psr =
          new ParallelComputationTaskRunner(
              ParameterBlocks.getSubBlockValueOrDefault(parameter, NUM_CORES,
                  -1));
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
    return psr;
  }
}
