/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.taskrunner.plugintype.AbstractTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

/**
 * @author Stefan Rybacki
 * 
 */
public class SimulationRunnerSetup extends
    ConfigureFactoryPanel<TaskRunnerFactory> {

  private static final long serialVersionUID = -3112459161360063847L;

  private ConfigureFactoryPanel<TaskRunnerFactory> confSimRunnerPanel;

  public SimulationRunnerSetup() {
    super(SimSystem.getRegistry().getFactoryOrEmptyList(
        AbstractTaskRunnerFactory.class, null),
        "Select simulation runner to be used:", null, false);
  }

  public ParameterizedFactory<TaskRunnerFactory> getFactory() {
    return getSelectedFactoryAndParameter();
  }

  /**
   * @param taskRunnerFactory
   */
  public void setTaskRunner(
      ParameterizedFactory<TaskRunnerFactory> taskRunnerFactory) {
    setSelectedFactory(taskRunnerFactory.getFactory().getClass().getName(),
        taskRunnerFactory.getParameters());
  }
}
