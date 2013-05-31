/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.base;

import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.util.Reflect;

/**
 * The Class EntityRef is a remote accessible (through Java RMI) entity.
 * 
 * <br/>
 * This class "hides" an instance of an entity which is made remote accessible
 * by this class. This allows to have different mechanisms for remote
 * communication, and removes the burden to handle remote communication in the
 * local case, and, even more important, in the implementation of all classes.
 * 
 * @author Jan Himmelspach
 * @param <L>
 *          the type of the entity
 */
public class EntityRef<L extends IEntity> extends UnicastRemoteObject implements
    IEntityRef {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -3984406557630108329L;

  /** The local instance we are the gate to the outer world for. */
  private transient L local;

  /**
   * Instantiates a new entity ref.
   * 
   * @param local
   *          the local
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public EntityRef(L local) throws RemoteException {
    super();
    this.local = local;
  }

  /**
   * Execute the passed method (identified by its name) on the local instance
   * this ref instance provides access to. <br>
   * The {@link #executeMethod(Method, Object[])} method can be used instead of
   * this if the method object is already known - this reduces the overhead
   * induced by this method on finding the Method to be executed. <br>
   * The {@link #executeMethod(Method, Object[])} and the
   * {@link #executeMethod(String, Object[])} can both be used to conveniently
   * execute methods on the local object.
   * 
   * @param methodName
   *          the method name of the method to be executed from the local object
   * @param parameters
   *          the parameters to be used for the method call
   * 
   * @return the result value of the method call, maybe null
   */
  public Object executeMethod(String methodName, Object[] parameters) {
    return Reflect.executeMethod(local, methodName, parameters);
  }

  /**
   * Execute the passed method on the local instance this ref instance provides
   * access to.<br>
   * This method can be used instead of {@link #executeMethod(String, Object[])}
   * if the method object is already known - this reduces the overhead induced
   * by finding the Method to be executed.<br>
   * The {@link #executeMethod(Method, Object[])} and the
   * {@link #executeMethod(String, Object[])} can both be used to conveniently
   * execute methods on the local object.
   * 
   * @param method
   *          the method to be executed to be executed from the local object
   * @param parameters
   *          the parameters to be used for the method call
   * 
   * @return the result value of the method call, maybe null
   */
  public Object executeMethod(Method method, Object[] parameters) {
    return Reflect.executeMethod(local, method, parameters);
  }

  @Override
  public Object remoteExecuteMethod(String methodName, Object[] parameters)
      throws RemoteException {
    return executeMethod(methodName, parameters);
  }

  @Override
  public String remoteGetClassName() throws RemoteException {
    return this.getClass().getName() + "(encapsulated local: "
        + local.getClass().getName() + ")";
  }

  @Override
  public String remoteGetCompleteInfoString() throws RemoteException {
    return local.getCompleteInfoString();
  }

  @Override
  public long remoteGetUid() throws RemoteException {
    return local.getSimpleId();
  }

  @Override
  public void remoteRegisterObserver(IObserver observer) throws RemoteException {
    local.registerObserver(observer);
  }

  @Override
  public void remoteUnregisterObserver(IObserver observer)
      throws RemoteException {
    local.unregisterObserver(observer);
  }

  /**
   * Gets the local.
   * 
   * @return the local
   */
  protected final L getLocal() {
    return local;
  }

  /**
   * Sets the local.
   * 
   * @param newLocal
   */
  protected final void setLocal(L newLocal) {
    local = newLocal;
  }

}
