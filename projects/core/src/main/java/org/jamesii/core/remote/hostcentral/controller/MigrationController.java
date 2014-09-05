/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.controller;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.core.remote.IMigrationController;
import org.jamesii.core.remote.exceptions.MigrationDeniedException;
import org.jamesii.core.remote.exceptions.NoLocalObjectException;
import org.jamesii.core.remote.exceptions.NonSerializableException;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.rmi.CommunicationCenterFactory;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;

/**
 * The class MigrationController.
 * 
 * It's responsible for keeping references in all RemoteCommunicationCenters up
 * to date.
 * 
 * @author Simon Bartels
 */
public class MigrationController extends UnicastRemoteObject implements
    IMigrationController<IRemoteCommunicationCenter> {

  /**
   * The serial version id.
   */
  private static final long serialVersionUID = 7007732646846552852L;

  /** The rc center. */
  private IRemoteCommunicationCenter rcCenter;

  /** The object referrer. */
  private IObjectReferrer objectReferrer;

  /**
   * Instantiates a new migration controller.
   * 
   * @throws RemoteException
   *           May be caused by RMI.
   */
  public MigrationController() throws RemoteException {
    super();
    this.rcCenter =
        new CommunicationCenterFactory().create(null, SimSystem.getRegistry().createContext()).getFirstValue();
  }

  @Override
  public <X extends Set<IObjectId> & Serializable> boolean migrateTo(X objects,
      IMigrationController<IRemoteCommunicationCenter> newLocation) {

    Map<IObjectId, Serializable> migrationMap = new HashMap<>();

    // make a map of the real objects
    for (IObjectId id : objects) {
      Object o = null;
      try {
        o = rcCenter.getObjectById(id);
        if (null == o) {
          throw new NoLocalObjectException(id);
        }
      } catch (RemoteException e) {
        // This should not happen as RMI is not involved
        SimSystem.report(e);
      }

      if (o instanceof Serializable) {
        migrationMap.put(id, (Serializable) o);
      } else {
        if (o != null) {
          throw new NonSerializableException(id, o.getClass());
        } else {
          throw new NonSerializableException(id);
        }

      }
    }

    // let's try to move the objects
    try {
      if (!newLocation.receive(migrationMap)) {
        throw new MigrationDeniedException();
        // return false;
      }
    } catch (RemoteException e1) {
      SimSystem.report(e1);
      return false;
    }

    try {
      // a set of all remote communication centers which need to be
      // informed of migrated objects
      Set<IRemoteCommunicationCenter> rccToInform = new HashSet<>();

      // TODO: This loop has been running before!
      // Could this code be more effetive?
      // sb513: I think not as we have to wait whether the migration succeeds or
      // not.
      for (IObjectId x : objects) {
        // this is the set of all partners from object
        Set<IObjectId> partners =
            objectReferrer.getCommunicationPartnersFrom(x);

        for (IObjectId id : partners) {
          // we need to inform every communication center which holds
          // a partner from object
          rccToInform.add(rcCenter.getLocationOfObject(id));
        }
      }

      // just in case our main candidate is not in the set
      rccToInform.add(this.rcCenter);

      // The corresponding communication center from the destination
      // controller
      IRemoteCommunicationCenter newCommunicationCenter =
          newLocation.getLocationObject();
      for (IRemoteCommunicationCenter rcc : rccToInform) {
        rcc.updateObjectLocations(objects, newCommunicationCenter);
      }
      return true;

    } catch (RemoteException e) {
      // TODO: Think about what to do when migration fails...
      SimSystem.report(e);
    }
    return false;
  }

  @Override
  public void setObjectReferrer(IObjectReferrer objectReferrer) {
    this.objectReferrer = objectReferrer;
  }

  @Override
  public void setLocationObject(IRemoteCommunicationCenter rcCenter) {
    this.rcCenter = rcCenter;
  }

  @Override
  public boolean receive(Map<IObjectId, Serializable> objects)
      throws RemoteException {
    for (Entry<IObjectId, Serializable> e : objects.entrySet()) {
      rcCenter.registerLocalObject(e.getKey(), e.getValue());
    }
    return true;
  }

  @Override
  public IRemoteCommunicationCenter getLocationObject() throws RemoteException {
    return rcCenter;
  }

}
