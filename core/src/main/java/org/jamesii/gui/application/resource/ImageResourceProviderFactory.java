/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.awt.Image;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.resource.plugintype.ResourceProviderFactory;

/**
 * Resource factory providing access to {@link ImageResourceProvider} which is
 * able to load an image file into an {@link Image}.
 * 
 * @author Stefan Rybacki
 */
public class ImageResourceProviderFactory extends ResourceProviderFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -7618827435436376457L;

  @Override
  public IResourceProvider create(ParameterBlock params) {
    return ImageResourceProvider.getInstance();
  }

}
