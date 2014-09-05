/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.IResourceProvider;

/**
 * Basic factory for all factories that provide resource providers
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class ResourceProviderFactory extends
    Factory<IResourceProvider> {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 1167909894284902432L;

  /**
   * Creates resource provider factory.
 * @param params
   *          parameters
 * @return resource provider factory
   */
  @Override
  public abstract IResourceProvider create(ParameterBlock params, Context context);

}
