/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.windows.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;
import org.jamesii.core.model.IModel;

/**
 * Abstract factory for all model window factories.
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractModelWindowFactory extends
    AbstractFilteringFactory<ModelWindowFactory> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -8626706310150199574L;

  /**
   * The model to be displayed, type: {@link IModel}.
   */
  public static final String MODEL = "model";

}
