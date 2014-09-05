/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.service.view.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.service.view.IServiceView;

/**
 * Base factory to create service views.
 * 
 * @author Stefan Leye
 */
public abstract class ServiceViewFactory extends Factory<IServiceView>
    implements IParameterFilterFactory {

  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = 5906362716828045252L;

  /**
   * The tag for the service reference in the parameter block.
   */
  public static final String SERVICE = "service";

  /**
   * The tag for the desired contribution in the parameter block.
   */
  public static final String CONTRIBUTAION = "contribution";

  /**
   * Creates a view.
 * @param params
   *          parameters containing the service
 * @return the view for the service
   */
  @Override
  public abstract IServiceView create(ParameterBlock params, Context context);

}
