/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.awt.event.ActionEvent;

import org.jamesii.core.data.experiment.read.plugintype.ExperimentReaderFactory;
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
public class ExperimentReadAction extends DefaultSwingAction {

  /** Serialisation ID. */
  private static final long serialVersionUID = -7210777700860286173L;

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
  public ExperimentReadAction(ExperimentPerspective ePersp,
      IFactoryParameterDialog<?> pDialog, String name) {
    super(name);
    expPerspective = ePersp;
    paramDialog = pDialog;

  }

  @Override
  public void actionPerformed(ActionEvent e) {
    Pair<ParameterBlock, ? extends Factory> param =
        paramDialog.getFactoryParameter(null);
    if (param == null || param.getFirstValue() == null
        || param.getSecondValue() == null) {
      return;
    }

    handleSingleExperiment(param);
  }

  /**
   * Handles read/write of an experiment.
   * 
   * @param param
   *          factory and its parameters
   */
  protected void handleSingleExperiment(
      Pair<ParameterBlock, ? extends Factory> param) {
    expPerspective
        .openExperiment((ExperimentReaderFactory) param.getSecondValue(),
            param.getFirstValue());
  }

}
