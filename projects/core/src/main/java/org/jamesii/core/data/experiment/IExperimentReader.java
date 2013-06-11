/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * An experiment reader retrieves an experiment description from an external
 * source. The experiment to be retrieved has to be specified by an URI.
 * 
 * There is no limitation from where an experiment can be retrieved - the
 * returned experiment must be a valid BaseExperiment.
 * 
 * @author Jan Himmelspach
 */
public interface IExperimentReader {

  /**
   * Fetch a list of defined experiments from the list of given databases. All
   * databases need to be of the same type.
   * 
   * @param datasources
   *          list of databases to retrieve the experiment definitions from
   * @return list of experiment definitions
   */
  List<ExperimentInfo> getAvailableExperiments(List<URI> datasources);

  /**
   * Return an instance of an experiment. An experiment is defined as multiple
   * simulation runs whereby the start parameters of a model and/or the
   * simulation run can be modified from simulation run to simulation by
   * modifiers (e.g. optimization algorithms)
   * 
   * @param param
   *          the information on the experiment to be read
   * @return the experiment instance to be executed
   * @throws IOException
   *           when some i/o error occurs
   */
  BaseExperiment readExperiment(ParameterBlock param) throws IOException;
}
