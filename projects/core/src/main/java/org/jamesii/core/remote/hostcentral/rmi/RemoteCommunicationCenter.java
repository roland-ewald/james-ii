/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.rmi;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.util.Reflect;
import org.jamesii.core.util.id.IUniqueID;
import org.jamesii.core.util.id.UniqueIDGenerator;
import org.jamesii.core.util.info.JavaInfo;

/**
 * The Class RemoteCommunicationCenter.
 * 
 * Simple implementation of a remote communication center. Has to maintain a
 * full mapping of all objects of interest (may include all existing objects). A
 * range of ids based implementation could save a lot of memory, but would be
 * more difficult to maintain.<br>
 * 
 * 
 * 
 * @author Jan Himmelspach
 * @author Simon Bartels
 */
public class RemoteCommunicationCenter extends UnicastRemoteObject implements
    IRemoteCommunicationCenter {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 681877407740771497L;

  private IUniqueID uid = UniqueIDGenerator.createUniqueID();

  /**
   * List of all remote communication centers interested in this.
   */
  private transient Set<IRemoteCommunicationCenter> remoteLocationObjects =
      new HashSet<>();

  /**
   * The locations of the remote objects - through which center we can
   * communicate with those?.
   */
  private transient Map<IObjectId, IRemoteCommunicationCenter> remoteLocations =
      new HashMap<>();

  /** The local locations. */
  private transient Map<IObjectId, Object> localLocations = new HashMap<>();

  /** number of trials to make the remote call before giving up */
  private transient int trials = 3;

  private Map<Class<?>, Map<String, Method>> cache = new HashMap<>();
  
  /**
   * Instantiates a new remote communication center.
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public RemoteCommunicationCenter() throws RemoteException {
    super();
  }

  /**
   * Return the unique identifier created for this remote communication center.
   * 
   * @return
   */
  public IUniqueID getUid() {
    return uid;
  }

  private Method getMethod(Object local, String methodName,
      Object[] parameters) {
    // FIXME (nf028): CACHE DOES NOT WORK FOR OVERLOADED METHODS!!!!

    Map<String, Method> methods = cache.get(local.getClass());

    if (methods == null) {
      methods = new HashMap<>();
      cache.put(local.getClass(), methods);

    }

    Method m = methods.get(methodName);
    if (m == null) {
      try {
        m =
            Reflect.getMethod(local.getClass(), methodName,
                Reflect.getParameterTypes(parameters));
        methods.put(methodName, m);
      } catch (SecurityException e) {
        SimSystem.report(
            Level.SEVERE,
            "The method " + methodName + " with parameter types "
                + disp(Reflect.getParameterTypes(parameters))
                + " causes a security problem.", e);
      } catch (NoSuchMethodException e) {
        SimSystem.report(Level.SEVERE,
            "For the method " + methodName + " with parameter types "
                + disp(Reflect.getParameterTypes(parameters))
                + " no method has been found.", e);

      }
    }
    
    return m;
  }

  /**
   * Similar to generic definition.
   * 
   * @param array
   *          the array
   * @return string representation
   */
  public static String disp(Class<?>[] array) {
    if (array == null) {
      return "null";
    }
    StringBuilder s = new StringBuilder("[");
    for (int i = 0; i < array.length; i++) {
      s.append(" " + array[i].getName());
    }
    s.append(" ]");
    return s.toString();
  }

  @Override
  public Object executeMethodIn(String methodName, Object[] parameters,
      IObjectId objectID) throws RemoteException {
    Object local = localLocations.get(objectID);
    if (local == null) {
      throw new RemoteCommunicationCenterException(
          "RemoteCommunicationCenter: Invalid model location. Model is not located on host "
              + new JavaInfo().getHostName()
              + "\n"
              + "Tried to call "
              + methodName
              + " on object "
              + objectID.getClassNameFromObject()
              + "with ID " + objectID.getStringRep());
    }

    Method m = getMethod(local, methodName, parameters);
    if (m != null) {
      try {
 //       System.out.println("by cache");
        return m.invoke(local, parameters);
      } catch (IllegalAccessException | IllegalArgumentException
          | InvocationTargetException e) {
        SimSystem.report(e);
      }
    }
    System.out.println("not by cache");
    // execute the method on the local object
    return Reflect.executeMethod(local, methodName, parameters);
  }

  /**
   * Execute method by calling the execute method of the remote communication
   * center.
   * 
   * @param methodName
   *          the name of the method to be executed
   * @param parameters
   *          the parameters to be passed to the method to be executed
   * @param objectID
   *          the object id of the object where the method shall be executed on
   * 
   * @return the result of the message call
   * 
   */
  public Object executeMethodOut(String methodName, Object[] parameters,
      IObjectId objectID) {

    IRemoteCommunicationCenter host =
        localLocations.containsKey(objectID) ? this : remoteLocations
            .get(objectID);

    if (host == null) {
      throw new RemoteCommunicationCenterException("Object with ID '"
          + objectID + " (class: " + objectID.getClassNameFromObject()
          + ") is unknown!");
    }

    for (int i = 0; i < trials; i++) {
      try {
        // execute the method on the local object
        // call the executeMethodIn method of the remote communication center of
        // the object identified by objectId
        // TODO: when host == this the call should be done locally, SUSPICIION:
        // it's NOT
        return host.executeMethodIn(methodName, parameters, objectID);
      } catch (RemoteException re) {
        SimSystem.report(re);
      }
    }

    throw new RemoteCommunicationCenterException(
        "Failed on calling the method "
            + methodName
            + " on the remote object "
            + objectID
            + ". Please see the log for further details on the type of problem occured.");
  }

  @Override
  public void updateObjectLocations(Set<IObjectId> objects,
      IRemoteCommunicationCenter newCommunicationCenter) throws RemoteException {

    synchronized (this) {
      if (newCommunicationCenter.getUID().compareTo(getUID()) != 0) {
        for (IObjectId object : objects) {
          // make sure that it is no longer in the local locations

          localLocations.remove(object);
          remoteLocations.put(object, newCommunicationCenter);
        }
      } else {
        for (IObjectId object : objects) {
          // make sure that it is no longer in the remote locations
          remoteLocations.remove(object);

          // If this RCC is the new location then the objects should be already
          // in the map.
          // localLocations.put(object.getKey(), object.getValue());
        }
      }

    }
    
//    System.out.println("We are in object: "+this);
//    
//    System.out.println("Add "+ objects.toString() + " is local on "+newCommunicationCenter);
//    
//    System.out.println("Now:");
//    int c = 0;
//    for (Entry<IObjectId, IRemoteCommunicationCenter> entry : remoteLocations.entrySet()) {
//      c++;
//      System.out.println(c+":\t(\t"+entry.getKey());  
//      System.out.println("\t\t"+entry.getValue() + " )");
//    }
//    
//    

  }

  @Override
  public IRemoteCommunicationCenter getLocationOfObject(IObjectId objectID)
      throws RemoteException {
    // the object is here
    if (null != localLocations.get(objectID)) {
      return this;
    }

    // if it's not here, then we return a result if we know where it is or null
    return remoteLocations.get(objectID);
  }

  @Override
  public Object getObjectById(IObjectId objectID) throws RemoteException {
    return localLocations.get(objectID);
  }

  @Override
  // TODO: Threadsafe?!?!
  // TODO: How should already registered IDs be treated?!
  public void registerLocalObject(IObjectId objectID, Object object)
      throws RemoteException {
    synchronized (this) {
      localLocations.remove(objectID);
      remoteLocations.remove(objectID);
      localLocations.put(objectID, object);
    }

    Set<IObjectId> s = new HashSet<>();
    s.add(objectID);
    for (IRemoteCommunicationCenter rc : remoteLocationObjects) {
      rc.updateObjectLocations(s, this);
    }
  }

  @Override
  public void unregisterObject(IObjectId objectID) throws RemoteException {
    localLocations.remove(objectID);
    remoteLocations.remove(objectID);
  }

  @Override
  public Set<IObjectId> getAllLocalObjectIds() throws RemoteException {
    HashSet<IObjectId> s = new HashSet<>();
    s.addAll(localLocations.keySet());
    return s;
  }

  @Override
  public void introduceNewCommunicationCenter(IRemoteCommunicationCenter rc)
      throws RemoteException {
    remoteLocationObjects.add(rc);
  }

  @Override
  public IUniqueID getUID() throws RemoteException {
    return uid;
  }

  public Map<IObjectId, String> getKnownObjects() {
    //TODO not needed
    Map<IObjectId, String> ret = new HashMap<>();
    for(IObjectId id : localLocations.keySet()){
      ret.put(id, "local");
    }
    
    for(IObjectId id : remoteLocations.keySet()){
      ret.put(id, "remote");
    }
    return ret;
  }
  

}
