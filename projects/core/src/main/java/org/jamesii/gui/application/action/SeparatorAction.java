/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.action;

import java.util.concurrent.atomic.AtomicInteger;

import org.jamesii.gui.application.IWindow;

/**
 * Action that is used to represent separators within a list of actions. This
 * can be used when defining an action tree thru the {@link ActionManager} and
 * using the {@link ActionManager#createJToolBarFor(String)},
 * {@link ActionManager#createJPopupMenuFor(String)},
 * {@link ActionManager#createJMenuBarFor(String)} methods.
 * 
 * @author Stefan Rybacki
 */
public final class SeparatorAction extends AbstractAction {

  /** The Constant autoInc. */
  private static final AtomicInteger autoInc = new AtomicInteger();

  /**
   * Instantiates a new separator action.
   * 
   * @param id
   *          the id
   * @param paths
   *          the paths
   * @param window
   *          the window the action belongs to
   */
  public SeparatorAction(String id, String[] paths, IWindow window) {
    super(id == null ? String.format("SEPERATOR%010d",
        autoInc.incrementAndGet()) : id, "", paths, window);
  }

  /**
   * Instantiates a new separator action.
   * 
   * @param paths
   *          the paths
   * @param window
   *          the window the action belongs to
   */
  public SeparatorAction(String[] paths, IWindow window) {
    this(null, paths, window);
  }

  @Override
  public void execute() {
  }

  /**
   * Gets the separator for.
   * 
   * @param path
   *          the path
   * @param window
   *          the window the action is used in
   * 
   * @return the separator for
   */
  public static IAction getSeparatorFor(String path, IWindow window) {
    return new SeparatorAction(new String[] { path }, window);
  }

  @Override
  public final ActionType getType() {
    return ActionType.SEPARATOR;
  }
}
