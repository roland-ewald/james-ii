/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Simple action that closes the associated application
 * 
 * @author Stefan Rybacki
 */
public class QuitAction extends AbstractAction {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -5525640827779193855L;

  /**
   * the application that should be closed by the action
   */
  private transient IApplicationManager app;

  /**
   * Instantiates a new quit action.
   * 
   * @param app
   *          the application to close by this action
   */
  public QuitAction(IApplicationManager app) {
    super("Quit");
    this.app = app;

    Icon smallIcon;
    Icon largeIcon;
    smallIcon = IconManager.getIcon(IconIdentifier.QUIT_SMALL, "Quit");
    largeIcon = IconManager.getIcon(IconIdentifier.QUIT_LARGE, "Quit");

    putValue(LARGE_ICON_KEY, largeIcon);
    putValue(SMALL_ICON, smallIcon);

    putValue(SHORT_DESCRIPTION, "Quits the application");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    app.close(false);
  }
}
