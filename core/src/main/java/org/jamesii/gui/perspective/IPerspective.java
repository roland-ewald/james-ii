/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.List;

import org.jamesii.gui.application.IPerspectiveChangeListener;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.preferences.IPreferencesPage;

/**
 * Perspective interface used to define perspectives that can be pluged into the
 * James GUI.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IPerspective {
  /**
   * Needs to return the same action objects on succesive calls after the first
   * call.
   * 
   * @return a list of actions for that perspective
   */
  List<IAction> getActions();

  /**
   * @return the perspective's description
   */
  String getDescription();

  /**
   * @return the perspective's name
   */
  String getName();

  /**
   * Specifies whether this perspective is optional displayable or not. That
   * means perspectives that are not mandatory can be disable or enabled as the
   * user likes.
   * 
   * @return true if perspective is to be displayed always
   */
  boolean isMandatory();

  /**
   * Called when the perspective was closed. This happens when the application
   * shuts down or if this perspective is not mandatory in case it is being
   * disabled. This method can be used to clean up resources used by the
   * perspective.
   */
  void perspectiveClosed();

  /**
   * Called after the perspective is loaded and supposed to be shown. The
   * perspective can for instance open default windows in the GUI on startup and
   * so on.
   */
  void openPerspective();

  /**
   * @return a list of preferences pages that are plugged into the org.jamesii
   *         preferences dialog
   */
  List<IPreferencesPage> getPreferencesPages();

  /**
   * Adds a perspective change listener. Perspective change listener can be used
   * to track changes to perspectives like changing actions that need to be
   * refreshed in the UI.
   * 
   * @param listener
   *          the listener to attach
   */
  void addPerspectiveChangeListener(IPerspectiveChangeListener listener);

  /**
   * Removes a previously attached listener
   * 
   * @param listener
   *          the listener to remove
   */
  void removePerspectiveChangeListener(IPerspectiveChangeListener listener);
}
