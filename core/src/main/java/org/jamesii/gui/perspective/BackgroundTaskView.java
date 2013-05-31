/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.application.task.TaskManagerTable;

/**
 * View in JAMES GUI that can only be open alone hence it is implemented using
 * the singleton pattern. This view is used to observe and manage the
 * {@link ITask}s currently running in the {@link TaskManager}.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class BackgroundTaskView extends AbstractWindow {
  /**
   * the instance
   */
  private static final BackgroundTaskView INSTANCE = new BackgroundTaskView();

  /**
   * Hidden constructor due to usage of singleton pattern
   */
  private BackgroundTaskView() {
    super("Running Tasks", null, Contribution.BOTTOM_VIEW);
  }

  @Override
  public Icon getWindowIcon() {
    return IconManager.getIcon(IconIdentifier.PLAY_SMALL, "!>");
  }

  @Override
  protected IAction[] generateActions() {
    return null;
  }

  @Override
  public JComponent createContent() {
    return new JScrollPane(new TaskManagerTable());
  }

  /**
   * @return the instance
   */
  public static BackgroundTaskView getInstance() {
    return INSTANCE;
  }

}
