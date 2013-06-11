/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite;

import java.io.IOException;

import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ExperimentSuite;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for writers of experiment suites. Similar to
 * {@link IExperimentWriter}.
 * 
 * @author Roland Ewald
 * 
 */
public interface IExperimentSuiteWriter {

  /**
   * Store the given experiment suite.
   * 
   * @param <E>
   *          the type of the experiment
   * 
   * @param param
   *          information on the location of the experiment suite
   * 
   * @param experimentSuite
   *          the experiment suite to be saved
   * @throws IOException
   */
  <E extends BaseExperiment> void writeExperimentSuite(ParameterBlock param,
      ExperimentSuite<E> experimentSuite) throws IOException;

}
