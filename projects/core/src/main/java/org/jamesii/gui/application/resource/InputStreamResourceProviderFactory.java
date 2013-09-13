/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.plugintype.ResourceProviderFactory;

/**
 * Resource factory providing access to {@link InputStreamResourceProvider}
 * which is able to load a resource into an {@link java.io.InputStream}.
 * 
 * @author Stefan Rybacki
 */

public class InputStreamResourceProviderFactory extends ResourceProviderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -4924122207760948893L;

  @Override
  public IResourceProvider create(ParameterBlock params) {
    return InputStreamResourceProvider.getInstance();
  }

}
