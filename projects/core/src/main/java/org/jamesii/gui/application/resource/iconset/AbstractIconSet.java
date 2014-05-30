/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource.iconset;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.ImageIcon;

/**
 * Abstract iconset class that already implements
 * {@link #getIcon(IconIdentifier)} on basis of
 * {@link #getImage(IconIdentifier)}.
 * 
 * @author Stefan Rybacki
 */
public abstract class AbstractIconSet implements IIconSet {

  @Override
  public final Icon getIcon(IconIdentifier id) {
    Image image = getImage(id);
    if (image == null) {
      return null;
    }
    return new ImageIcon(image);
  }

}
