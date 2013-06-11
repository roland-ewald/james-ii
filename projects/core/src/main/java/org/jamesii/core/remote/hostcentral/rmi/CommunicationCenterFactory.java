/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.rmi;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;
import org.jamesii.core.util.misc.Pair;

/**
 * Factory to create communication centers.
 * 
 * @author Simon Bartels
 * 
 */
public class CommunicationCenterFactory extends
    Factory<Pair<IRemoteCommunicationCenter, IRemoteMethodCaller>> {

  /** The serial version uid. */
  private static final long serialVersionUID = -3644971813522133027L;

  /**
   * The local communication center.
   */
  private static volatile IRemoteMethodCaller lcc = null;

  private static volatile RemoteCommunicationCenter center = null;

  /**
   * This method returns a pair of remote and local communication center.
   * 
   * @param pb
   *          Currently ignored.
   * @return The two singletons Remote- and LocalCommunicationCenter.
   */
  @Override
  public Pair<IRemoteCommunicationCenter, IRemoteMethodCaller> create(
      ParameterBlock pb) {
    try {
      if (center == null) {
        center = new RemoteCommunicationCenter();
      }
      RemoteCommunicationCenter rcc = center;// RemoteCommunicationCenter.getInstance();
      // initialize our lcc only once
      if (null == lcc) {
        lcc = new LocalCommunicationCenter(rcc);
      }
      return new Pair<IRemoteCommunicationCenter, IRemoteMethodCaller>(rcc, lcc);
    } catch (RemoteException e) {
      SimSystem.report(e);
    }
    return null;
  }

}
