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
 * Resource factory providing access to {@link TextfileResourceProvider} which
 * is able to load a text file into a {@link String} using an optional specified
 * encoding.
 * 
 * @author Stefan Rybacki
 */
public class TextfileResourceProviderFactory extends ResourceProviderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 2625522457582520888L;

  @Override
  public IResourceProvider create(ParameterBlock params) {
    return TextfileResourceProvider.getInstance();
  }

}
