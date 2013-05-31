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
import org.jamesii.core.processor.ProcessorState;
import org.jamesii.core.remote.direct.rmi.base.EntityRef;

/**
 * The Class ProcessorRef.
 * 
 * @author Jan Himmelspach
 * @param <PL>
 */
public class ProcessorRef<PL extends IProcessor> extends EntityRef<PL>
    implements IProcessorRef {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 5446694267570364135L;

  /**
   * The Constructor.
   * 
   * @param local
   *          the local
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ProcessorRef(PL local) throws RemoteException {
    super(local);
  }

  @Override
  public IProcessor getProxy() {
    return new ProcessorProxy<>(this, null);
  }

  @Override
  public void remoteExecuteNextStep() throws RemoteException {
    getLocal().executeNextStep();
  }

  @Override
  public IModel remoteGetModel() throws RemoteException {
    return getLocal().getModel();
  }

  @Override
  public ProcessorState remoteGetState() throws RemoteException {
    return getLocal().getState();
  }

  @Override
  public void remoteCleanUp() throws RemoteException {
    if (getLocal() == null) {
      return;
    }
    getLocal().cleanUp();
    setLocal(null);
  }

}
