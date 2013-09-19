/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.plugintype.AbstractTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

/**
 * Panel to configure the
 * {@link org.jamesii.core.experiments.taskrunner.ITaskRunner} to be used for
 * the current {@link BaseExperiment}.
 * 
 * @author Roland Ewald
 */
public class EditSimulationRunner extends EditExperimentPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4474445126955441078L;

  /** Panel to configure data storage factories. */
  private ConfigureFactoryPanel<TaskRunnerFactory> confSimRunnerPanel;

  /**
   * Default constructor.
   * 
   * @param exp
   *          reference to experiment
   */
  public EditSimulationRunner(BaseExperiment exp) {
    super(exp);
    setLayout(new BorderLayout());
    confSimRunnerPanel =
        new ConfigureFactoryPanel<>(SimSystem.getRegistry()
            .getFactoryOrEmptyList(AbstractTaskRunnerFactory.class, null),
            "Select simulation runner to be used:", getExperiment()
                .getTaskRunnerFactory(), false);
    add(confSimRunnerPanel, BorderLayout.CENTER);
  }

  @Override
  public void closeDialog() {
    getExperiment().setTaskRunnerFactory(
        confSimRunnerPanel.getSelectedFactoryAndParameter());
  }

  @Override
  public String getName() {
    return "Simulation Runner";
  }
}
