/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import javax.swing.JComponent;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.gui.experiment.actions.SliderSimAction;
import org.jamesii.gui.workflow.experiment.plugintype.AbstractExperimentSetup;
import org.jamesii.gui.workflow.experiment.plugintype.ExperimentSetupEditorFactory;

/**
 * The Class ParameterExplorationSetup. Parameter exploration / parameter scan /
 * "classical / normal" simulation study means an experiment in which a model is
 * simulated using a set of pre-defined parameter combinations. I.e., the
 * parameter combinations to be used do not depend on results of preceding runs.
 * An example for such an experiment is to increase the number of entities in a
 * system, e.g., to observe whether some behavioral patterns only appear if
 * thresholds are exceeded or the proportion of different types of entities has
 * a certain value.
 * 
 * @author Jan Himmelspach
 * @author Stefan Rybacki
 */
public class ParameterExplorationSetup extends AbstractExperimentSetup {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3611419322690735157L;

  private final ExperimentVariablesSetup expVarSetup =
      new ExperimentVariablesSetup();

  private final ReplicationCriterionSetup repCritSetup =
      new ReplicationCriterionSetup();

  private final SimRunStopPolicySetup simRunStopSetup =
      new SimRunStopPolicySetup();

  private final SimpleSimulationAlgorithmSetup simAlgoSetup;

  private final SimulationRunnerSetup simRunnerSetup =
      new SimulationRunnerSetup();

  /**
   * Instantiates a new model loader editor.
   * 
   * @param params
   */
  public ParameterExplorationSetup(ParameterBlock params) {
    IModel model = params.getSubBlockValue(ExperimentSetupEditorFactory.MODEL);

    simAlgoSetup = new SimpleSimulationAlgorithmSetup(model);
  }

  @Override
  public JComponent getPage(int index) {
    switch (index) {
    case 0:
      return expVarSetup;
    case 1:
      return simRunStopSetup;
    case 2:
      return repCritSetup;
    case 3:
      return simAlgoSetup;
    case 4:
      return simRunnerSetup;
    }
    return null;
  }

  @Override
  public int getPageCount() {
    return 5;
  }

  @Override
  public String getPageTitle(int index) {
    switch (index) {
    case 0:
      return "Experiment Variables";
    case 1:
      return "Simulation Stop Policy";
    case 2:
      return "Simulation Replication Criterion";
    case 3:
      return "Simulation Algorithm";
    case 4:
      return "Simulation Runner";
    }
    return null;
  }

  @Override
  public void setupExperiment(BaseExperiment experiment) {
    experiment.setExperimentVariables(expVarSetup.getExperimentVariables());

    experiment.setReplicationCriterionFactory(repCritSetup
        .getReplicationCriterionFactory());
    experiment.setComputationTaskStopPolicyFactory(simRunStopSetup
        .getSimRunStopPolicy());
    experiment.setComputationTaskStopPolicyParameters(simRunStopSetup
        .getSimRunStopPolicyParameters());

    ParameterBlock block =
        new ParameterBlock(simAlgoSetup.getSelectedFactory().getClass()
            .getName(), simAlgoSetup.getSelectedParameters().getSubBlocks());
    experiment.setProcessorFactoryParameters(block);

    ParameterizedFactory<TaskRunnerFactory> factory =
        simRunnerSetup.getFactory();
    if (factory != null) {
      experiment.setTaskRunnerFactory(factory);
    }

    experiment
        .setComputationTaskInterStepDelay(SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS);
  }

  @Override
  public void setupFromExperiment(BaseExperiment experiment) {
    simRunStopSetup.setSimRunStopPolicy(experiment
        .getComputationTaskStopFactory());

    repCritSetup.setReplicationCriteria(experiment
        .getReplicationCriterionFactory());

    ParameterBlock b =
        experiment.getParameters().getParameterBlock()
            .getSubBlock(ProcessorFactory.class.getName());

    if (b != null) {
      simAlgoSetup.setAlgoFactory((ProcessorFactory) SimSystem.getRegistry()
          .getFactory((String) b.getValue()), b);
    }

    simRunnerSetup.setTaskRunner(experiment.getTaskRunnerFactory());
  }
}
