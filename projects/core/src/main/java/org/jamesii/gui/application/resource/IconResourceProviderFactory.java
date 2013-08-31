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
 * Resource factory providing access to {@link IconResourceProvider} which is
 * able to load an image file into an {@link javax.swing.Icon}.
 * 
 * @author Stefan Rybacki
 */

public class IconResourceProviderFactory extends ResourceProviderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4182190479198660118L;

  @Override
  public IResourceProvider create(ParameterBlock params) {
    return IconResourceProvider.getInstance();
  }

}
