/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.parametersetup.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for all model parameter setup window factories.
 * 
 * @author Jan Himmelspach
 * 
 */
public class AbstractModelParameterSetupWindowFactory extends
    AbstractFilteringFactory<ModelParameterSetupWindowFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7969967336663680262L;

  /**
   * The model to be displayed, type: {@link org.jamesii.core.model.IModel}.
   */
  public static final String MODEL = "model";

}
