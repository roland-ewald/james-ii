/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Set;

import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.util.id.IUniqueID;

/**
 * The Interface IRemoteCommunicationCenter.
 */
public interface IRemoteCommunicationCenter extends Remote {

  /**
   * Execute method by calling the method of the local object, thus of an object
   * being on the same host as this center instance.
   * 
   * @param methodName
   *          the method name
   * @param parameters
   *          the parameters
   * @param objectID
   *          the object id
   * 
   * @return the object
   * 
   * @throws RemoteException
   *           the remote exception
   */
  Object executeMethodIn(String methodName, Object[] parameters,
      IObjectId objectID) throws RemoteException;

  /**
   * Introduce new communication center. <br>
   * Puts rc into a set of other communication centers which will be informed,
   * when new objects are registered locally.
   * 
   * @param rc
   *          The remote communication center to be introduced to this one.
   * @throws RemoteException
   *           The remote exception
   */
  void introduceNewCommunicationCenter(IRemoteCommunicationCenter rc)
      throws RemoteException;

  /**
   * Update object locations. <br>
   * If the newCommunicationCenter is the instance this method is called on then
   * this method removes the objects from the list of remote objects. <br>
   * If the newCommunicationCenter is a remote instance this method will remove
   * all entries in its local reference table to any keys contained in the list
   * passed. Afterwards it will update the remote locations list with the entry.
   * 
   * @param objects
   *          the list of objects being at a new location.
   * @param newCommunicationCenter
   *          the new communication center the list of passed objects is located
   *          on
   * 
   * @throws RemoteException
   *           the remote exception
   */
  void updateObjectLocations(Set<IObjectId> objects,
      IRemoteCommunicationCenter newCommunicationCenter) throws RemoteException;

  /**
   * Used to ask for a location of an object.
   * 
   * @param objectID
   *          the object of interest.
   * @return hopefully a reference to an RemoteCommunicationCenter or if the
   *         local center does not know: NULL
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IRemoteCommunicationCenter getLocationOfObject(IObjectId objectID)
      throws RemoteException;

  /**
   * This method returns an object if it's local.
   * 
   * @param objectID
   *          ObjectID of the requested object.
   * @return NULL if the object doesn't exist or is NOT local. Otherwise, the
   *         object.
   * 
   * @throws RemoteException
   *           This exception will never be thrown but it's necessary that the
   *           method is able to it as this interface extends remote.
   */
  Object getObjectById(IObjectId objectID) throws RemoteException;

  // TODO (sb513): This method doesn't belong here as it must be called only
  // LOCALLY!
  /**
   * This method is used by the local communication center to register new
   * objects and by the MigrationController to inform this center that an object
   * is now located here.<br>
   * If there's an old entry with this objectID it'll be replaced by the new
   * object.
   * 
   * @param objectID
   *          ID of the object.
   * @param object
   *          The object itself.
   * @throws RemoteException
   *           An exception that will be never thrown.
   */
  void registerLocalObject(IObjectId objectID, Object object)
      throws RemoteException;

  /**
   * Destroys any references to the object id made by this communication center.
   * 
   * @param objectID
   *          The object id that shall be unregistered.
   * @throws RemoteException
   *           May be caused by RMI.
   */
  void unregisterObject(IObjectId objectID) throws RemoteException;

  /**
   * @return the object ID of every object that's stored in the communication
   *         center.
   * @throws RemoteException
   *           May be caused by RMI.
   */
  Set<IObjectId> getAllLocalObjectIds() throws RemoteException;

  /**
   * Gets the uID.
   * 
   * @return the uID
   * 
   * @throws RemoteException
   *           the remote exception
   */
  IUniqueID getUID() throws RemoteException; 
}
