/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.iconset;

import java.awt.Image;

/**
 * Default implementation of the {@link IIconSet} interface. Where there are no
 * icon locations returned and therefore no icons provided.
 * 
 * @author Stefan Rybacki
 */
public final class DefaultIconSet extends AbstractIconSet {
  /**
   * singleton instance of icon set
   */
  private static final DefaultIconSet INSTANCE = new DefaultIconSet();

  /**
   * omitted constructor
   */
  private DefaultIconSet() {
    // nothing to do
  }

  @Override
  public Image getImage(IconIdentifier id) {
    return null;
  }

  /**
   * @return the singleton instance of this set
   */
  public static DefaultIconSet getInstance() {
    return INSTANCE;
  }

  @Override
  public String getName() {
    return "Default Icon Set";
  }
}
