/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.actions;

import java.lang.ref.WeakReference;

import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IComputationTaskExecutionListener;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.gui.application.action.DefaultSwingAction;

/**
 * Basic class for actions to control a simulation run.
 * 
 * @author Roland Ewald
 * @author Stefan Leye
 */
public abstract class AbstractSimAction extends DefaultSwingAction implements
    IComputationTaskExecutionListener {
  /**
   * The serialization ID.
   */
  private static final long serialVersionUID = -7489619500449388349L;

  /** Reference to runtime information of the current simulation. */
  private WeakReference<ComputationTaskRuntimeInformation> simRuntimeInfo =
      null;

  /**
   * Default constructor.
   * 
   * @param name
   *          name of the action
   */
  public AbstractSimAction(String name) {
    super(name);
    putValue(SHORT_DESCRIPTION, name);
  }

  /**
   * Checks whether there is a current simulation that can be controlled.
   * 
   * @return true if there is a current simulation that can be controlled
   */
  public boolean simControllingPossible() {
    return ComputationTaskRuntimeInformation
        .computationTaskControlPossible(simRuntimeInfo.get());
  }

  /**
   * Gets the sim runtime info.
   * 
   * @return the sim runtime info
   */
  public final ComputationTaskRuntimeInformation getSimRuntimeInfo() {
    return simRuntimeInfo.get();
  }

  /**
   * Initialises action with new SRTI and status.
   * 
   * @param srti
   *          the new simulation runtime information
   */
  public void init(ComputationTaskRuntimeInformation srti) {
    if (simRuntimeInfo != null) {
      ComputationTaskRuntimeInformation simRTInfo = simRuntimeInfo.get();
      if (simRTInfo != null) {
        simRTInfo.removeComputationTaskExecutionListener(this);
      }
    }

    if (srti == null) {
      setEnabled(false);
      simRuntimeInfo = null;
    } else {
      simRuntimeInfo = new WeakReference<>(srti);
      setEnabled(true);
      srti.addComputationTaskExecutionListener(this);
    }
  }

  /**
   * Return the model or null if one of the objects on the path is null (or the
   * model itself)
   * 
   * @param srti
   * @return
   */
  protected final IModel getModel() {
    if (getSimRuntimeInfo().getComputationTask() == null) {
      return null;
    }
    return getSimRuntimeInfo().getComputationTask().getModel();
  }

  /**
   * Return the model or null if one of the objects on the path is null (or the
   * model itself)
   * 
   * @param srti
   * @return
   */
  protected final IProcessor getProcessor() {
    if (getSimRuntimeInfo().getComputationTask() == null
        || getSimRuntimeInfo().getComputationTask().getProcessorInfo() == null) {
      return null;
    }
    return getSimRuntimeInfo().getComputationTask().getProcessorInfo()
        .getLocal();
  }

  @Override
  public void stateChanged(ComputationTaskRuntimeInformation srti) {
    refreshIcon(srti);
  }

  /**
   * Refresh icon.
   * 
   * @param srti
   *          the srti
   */
  public abstract void refreshIcon(ComputationTaskRuntimeInformation srti);

  /**
   * @param simRuntimeInfo
   *          the simRuntimeInfo to set
   */
  public void setSimRuntimeInfo(
      WeakReference<ComputationTaskRuntimeInformation> simRuntimeInfo) {
    this.simRuntimeInfo = simRuntimeInfo;
  }
}