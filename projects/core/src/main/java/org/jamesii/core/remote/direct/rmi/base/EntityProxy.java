/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.base;

import java.io.Serializable;
import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.IContext;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.remote.RemoteCallForbiddenException;
import org.jamesii.core.util.ReflectionException;
import org.jamesii.core.util.exceptions.OperationNotSupportedException;

/**
 * The Class EntityProxy is a proxy for a remote entity, it can be used at the
 * local site as "local" replacement {@link org.jamesii.core.base.IEntity} for
 * the remote object {@link IEntityRef}. Thus any handling of remote
 * communication is wrapped in here.
 * 
 * <p>
 * All classes in the .rmi packages are created for realizing a parallel and
 * distributed simulation. Thereby XXXProxy classes implement the same interface
 * as the "real" models do: thus they can be used as if they were real models.
 * These proxies have a reference to a remote object interface (IXXXRef
 * interface). The class implementing this interface (XXXRef class) has a
 * reference to a local XXX instance. Thus all calls of one of the proxie's
 * methods are redirected to the remote object. <br/>
 * In comparison to the old solution this solution has several advantages: - RMI
 * handling is no longer needed within the model classes - simulators now always
 * work on local objects - objects can be more easily migrated - RMI can be
 * replaced <br/>
 * The only draw back is that now each method call is wrapped by an extra try
 * catch block. Thus iterating over a large number of entities and calling
 * methods is slightly more expensive as before (where one try ... catch block
 * was sufficient for all calls).
 * </p>
 * 
 * This class provides a set of convenience methods for making use of this
 * communication schema:
 * <ul>
 * <li>{@link #handleRemoteException(RemoteException)}, can be used to present a
 * uniform behavior if a RemoteException occurs</li>
 * <li>{@link #executeMethod(String, Object[])}, can be used to call a method on
 * the remote site by just passing its name and parameters</li>
 * <li>{@link #throwDontCallRemotelyException(String)}, can be used to throw a
 * special exception telling the user of the method that this method cannot be
 * used on a proxy.</li>
 * </ul>
 * 
 * @param <R>
 *          the type of the entity ref
 * 
 * @author Jan Himmelspach
 */
public class EntityProxy<R extends IEntityRef> implements
    org.jamesii.core.base.IEntity, Serializable {

  /** Serialisation ID. */
  static final long serialVersionUID = 5588335686516401149L;

  /** The ref. */
  private R ref;

  /** The number of trials for remote communication. */
  private int trials = 3;

  /** The waiting time between two trials (in milli seconds). */
  private int trialsWait = 0;

  /**
   * Creates a new proxy which can be used as "local" object. It internally has
   * a reference (which is passed in this constructor) to the remote entity to
   * be used
   * 
   * @param ref
   *          the remote entity for which this shall be the proxy
   */
  public EntityProxy(R ref) {
    super();
    this.ref = ref;
  }

  @Override
  public void changed() {
    throw new OperationNotSupportedException(
        "You should not be able to change a remote entity. You therefore should also avoid calling changed().");
  }

  /**
   * Execute the method identified by methodName on the remote site object. <br>
   * The request to execute the method with the given name is forwarded to the
   * {@link #ref} object and executed there by using
   * {@link #ref.executeMethod(String,Object[])}. This method will try to call
   * the specified method on its local instance. <br>
   * This method will try to call the method up to {@link #trials} times, and
   * will try to wait between two subsequent calls for {@link #trialsWait}
   * milliseconds. Thus there is a chance to get around temporary connection
   * problems. If the method execution finally fails
   * 
   * @param methodName
   *          the name of the method to be executed
   * @param parameters
   *          the parameters to be used for execution
   * 
   * @return the resultof the remote method call.
   */
  protected synchronized Object executeMethod(String methodName,
      Object[] parameters) {
    for (int i = 0; i < trials; i++) {
      try {
        return ref.remoteExecuteMethod(methodName, parameters);
      } catch (RemoteException e) {
        SimSystem.report(e);
        try {
          Thread.sleep(trialsWait);
        } catch (InterruptedException e1) {
          // forget it
        }
        // Cancel if we are not allowed to execute the method on the remote
        // object, because in this case we do not need to retry
        if (e.getCause() instanceof ReflectionException) {
          break;
        }
      }
    }
    throw new RuntimeException("Remote execution of method (" + methodName
        + ") on remote object " + ref
        + " failed finally. See log for further details.");
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

  @Override
  public String getCompleteInfoString() {
    try {
      return ref.remoteGetCompleteInfoString();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    return "";
  }

  @Override
  public IMediator getMediator() {
    return throwDontCallRemotelyException("getMediator");
  }

  @Override
  public long getSimpleId() {
    try {
      return ref.remoteGetUid();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    return -1;
  }

  /**
   * Internal method for handling remote exceptions.
   * 
   * @param re
   *          the re
   */
  protected void handleRemoteException(RemoteException re) {
    SimSystem.report(re);
  }

  @Override
  public void registerObserver(IObserver observer) {
    try {
      ref.remoteRegisterObserver(observer);
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }

  @Override
  public void setMediator(IMediator mediator) {
    throwDontCallRemotelyException("setMediator");
  }

  @Override
  public void unregisterObserver(IObserver observer) {
    try {
      ref.remoteUnregisterObserver(observer);
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }

  @Override
  public void unregisterObservers() {
    executeMethod("unregisterObservers", null);
  }

  /**
   * Gets the ref.
   * 
   * @return the ref
   */
  protected final R getRef() {
    return ref;
  }

  /**
   * Sets the ref.
   * 
   * @param newRef
   *          the ref
   */
  protected final void setRef(R newRef) {
    ref = newRef;
  }

  @Override
  public void changed(Object hint) {
    throw new OperationNotSupportedException(
        "You should not be able to change a remote entity. You therefore should also avoid calling changed(hint).");
  }

}
