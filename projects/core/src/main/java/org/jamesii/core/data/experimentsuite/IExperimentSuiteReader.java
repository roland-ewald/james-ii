/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ExperimentSuite;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Interface for readers of experiment suites. Similar to
 * {@link org.jamesii.core.data.experiment.IExperimentReader}.
 * 
 * @author Roland Ewald
 * 
 */
public interface IExperimentSuiteReader {

  /**
   * Get info on experiment suites stored at certain URIs.
   * 
   * @param locationList
   *          list of experiment storage locations
   * @return list of experiment info
   */
  List<ExperimentSuiteInfo> getAvailableExperimentSuites(List<URI> locationList);

  /**
   * Read experiment suite.
   * 
   * @param <E>
   *          the type of the experiment
   * 
   * @param expType
   *          the exp type
   * @param param
   *          the param
   * 
   * @return the experiment suite< e>
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  <E extends BaseExperiment> ExperimentSuite<E> readExperimentSuite(
      Class<E> expType, ParameterBlock param) throws IOException;

}
