/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote;

import java.io.Serializable;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.controller.IObjectReferrer;
import org.jamesii.core.remote.hostcentral.rmi.IRemoteCommunicationCenter;
import org.jamesii.core.remote.hostcentral.rmi.RemoteCommunicationCenter;

/**
 * 
 * The Interface IMigrationController. Is implemented by components that trigger
 * an automatic migration of model and simulation entities from one physical
 * host (represented by its {@link RemoteCommunicationCenter}) to another.
 * 
 * @author Simon Bartels
 * 
 * @param <M>
 *          the location object type (e.g. IRemoteCommunicationCenter, ...)
 * 
 * @see IRemoteCommunicationCenter
 * @see IObjectReferrer
 * 
 */
public interface IMigrationController<M> extends Remote {

  /**
   * This method will tell the local location object to remove the map of
   * objects and move it to the new destination. Additionally it will inform all
   * other affected location objects. (The destination object itself will be
   * informed by another method called internal)
   * 
   * Make sure that you have properly set a location object and an
   * ObjectReferrer.
   * 
   * @param objects
   *          the objects
   * @param newLocation
   *          the migration controller which shall receive these objects
   * 
   * @return true, if migration was successful. false, when the objects are not
   *         on the controller or not serializable.
   * @throws RemoteException
   *           by RMI
   * @throws {@link MigrationException} during runtime. Have a look at the
   *         sub-classes to see what could happen.
   * 
   */
  <X extends Set<IObjectId> & Serializable> boolean migrateTo(X objects,
      IMigrationController<M> newLocation) throws RemoteException;

  /**
   * This method is called by another Migration Controller when migration has
   * been triggered. It just takes the objects and registers them at the
   * corresponding location object (e.g. the RemoteCommunicationCenter).
   * 
   * @param objects
   *          The objects that shall move here.
   * @return True, when everything went fine.
   * @throws RemoteException
   *           An RMI exception.
   */
  boolean receive(Map<IObjectId, Serializable> objects) throws RemoteException;

  /**
   * Sets the remote communication center.
   * 
   * @param rcCenter
   *          is the location object this migration handler is responsible for
   * @throws RemoteException
   */
  void setLocationObject(M rcCenter) throws RemoteException;

  /**
   * Sets the object referrer. The migration controller needs an object to tell
   * it which objects are influenced by others. This is the task of the
   * objectReferrer.
   * 
   * @param objectReferrer
   *          the object referrer to be used
   * @throws RemoteException
   * 
   */
  void setObjectReferrer(IObjectReferrer objectReferrer) throws RemoteException;

  /**
   * This method is needed for the migration, especially to inform other
   * communication centers on who received the objects.
   * 
   * @return The remote communication center associated to this migration
   *         controller.
   * @throws RemoteException
   */
  IRemoteCommunicationCenter getLocationObject() throws RemoteException;
}
