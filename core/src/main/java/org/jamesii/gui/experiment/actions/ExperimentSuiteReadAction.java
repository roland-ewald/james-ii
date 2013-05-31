/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;

import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteReaderFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.action.DefaultSwingAction;
import org.jamesii.gui.experiment.ExperimentPerspective;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;

/**
 * Action listener for the Open menu of the experiment GUI.
 * 
 * @author Roland Ewald
 */
public class ExperimentSuiteReadAction extends DefaultSwingAction {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2504698029936830276L;

  /** Reference to experiment perspective. */
  private final ExperimentPerspective expPerspective;

  /** Dialog to react to the action. */
  private final IFactoryParameterDialog<?> paramDialog;

  /**
   * Default constructor.
   * 
   * @param ePersp
   *          reference to experiment perspective
   * @param pDialog
   *          pDialog the parameter dialog
   * @param name
   *          the name
   */
  public ExperimentSuiteReadAction(ExperimentPerspective ePersp,
      IFactoryParameterDialog<?> pDialog, String name) {
    super(name);
    expPerspective = ePersp;
    paramDialog = pDialog;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Pair<ParameterBlock, ? extends Factory> param =
        paramDialog.getFactoryParameter(null);
    if (param == null || param.getFirstValue() == null) {
      return;
    }

    handleExperimentSuite(param);
  }

  /**
   * Handles read/write of an experiment suite.
   * 
   * @param param
   *          factory and its parameters
   */
  protected void handleExperimentSuite(
      Pair<ParameterBlock, ? extends Factory> param) {
    expPerspective.openExperimentSuite(
        (ExperimentSuiteReaderFactory) param.getSecondValue(),
        param.getFirstValue());
  }

}
