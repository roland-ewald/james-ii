/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.editexpsuite;

import javax.swing.DefaultListModel;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ExperimentSuite;

/**
 * List model for a list of {@link BaseExperiment} instances, organised as an
 * {@link ExperimentSuite}.
 * 
 * @author Roland Ewald
 */
public class ExperimentSuiteListModel extends DefaultListModel {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8650293044353703117L;

  /** Reference to current suite. */
  private final ExperimentSuite<BaseExperiment> suite;

  /**
   * Instantiates a new experiment suite list model.
   * 
   * @param expSuite
   *          the exp suite
   */
  public ExperimentSuiteListModel(ExperimentSuite<BaseExperiment> expSuite) {
    suite = expSuite;
    for (BaseExperiment e : expSuite.getExperiments()) {
      addElement(e);
    }
  }

  /**
   * Adds the experiment.
   * 
   * @param exp
   *          the exp
   */
  public void addExperiment(BaseExperiment exp) {
    suite.addExperiment(exp);
    addElement(exp);
  }

  /**
   * Removes the experiment.
   * 
   * @param index
   *          the index
   */
  public void removeExperiment(int index) {
    suite.getExperiments().remove(index);
    remove(index);
  }

}
