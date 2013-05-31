/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import javax.swing.Icon;
import javax.swing.KeyStroke;

import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.base.URLTreeNodeURL;

/**
 * Empty action that can be used to represent non empty nodes in the action
 * tree. And are basically used to group {@link IAction}s. In menus and tool
 * bars this {@link IAction} initiates a sub menu generation.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class ActionSet extends AbstractAction {

  /**
   * Creates a new {@link ActionSet} with the specified parameters.
   * 
   * @param id
   *          the action set's id
   * @param label
   *          the action's label
   * @param path
   *          the path in {@link URLTreeNodeURL} format
   * @param keyStroke
   *          the key stroke for this action (in a format
   *          {@link KeyStroke#getKeyStroke(String) understands})
   * @param mnemonic
   *          the action's mnemonic
   * @param icon
   *          the action's icon
   * @param window
   *          the window the action belongs to
   */
  public ActionSet(String id, String label, String path, String keyStroke,
      Integer mnemonic, Icon icon, IWindow window) {
    this(id, label, new String[] { path }, keyStroke, mnemonic, icon, window);
  }

  /**
   * Creates a new {@link ActionSet} with the specified parameters.
   * 
   * @param id
   *          the action set's id
   * @param label
   *          the action's label
   * @param path
   *          the paths in {@link URLTreeNodeURL} format
   * @param keyStroke
   *          the key stroke for this action (in a format
   *          {@link KeyStroke#getKeyStroke(String) understands})
   * @param mnemonic
   *          the action's mnemonic
   * @param icon
   *          the action's icon
   * @param window
   *          the window the action belongs to
   */
  public ActionSet(String id, String label, String[] path, String keyStroke,
      Integer mnemonic, Icon icon, IWindow window) {
    super(id, label, null, null, icon, path, keyStroke, mnemonic, window);
  }

  /**
   * Creates a new {@link ActionSet} with the specified parameters.
   * 
   * @param id
   *          the action set's id
   * @param label
   *          the action's label
   * @param path
   *          the path in {@link URLTreeNodeURL} format
   * @param window
   *          the window the action belongs to
   */
  public ActionSet(String id, String label, String path, IWindow window) {
    this(id, label, path, null, null, null, window);
  }

  /**
   * Creates a new {@link ActionSet} with the specified parameters.
   * 
   * @param id
   *          the action set's id
   * @param label
   *          the action's label
   * @param path
   *          the paths in {@link URLTreeNodeURL} format
   * @param window
   *          the window the action belongs to
   */
  public ActionSet(String id, String label, String[] path, IWindow window) {
    this(id, label, path, null, null, null, window);
  }

  @Override
  public final void execute() {
    // not needed
  }

  @Override
  public final ActionType getType() {
    return ActionType.ACTIONSET;
  }
}
