/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import javax.swing.JPanel;

import org.jamesii.core.experiments.BaseExperiment;

/**
 * Super class for all panels that provide different views on experiment
 * properties.
 * 
 * @author Roland Ewald
 */
public abstract class EditExperimentPanel extends JPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = -9044116693492440252L;

  /** Reference to experiment. */
  private BaseExperiment experiment = null;

  /**
   * Default constructor.
   * 
   * @param exp
   *          experiment to be edited
   */
  public EditExperimentPanel(BaseExperiment exp) {
    this.experiment = exp;
  }

  /**
   * Called when the dialog is closed and the user didn't press 'cancel', should
   * be used to store settings to the experiment.
   */
  public abstract void closeDialog();

  /**
   * Get name of the panel.
   * 
   * @return the name
   */
  @Override
  public abstract String getName();

  /**
   * @return the experiment
   */
  protected BaseExperiment getExperiment() {
    return experiment;
  }

  /**
   * @param experiment
   *          the experiment to set
   */
  protected void setExperiment(BaseExperiment experiment) {
    this.experiment = experiment;
  }

}
