/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application;

import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.perspective.IPerspective;

/**
 * Listener interface reacting on changes to {@link IPerspective}s.
 * 
 * @author Stefan Rybacki
 */
public interface IPerspectiveChangeListener {
  /**
   * Called whenever the actions the perspective provides change. This is useful
   * in case the perspective wants to provide a list of user defined commands
   * that can change during runtime as the user adds or removes those commands.
   * 
   * @param perspective
   *          the affected perspective
   * @param oldActions
   *          the old actions
   */
  void perspectiveActionsChanged(IPerspective perspective, IAction[] oldActions);
}
