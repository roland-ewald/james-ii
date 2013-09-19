/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.experiment.write.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for experiment writers.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractExperimentWriterFactory extends
    AbstractFilteringFactory<ExperimentWriterFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -1418701813047603286L;

  /**
   * Parameter identifier for storing the experiment information of the
   * experiment to be read or written . Type is
   * {@link org.jamesii.core.data.experiment.ExperimentInfo}.
   */
  public static final String EXPERIMENT_INFO = "experimentInfo";

}
