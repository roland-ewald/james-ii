/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.write.plugintype;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for experiment suite writers.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractExperimentSuiteWriterFactory extends
    AbstractFilteringFactory<ExperimentSuiteWriterFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2251332938992913257L;

  /**
   * Parameter identifier for storing the experiment information of the
   * experiment to be read or written . Type is {@link ExperimentInfo}.
   */
  public static final String EXPERIMENT_INFO = "experimentInfo";

}
