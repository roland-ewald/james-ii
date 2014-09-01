/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.awt.Toolkit;
import java.net.URL;
import java.util.Map;

/**
 * Resource provider providing images from specified location.
 * 
 * @author Stefan Rybacki
 */
final class ImageResourceProvider implements IResourceProvider {
  /**
   * singleton instance
   */
  private static final IResourceProvider instance = new ImageResourceProvider();

  @Override
  public final Object getResourceFor(String location,
      Map<String, String> params, Class<?> requestingClass) {
    URL stream;
    if (requestingClass == null) {
      stream = getClass().getResource(location);
    } else {
      stream = requestingClass.getResource(location);
    }
    if (stream != null) {
      return Toolkit.getDefaultToolkit().createImage(stream);
    }
    return null;
  }

  /**
   * @return an instance of this provider
   */
  public static IResourceProvider getInstance() {
    return instance;
  }

  /**
   * hidden constructor
   */
  private ImageResourceProvider() {
    // nothing to do here
  }

  @Override
  public boolean canHandleDomain(String domain) {
    return domain.equals("image");
  }

  @Override
  public String[] getSupportedDomains() {
    return new String[] { "image" };
  }
}
