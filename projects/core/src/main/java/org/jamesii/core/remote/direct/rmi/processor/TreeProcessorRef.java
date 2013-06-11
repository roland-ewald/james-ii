/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.processor;

import java.rmi.RemoteException;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ITreeProcessor;

/**
 * The Class TreeProcessorRef.
 * 
 * @param <L>
 */
public class TreeProcessorRef<TimeBase extends Comparable<TimeBase>, L extends ITreeProcessor<TimeBase>>
    extends ProcessorRef<L> implements ITreeProcessorRef {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8739236165563630863L;

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public TreeProcessorRef(L local) throws RemoteException {
    super(local);
  }

  @Override
  public IProcessor remoteGetParent() throws RemoteException {
    return getLocal().getParent();
  }

  @Override
  public void remoteSetParent(IProcessor parent) throws RemoteException {
    getLocal().setParent(parent);
  }

  @Override
  public void remoteAddChild(IProcessor proc) throws RemoteException {
    getLocal().addChild(proc);
  }

  @Override
  public void remoteRemoveChild(IProcessor proc) throws RemoteException {
    getLocal().removeChild(proc);
  }
}
