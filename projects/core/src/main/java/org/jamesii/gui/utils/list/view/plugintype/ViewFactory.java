/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view.plugintype;

import javax.swing.JComponent;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base class for all factories for creating views.
 * 
 * @author Jan Himmelspach
 */
public abstract class ViewFactory extends Factory<JComponent> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7126627936341483320L;

  public static final String DATA = "DATA";

  public static final String PROPERTIES = "PROPERTIES";

  /**
   * Instantiates a new view factory.
   */
  public ViewFactory() {
    super();
  }

  /**
   * Return a new instance of the view to be used.
 * @param parameter
 * @return the event queue
   */
  @Override
  public abstract JComponent create(ParameterBlock parameter, Context context);

}
