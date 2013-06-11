/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.rmi;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;

/**
 * The Class LocalCommunicationCenter. Wraps the remote communication center
 * from the local side to hide the remote interface stuff.
 * 
 * Implements the
 * {@link org.jamesii.core.remote.hostcentral.IRemoteMethodCaller} interface
 * which shall be used by models / simulators to communicate with remote
 * instances.
 * 
 * @author Jan Himmelspach
 * @author Simon Bartels
 */
public class LocalCommunicationCenter implements IRemoteMethodCaller {

  /** The remote communication center wrapped by this instance. */
  private final RemoteCommunicationCenter remote;

  /**
   * Instantiates a new local communication center.
   * 
   * @param remote
   *          the remote communication center to wrap
   */
  public LocalCommunicationCenter(RemoteCommunicationCenter remote) {
    super();
    this.remote = remote;
  }

  @Override
  public Object executeMethod(String methodName, Object[] parameters,
      IObjectId objectID) {
    // just wrap the call
    return remote.executeMethodOut(methodName, parameters, objectID);
  }

  /**
   * Gets the {@link RemoteCommunicationCenter}.
   * 
   * @return the remote communication center
   */
  public RemoteCommunicationCenter getRemoteCC() {
    return remote;
  }

  @Override
  public void registerObject(IObjectId objectID, Object object) {
    try {
      remote.registerLocalObject(objectID, object);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
  }
}
