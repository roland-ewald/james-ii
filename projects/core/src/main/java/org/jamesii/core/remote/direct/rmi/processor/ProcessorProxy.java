/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorState;
import org.jamesii.core.remote.direct.rmi.base.EntityProxy;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * The Class ProcessorProxy.
 * 
 * @param <PE>
 */
public class ProcessorProxy<PE extends IProcessorRef, TimeBase extends Comparable<TimeBase>> extends EntityProxy<PE>
    implements IProcessor<TimeBase>, Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2418341196688222765L;

  /**
   * reference to the proxy of the model ref of the model the processor ref is
   * working on.
   */
  private IModel model;

  /**
   * Instantiates a new processor proxy.
   * 
   * @param ref
   *          the ref
   * @param model
   *          the model
   */
  public ProcessorProxy(PE ref, IModel model) {
    super(ref);
    this.model = model;
  }

  @Override
  public void executeNextStep() {
    try {
      getRef().remoteExecuteNextStep();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }

  @Override
  public String getClassName() {
    try {
      return getRef().remoteGetClassName();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    return null;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <M extends IModel> M getModel() {

    // TODO: what to do with this old code?
    // try {
    // here we need to get the local proxy of the model and the remote one!!

    return (M) model;
    /*
     * } catch (RemoteException re) { handleRemoteException(re); } return null;
     */
  }

  @Override
  public ProcessorState getState() {
    try {
      return getRef().remoteGetState();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    return null;
  }

  @Override
  public TimeBase getTime() {
    SimSystem.report(Level.SEVERE, "no remote call of getTime!!");
    return null;
  }

  /**
   * Sets the local reference of the model (a proxy).
   * 
   * @param model
   *          the model
   */
  @Override
  public void setModel(IModel model) {
    this.model = model;
  }

  /**
   * Sets the simulation.
   * 
   * @param simulation
   *          the simulation
   */
  @Override
  public void setComputationTask(IComputationTask simulation) {
    SimSystem.report(Level.SEVERE,
        "Remotely calling set computation task is not permitted.");
    throw new OperationNotSupportedException(
        "Remotely calling set computation task is not permitted.");
  }

  @Override
  public void cleanUp() {
    if (getRef() == null) {
      return;
    }
    try {
      getRef().remoteCleanUp();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    model = null;
    setRef(null);
  }
}
