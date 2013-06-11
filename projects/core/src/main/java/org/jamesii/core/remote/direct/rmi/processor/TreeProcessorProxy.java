/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.RemoteException;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ITreeProcessor;

/**
 * The Class TreeProcessorProxy.
 * 
 * @param <R>
 */
public class TreeProcessorProxy<R extends ITreeProcessorRef, TimeBase extends Comparable<TimeBase>>
    extends ProcessorProxy<R, TimeBase> implements ITreeProcessor<TimeBase> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 628030909807139565L;

  /**
   * Instantiates a new tree processor proxy.
   * 
   * @param ref
   *          the ref
   * @param model
   *          the model
   */
  public TreeProcessorProxy(R ref, IModel model) {
    super(ref, model);
  }

  @Override
  public IProcessor<TimeBase> getParent() {
    try {
      return getRef().remoteGetParent();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    return null;
  }

  @Override
  public void setParent(IProcessor<TimeBase> parent) {
    try {
      getRef().remoteSetParent(parent);
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }

  @Override
  public void addChild(IProcessor<TimeBase> proc) {
    try {
      getRef().remoteAddChild(proc);
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }

  @Override
  public void removeChild(IProcessor<TimeBase> proc) {
    try {
      getRef().remoteRemoveChild(proc);
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }
}
