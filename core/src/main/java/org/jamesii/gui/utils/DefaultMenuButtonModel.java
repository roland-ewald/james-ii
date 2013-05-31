/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * @author Stefan Rybacki
 * 
 */
public class DefaultMenuButtonModel extends AbstractMenuButtonModel {

  private JPopupMenu menu;

  private Action defaultAction;

  public DefaultMenuButtonModel(JPopupMenu menu, Action defaultAction) {
    super();
    this.menu = menu;
    this.defaultAction = defaultAction;
  }

  @Override
  public Action getDefaultAction() {
    return defaultAction;
  }

  @Override
  public JPopupMenu getPopUpMenu() {
    return menu;
  }

}
