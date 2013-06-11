/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.autotask;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.autotask.plugintype.AutoTaskFactory;

/**
 * This class provides static access to all available auto tasks, so that the
 * James GUI can use this class to retrieve all auto tasks and can execute them
 * accordingly.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class AutoTaskManager {
  /**
   * omitted constructor
   */
  private AutoTaskManager() {
  }

  /**
   * @return all available auto tasks
   */
  public static List<IAutoTask> getAutoTasks() {
    List<IAutoTask> autoTasks = new ArrayList<>();
    List<AutoTaskFactory> taskFactories = null;
    try {
      taskFactories =
          SimSystem.getRegistry().getFactories(AutoTaskFactory.class);
    } catch (Exception e) {
      SimSystem.report(Level.WARNING, "Error getting factories for auto tasks",
          e);
    }

    if (taskFactories == null) {
      taskFactories = new ArrayList<>();
    }

    ParameterBlock params = new ParameterBlock();

    for (AutoTaskFactory f : taskFactories) {
      try {
        IAutoTask autoTask = f.create(params);
        if (autoTask != null) {
          autoTasks.add(autoTask);
        }
      } catch (Exception e) {
        SimSystem.report(Level.WARNING,
            "Error creating auto task " + f.getName(), e);
      }

    }

    return autoTasks;
  }

}
