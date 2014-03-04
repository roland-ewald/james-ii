/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.failuredetection;


import java.util.HashSet;
import java.util.Set;

import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.factories.Factory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Simple structure to describe a failure that occurred at runtime. This
 * description should ultimately be distributed to the plug-in author(s), and
 * serves for documenting the failure and reason on it.
 * 
 * @author Roland Ewald
 */
public class FailureDescription {

  /** The system configuration that was used when the error occurred. */
  private final Set<Class<? extends Factory<?>>> defectiveFactories;

  /** The setting in which the given set-up failed. */
  private final TaskConfiguration taskConfiguration;

  /** The cause of the error that occurred. */
  private final Throwable cause;

  /**
   * Instantiates a new failure description.
   * 
   * @param selectionTree
   *          the selection tree that failed
   * @param config
   *          the task configuration for which it failed
   * @param t
   *          the cause
   */
  public FailureDescription(SelectionTree selectionTree,
      TaskConfiguration config, Throwable t) {
    this(new HashSet<>(
        selectionTree.getUniqueFactories()), config, t);
  }

  /**
   * Instantiates a new failure description.
   * 
   * @param failingFactories
   *          the set of failing factories
   * @param src
   *          the simulation run configuration for which they failed
   * @param t
   *          the cause
   */
  public FailureDescription(Set<Class<? extends Factory<?>>> failingFactories,
      TaskConfiguration src, Throwable t) {
    defectiveFactories = failingFactories;
    taskConfiguration = src;
    cause = t;
  }

  /**
   * Gets the task configuration.
   * 
   * @return the task configuration
   */
  public TaskConfiguration getTaskConfiguration() {
    return taskConfiguration;
  }

  /**
   * Gets the cause of the failure.
   * 
   * @return the cause
   */
  public Throwable getCause() {
    return cause;
  }

  /**
   * Gets the defective factories.
   * 
   * @return the defectiveFactories
   */
  public Set<Class<? extends Factory<?>>> getDefectiveFactories() {
    return defectiveFactories;
  }
}
