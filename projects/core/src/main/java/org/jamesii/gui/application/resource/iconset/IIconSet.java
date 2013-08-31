/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.iconset;

import java.awt.Image;

import javax.swing.Icon;

/**
 * Interface for an icon set that is used throughout the James application for
 * menu, toolbar and other icons. An implementing class can replace the current
 * used icon set and therefore change the used icons for the application.
 * <p>
 * Note: there is a set of icons that should be provided by an implementing
 * class and is defined through {@link IconIdentifier}
 * 
 * @see #getIcon(IconIdentifier)
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IIconSet {
  /**
   * Returns a an icon for a given id.
   * <p>
   * The implementing class should make use of
   * {@link org.jamesii.gui.application.resource.ApplicationResourceManager#getResource(String, Class)}
   * to enable automatic caching and so on.
   * 
   * @param id
   *          the icons id
   * @return the icon for the given id
   */
  Icon getIcon(IconIdentifier id);

  /**
   * Returns an image for a given id.
   * <p>
   * The implementing class should make use of
   * {@link org.jamesii.gui.application.resource.ApplicationResourceManager#getResource(String, Class)}
   * to enable automatic caching and so on.
   * 
   * @param id
   *          the icons id
   * @return the image for given id
   */
  Image getImage(IconIdentifier id);

  /**
   * Returns a human readable name for the iconset.
   * 
   * @return a human readable name of the icon set
   */
  String getName();

}
