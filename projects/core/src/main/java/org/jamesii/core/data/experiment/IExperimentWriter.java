/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment;

import java.io.IOException;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Write an experiment given in a BaseExperiment class.
 * 
 * Experiments should be stored in a way that they can be redone later on.
 * Therefore the system provides experiment readers and writers.
 * 
 * An experiment writer has to make sure that all properties of interest, and at
 * least the properties essentially required to repeat the experiment are
 * stored.
 * 
 * @author Mathias Röhl
 */
public interface IExperimentWriter {

  /**
   * Store the given experiment.
   * 
   * The experiment might be written to any data sink, i.e., to files,
   * databases, web based repositories, or whereever else.
   * 
   * @param param
   *          information on the location of the experiment
   * 
   * @param experiment
   *          the experiment to be saved
   * @throws IOException
   *           the exception thrown, when the writing process fails
   */
  void writeExperiment(ParameterBlock param, BaseExperiment experiment)
      throws IOException;

}
