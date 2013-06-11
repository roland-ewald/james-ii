/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import java.awt.Image;
import java.io.InputStream;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 * Simple provider class that supports the loading of icons as resources. It
 * loads the icon from the given location and returns it as resource.
 * 
 * @author Stefan Rybacki
 * 
 */
final class IconResourceProvider implements IResourceProvider {
  /**
   * singleton instance
   */
  private static final IResourceProvider instance = new IconResourceProvider();

  @Override
  public Object getResourceFor(String location, Map<String, String> params,
      Class<?> requestingClass) {

    Image image = null;
    try {
      InputStream stream = null;
      if (requestingClass != null) {
        stream = requestingClass.getResourceAsStream(location);
      } else {
        stream = getClass().getResourceAsStream(location);
      }
      if (stream == null) {
        return null;
      }
      image = ImageIO.read(stream);
    } catch (Exception ex) {
      throw new RuntimeException("Was not able to read a resource image from: "
          + location, ex);
    }

    return new ImageIcon(image);
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
  private IconResourceProvider() {
    // nothing to do here
  }

  @Override
  public boolean canHandleDomain(String domain) {
    return domain.equals("icon");
  }

  @Override
  public String[] getSupportedDomains() {
    return new String[] { "icon" };
  }
}
