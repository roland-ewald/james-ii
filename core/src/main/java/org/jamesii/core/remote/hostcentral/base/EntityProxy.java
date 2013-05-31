/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.base;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.factories.IContext;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.remote.RemoteCallForbiddenException;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;

/**
 * The Class IEntityProxy.
 * 
 * Represents a remote entity, typically being located at a different machine,
 * but usually at least in a different JVM. This means that not all methods of
 * {@link org.jamesii.core.base.IEntity} are useful, and so some might throw a
 * {@link org.jamesii.core.remote.RemoteCallForbiddenException}. Most likely
 * this will be the case if the return value does not represent the value it is
 * ought to be on a remote machine.
 * 
 * @author Jan Himmelspach
 */
public class EntityProxy implements IEntity {

  /** The remote method caller. */
  private IRemoteMethodCaller remoteMethodCaller;

  /** The object id. */
  private IObjectId objectId;

  /**
   * Entity proxy.
   * 
   * @param remoteMethodCaller
   *          the remote method caller
   * @param objId
   *          the object id of this object
   */
  public EntityProxy(IRemoteMethodCaller remoteMethodCaller, IObjectId objId) {
    super();
    this.remoteMethodCaller = remoteMethodCaller;
    this.objectId = objId;
  }

  /**
   * Throw don't call remotely exception. This convenience method can be used to
   * fill method stubs in descendant classes if the methods cannot be used from
   * remotely.
   * 
   * This method will under no circumstances return anything. But by defining a
   * return type it can be used as a function as well.
   * 
   * @param <O>
   *          type of the object to be returned
   * 
   * @param methodName
   *          the method name
   * 
   * @return never anything, this method will throw an exception.
   */
  public static <O> O throwDontCallRemotelyException(String methodName) {
    throw new RemoteCallForbiddenException("It is not allowed to call "
        + methodName + " on the remote object.");
  }

  /**
   * Execute method. Convenience wrapper. Wraps access to the remoteMethodCaller
   * object and the usage of the objectId.
   * 
   * @param methodName
   *          the method name to be executed
   * @param parameters
   *          the parameters to be used on calling the method
   * 
   * @return the result of the method call, can be null
   */
  protected Object executeMethod(String methodName, Object[] parameters) {
    return remoteMethodCaller.executeMethod(methodName, parameters, objectId);
  }

  /**
   * Execute method. Convenience wrapper. Wraps access to the remoteMethodCaller
   * object and the usage of the objectId. Only usable for methods without
   * parameters.
   * 
   * @param methodName
   *          the method name to be executed
   * 
   * @return the result of the method call, can be null
   */
  protected Object executeMethod(String methodName) {
    return executeMethod(methodName, null);
  }

  @Override
  public String getCompleteInfoString() {
    return (String) executeMethod("getCompleteInfoString", null);
  }

  @Override
  public long getSimpleId() {
    return (Long) executeMethod("getUid", null);
  }

  @Override
  public void changed() {
    remoteMethodCaller.executeMethod("changed", null, objectId);
  }

  @Override
  public void changed(Object hint) {
    remoteMethodCaller
        .executeMethod("changed", new Object[] { hint }, objectId);
  }

  @Override
  public IMediator getMediator() {
    return throwDontCallRemotelyException("getMediator");
  }

  @Override
  public void registerObserver(IObserver observer) {
    throwDontCallRemotelyException("registerObserver");
  }

  @Override
  public void setMediator(IMediator mediator) {
    throwDontCallRemotelyException("getMediator");
  }

  @Override
  public void unregisterObserver(IObserver observer) {
    throwDontCallRemotelyException("unregisterObserver");
  }

  @Override
  public void unregisterObservers() {
    throwDontCallRemotelyException("unregisterObservers");
  }

}
