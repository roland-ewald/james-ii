/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.execution;

import java.util.List;

import javax.swing.SwingWorker;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.RunInformation;

/**
 * 
 * This thread runs the experiment and propagates all necessary information to
 * the GUI.
 * 
 * @author Roland Ewald
 * 
 */
public class ExperimentThread extends
    SwingWorker<List<List<RunInformation>>, ComputationTaskRuntimeInformation> {

  /**
   * Reference to the experiment to be executed.
   */
  private BaseExperiment experiment;

  /**
   * Default constructor.
   * 
   * @param exp
   *          experiment to be executed
   */
  public ExperimentThread(BaseExperiment exp) {
    experiment = exp;
  }

  /**
   * Executes experiment.
   * 
   * @return run information
   * @throws Exception
   *           if something goes wrong
   */
  @Override
  protected List<List<RunInformation>> doInBackground() {
    experiment.execute();
    BaseExperiment exp = experiment;
    experiment = null;
    return exp.getResults();
  }

}