/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import javax.swing.Icon;

import org.jamesii.gui.application.IWindow;

/**
 * Basic action providing abstract access to a toggleable {@link IAction}.
 * 
 * @author Stefan Rybacki
 */
public abstract class ToggleAction extends AbstractAction {

  /**
   * @param id
   * @param label
   * @param icon
   * @param paths
   * @param keyStroke
   * @param mnemonic
   * @param window
   *          the window the action belongs to
   */
  public ToggleAction(String id, String label, Icon icon, String[] paths,
      String keyStroke, Integer mnemonic, IWindow window) {
    super(id, label, icon, paths, keyStroke, mnemonic, window);
  }

  @Override
  public final void execute() {
  }

  @Override
  public final ActionType getType() {
    return ActionType.TOGGLEACTION;
  }

  @Override
  protected abstract void toggleChanged(boolean previousState);
}
