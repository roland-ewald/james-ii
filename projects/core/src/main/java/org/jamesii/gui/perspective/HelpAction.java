/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;

import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;

/**
 * Simple action that starts the help system
 * 
 * @author Stefan Rybacki
 */
class HelpAction extends AbstractAction {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -5556934969431293390L;

  /**
   * Creates a new help action
   */
  public HelpAction() {
    super("Help");

    Icon smallIcon;
    Icon largeIcon;
    smallIcon = IconManager.getIcon(IconIdentifier.HELP_SMALL, "Help");
    largeIcon = IconManager.getIcon(IconIdentifier.HELP_LARGE, "Help");

    putValue(LARGE_ICON_KEY, largeIcon);
    putValue(SMALL_ICON, smallIcon);

    putValue(SHORT_DESCRIPTION, "Starts the help system");
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    // do nothing
  }

}
