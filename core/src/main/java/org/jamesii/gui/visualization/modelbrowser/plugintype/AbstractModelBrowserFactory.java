/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.modelbrowser.plugintype;

import org.jamesii.core.factories.AbstractFilteringFactory;

/**
 * Abstract factory for model browsers.
 * 
 * @author Jan Himmelspach
 * 
 */
public class AbstractModelBrowserFactory extends
    AbstractFilteringFactory<ModelBrowserFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3324173416019602997L;

  /** The Constant MODEL. */
  public static final String MODEL = "model";

}
