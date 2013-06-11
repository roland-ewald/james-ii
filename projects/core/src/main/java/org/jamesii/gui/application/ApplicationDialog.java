/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.Window;
import java.awt.image.BufferedImage;

import javax.swing.Icon;
import javax.swing.JDialog;

/**
 * Base class for all {@link JDialog}s used within the JAMES II GUI. This class
 * overrides {@link #setVisible(boolean)} and calls {@link #pack()} in case a
 * window is shown or {@link #dispose()} in case the window is hidden.
 * 
 * @author Stefan Rybacki
 * 
 */
public class ApplicationDialog extends JDialog {

  /**
   * {@link JDialog#JDialog(Window, String)}
   * 
   * @param owner
   *          the dialog's owner
   * @param title
   *          the dialog's title
   */
  public ApplicationDialog(Window owner, String title) {
    super(owner, title);
  }

  /**
   * {@link JDialog#JDialog()}
   */
  public ApplicationDialog() {
    this(WindowManagerManager.getWindowManager() != null ? WindowManagerManager
        .getWindowManager().getMainWindow() : null, null);
  }

  /**
   * {@link JDialog#JDialog(Window)}
   * 
   * @param owner
   *          the dialog's owner
   */
  public ApplicationDialog(Window owner) {
    this(owner, null);
  }

  /**
   * {@link JDialog#JDialog()} But sets also the dialog's title
   * 
   * @param title
   *          the dialog's title
   */
  public ApplicationDialog(String title) {
    this(WindowManagerManager.getWindowManager() != null ? WindowManagerManager
        .getWindowManager().getMainWindow() : null, title);
  }

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -6199917527074922426L;

  @Override
  public void setVisible(boolean b) {
    super.setVisible(b);
    if (b && this.isDisplayable()) {
      // we need this try catch block in case the window is already closed and
      // disposed while attempting to pack
      try {
        pack();
      } catch (Exception e) {
        // nothing to do
      }
    }

    if (!b) {
      dispose();
    }
  }

  /**
   * Sets the icon for the dialog
   * 
   * @param icon
   *          the icon to set
   */
  protected void setIcon(Icon icon) {
    if (icon == null) {
      setIconImage(null);
      return;
    }

    Image i =
        new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
            BufferedImage.TYPE_INT_ARGB);
    Graphics g = i.getGraphics();
    icon.paintIcon(null, g, 0, 0);
    g.dispose();
    setIconImage(i);
  }

}
