/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;

/**
 * Manages a selection hook to the registry and thus allows a global public
 * access to factory selection data (in the form of selection trees).
 * 
 * @see SelectionHook
 * @see SelectionTree
 * @see Registry
 * 
 * @author Roland Ewald
 * 
 */
public final class SelectionHookSingleton {

  /** The instance. */
  private static SelectionHookSingleton instance;

  /** Selection hook to generate selection trees. */
  private final SelectionHook selectionHook;

  /**
   * Instantiates a new selection hook singleton.
   */
  private SelectionHookSingleton() {
    Registry registry = SimSystem.getRegistry();
    selectionHook = new SelectionHook(registry.getFactorySelectionHook());
    registry.installFactorySelectionHook(selectionHook);
  }

  /**
   * Gets the single instance of SelectionHookSingleton.
   * 
   * @return single instance of SelectionHookSingleton
   */
  public static synchronized SelectionHookSingleton getInstance() {
    if (instance == null) {
      instance = new SelectionHookSingleton();
    }
    return instance;
  }

  /**
   * Gets the selection tree for the given task.
   * 
   * @param id
   *          the id of the given task
   * @return the selection tree
   */
  public SelectionTree getSelectionTree(ComputationTaskIDObject id) {
    return selectionHook.getSelectionTree(id);
  }

  /**
   * Clear information collected for the given task (for clean-up; otherwise
   * this is a memory leak!).
   * 
   * @param id
   *          the task id
   */
  public void clearTask(ComputationTaskIDObject id) {
    selectionHook.clearTask(id);
  }

  /**
   * Reset (all collected data is discarded).
   */
  public void reset() {
    selectionHook.reset();
  }

  /**
   * Start recording.
   * 
   * @throws IllegalStateException
   *           in case the hook is already recording (to fail early, as
   *           concurrent access to these methods may cause very subtle yet
   *           severe problems)
   */
  public void startRecording() {
    if (selectionHook.isRecordingSelections()) {
      throw new IllegalStateException(
          "The selection hook is already recording, so starting it makes no sense.");
    }
    selectionHook.start();
  }

  /**
   * Stop recording.
   * 
   * @throws IllegalStateException
   *           in case the hook is not recording (to fail early, as concurrent
   *           access to these methods may cause very subtle yet severe
   *           problems)
   */
  public void stopRecording() {
    if (!selectionHook.isRecordingSelections()) {
      throw new IllegalStateException(
          "The selection hook is not recording anything, so stopping it makes no sense.");
    }
    selectionHook.stop();
  }
}
