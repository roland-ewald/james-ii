/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.workflow.experiment.plugintype.ExperimentSetupEditorFactory;
import org.jamesii.gui.workflow.experiment.plugintype.IExperimentSetup;

/**
 * A factory for creating ParameterExplorationSetup objects.
 * 
 * @author Jan Himmelspach
 */
public class ParameterExplorationSetupFactory extends
    ExperimentSetupEditorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 6453027339484955830L;

  @Override
  public IExperimentSetup create(ParameterBlock params) {
    return new ParameterExplorationSetup(params);
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    return 1;
  }

}
