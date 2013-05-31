/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experimentsuite.read.plugintype;

import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for experiment readers.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractExperimentSuiteReaderFactory extends
    AbstractFilteringFactory<ExperimentSuiteReaderFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5123981347560133829L;

  /**
   * Parameter identifier for storing the experiment information of the
   * experiment to be read or written . Type is {@link ExperimentInfo}.
   */
  public static final String EXPERIMENT_INFO = "experimentInfo";

}
