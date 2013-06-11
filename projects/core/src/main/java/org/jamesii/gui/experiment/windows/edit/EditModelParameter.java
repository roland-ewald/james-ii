/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.gui.application.Contribution;

/**
 * Stub panel to allow editing model parameters.
 * 
 * @author Roland Ewald
 */
public class EditModelParameter extends EditExperimentPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5106625695766701356L;

  /**
   * Default constructor.
   * 
   * @param exp
   *          reference to experiment
   */
  public EditModelParameter(BaseExperiment exp) {
    super(exp);

    setExperiment(exp);

    init();
  }

  /**
   * Inits the.
   */
  public void init() {
    this.add(new ExperimentVariablesView(getExperiment()
        .getExperimentVariables(), Contribution.EDITOR).createContent());
  }

  @Override
  public void closeDialog() {
    // TODO Auto-generated method stub
  }

  @Override
  public String getName() {
    return "Model parameters";
  }

}
