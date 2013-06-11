/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.setup.internalsimrun;

import org.jamesii.core.experiments.tasks.setup.IComputationTaskSetup;
import org.jamesii.core.experiments.tasks.setup.plugintype.TaskSetupFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.IJamesProcessorFactory;

/**
 * This factory returns an instance of
 * {@link org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunSetup}
 * . This instance can be used to create internal
 * {@link org.jamesii.core.simulationrun.SimulationRun} objects.
 * 
 * @author Jan Himmelspach
 */
public class SimulationRunFactory extends TaskSetupFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8017556363756259948L;

  @Override
  public IComputationTaskSetup create(ParameterBlock parameter) {
    return new SimulationRunSetup();
  }

  @Override
  public boolean supportsProcessor(Class<? extends Factory> factory) {
    return IJamesProcessorFactory.class.isAssignableFrom(factory);
  }

}
