/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import javax.swing.UIManager.LookAndFeelInfo;

/**
 * Listener interface used in the {@link LafManager}. It provides access to
 * changes to the currently active look and feel as well as to added or removed
 * look and feels.
 * 
 * @author Stefan Rybacki
 */
public interface ILafChangeListener {
  /**
   * Called if a look and feel was added to the {@link LafManager}
   * 
   * @param info
   *          the info for the look and feel that was added
   */
  void lookAndFeelAdded(LookAndFeelInfo info);

  /**
   * Called if a look and feel was removed
   * 
   * @param info
   *          the info for the look and feel that was removed
   */
  void lookAndFeelRemoved(LookAndFeelInfo info);

  /**
   * Called if the currently active look and feel changed
   * 
   * @param info
   *          the info for the new active look and feel
   */
  void activeLookAndFeelChanged(LookAndFeelInfo info);
}
