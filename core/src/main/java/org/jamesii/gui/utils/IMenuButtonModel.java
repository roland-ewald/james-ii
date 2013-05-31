/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.beans.PropertyChangeListener;

import javax.swing.Action;
import javax.swing.JPopupMenu;

/**
 * @author Stefan Rybacki
 * 
 */
public interface IMenuButtonModel {

  String ACTION = "action";

  String MENU = "menu";

  void addPropertyChangeListener(PropertyChangeListener listener);

  Action getDefaultAction();

  JPopupMenu getPopUpMenu();

}
