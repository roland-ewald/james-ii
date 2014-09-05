/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.taskrunner.sequential;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating SequentialSimulationRunner objects.
 * 
 * @author Stefan Leye
 */
public class SequentialSimulationRunnerFactory extends TaskRunnerFactory {

  /** Serialization ID. */
  private static final long serialVersionUID = 6741871731258677477L;

  /**
   * Instantiates a new sequential simulation runner factory.
   */
  public SequentialSimulationRunnerFactory() {
    super();
  }

  @Override
  public ITaskRunner create(ParameterBlock parameter, Context context) {
    SequentialComputationTaskRunner ssr = null;
    try {
      ssr = new SequentialComputationTaskRunner();
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
    return ssr;
  }
}
