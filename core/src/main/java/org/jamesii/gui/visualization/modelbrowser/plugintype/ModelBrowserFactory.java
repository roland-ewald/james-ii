/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.modelbrowser.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.IWindow;

/**
 * Super class for all factories of model browsers.
 * 
 * The supportsParameters method checks for a parameter named modelClass
 * containing the class type info of the top most model. The model has to be in
 * a tag with the value {@value AbstractModelBrowserFactory#MODEL}, can be
 * accessed/used by the constant {@link AbstractModelBrowserFactory#MODEL}.
 * 
 * @author Jan Himmelspach
 */
public abstract class ModelBrowserFactory extends Factory<IWindow> implements
    IParameterFilterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5113336695599211601L;

  /**
   * Creates a model browser.
   * 
   * 
   * @param params
   *          the parameter block to configure the model browser
   * @return newly created model browser
   */
  @Override
  public abstract IWindow create(ParameterBlock params);

}
