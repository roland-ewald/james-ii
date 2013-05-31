/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import javax.swing.Icon;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.ToggleAction;

/**
 * Action class that is used in the {@link LafPerspective} for selecting look
 * and feels.
 * 
 * @author Stefan Rybacki
 */
class LafChooseAction extends ToggleAction implements ILafChangeListener {

  /**
   * the look and feel activated by this action
   */
  private LookAndFeelInfo info;

  /**
   * Creates a new look and feel choose action
   * 
   * @param id
   *          the action id
   * @param icon
   *          the action icon
   * @param paths
   *          the action paths
   * @param info
   *          the look and feel info
   * @param window
   *          the window the action is used in
   */
  public LafChooseAction(String id, Icon icon, String[] paths,
      LookAndFeelInfo info, IWindow window) {
    super(id, info.getName(), icon, paths, null, null, window);
    this.info = info;
    LafManager.addLafChangeListener(this);
    activeLookAndFeelChanged(LafManager.getActiveLookAndFeel());
  }

  @Override
  protected void toggleChanged(boolean previousState) {
    if (isToggleOn() && !previousState) {
      LafManager.setActiveLookAndFeel(info);
    }
    setToggleOn(UIManager.getLookAndFeel().getClass().getName()
        .equals(info.getClassName()));
  }

  @Override
  public void activeLookAndFeelChanged(LookAndFeelInfo i) {
    setToggleOn(i != null && i.getClassName().equals(info.getClassName()));
  }

  @Override
  public void lookAndFeelAdded(LookAndFeelInfo i) {
  }

  @Override
  public void lookAndFeelRemoved(LookAndFeelInfo i) {
  }

}
