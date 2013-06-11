/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view.item;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.logging.Level;

import javax.imageio.ImageIO;
import javax.swing.Icon;

import org.jamesii.SimSystem;
import org.jamesii.gui.utils.list.IViewableItem;

/**
 * Base class for items. Provides functionality needed by all item
 * implementations.
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class BasicItem implements IViewableItem {

  public BasicItem() {
    super();
  }

  /**
   * Tries to load an item if source is non null, if this fails a default item
   * will be generated.
   * 
   * @param dim
   * @param source
   * @return
   */
  protected Icon getIcon(final Dimension dim, URI source) {

    Image image;
    if (source != null) {

      try {
        image = ImageIO.read(new File(source));
        if (image != null) {
          return new ImageIcon(dim, image);
        }
      } catch (IOException | IllegalArgumentException e) {
        SimSystem.report(Level.WARNING, "Was not able to read the icon from "
            + source, e);
      }
    }

    return new Icon() {
      @Override
      public int getIconHeight() {
        return dim.height;
      }

      @Override
      public int getIconWidth() {
        return dim.width;
      }

      @Override
      public void paintIcon(Component c, Graphics g, int x, int y) {

        g.setColor(Color.WHITE);
        g.draw3DRect(x + 2, y + 2, getIconWidth() - 5, getIconHeight() - 5,
            false);
        g.setColor(Color.BLACK);

        StringBuilder build = new StringBuilder();
        for (String s : getLabel().split(" ")) {
          build.append(s.substring(0, 1));
        }

        // g.setFont(g.getFont().deriveFont(.5f));
        Rectangle oldClipRect = g.getClipBounds();
        g.clipRect(x + 2, y + 2, getIconWidth() - 5, getIconHeight() - 5);
        g.drawString(build.toString(), x + 4, getIconHeight() - 5);
        g.setClip(oldClipRect);
      }
    };
  }

  protected abstract Properties getInternalProperties();

  @SuppressWarnings("unchecked")
  @Override
  public <V> V getProperty(String ident) {

    Object result = getInternalProperties().getValue(ident, this);
    if (result != null) {
      return (V) result;
    }
    return null;
  }

  /**
   * Add a new property to the list of available properties of this item type.
   * 
   * @param property
   */
  protected void registerProperty(IProperty<?> property) {
    getInternalProperties().registerProperty(property);
  }

  /**
   * Provide access to the internal list of properties.
   */
  @Override
  public List<IProperty<? extends IViewableItem>> getProperties() {
    return getInternalProperties().getProperties();
  }

}
