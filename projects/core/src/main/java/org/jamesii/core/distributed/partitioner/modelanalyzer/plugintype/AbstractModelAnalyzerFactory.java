/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * 
 * Abstract factory for all model analyzer factories.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractModelAnalyzerFactory extends
    AbstractFilteringFactory<ModelAnalyzerFactory> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 4547784292294024083L;

  /**
   * Identifier for the model to be analyzed, type is
   * {@link org.jamesii.core.model.IModel}.
   */
  public static final String MODEL = "model";

}
