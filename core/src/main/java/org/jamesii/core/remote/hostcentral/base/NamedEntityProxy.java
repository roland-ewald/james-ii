/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.base;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;

/**
 * The Class NamedEntityProxy.
 * 
 * Represents a remote named entity, typically being located at a different
 * machine, but usually at least in a different JVM. This means that not all
 * methods of {@link org.jamesii.core.base.INamedEntity} are useful, and so some
 * might throw a {@link org.jamesii.core.remote.RemoteCallForbiddenException}.
 * Most likely this will be the case if the return value does not represent the
 * value it is ought to be on a remote machine.
 * 
 * @author Jan Himmelspach
 */
public class NamedEntityProxy extends EntityProxy implements INamedEntity {

  /**
   * Instantiates a new named entity proxy.
   * 
   * @param remoteMethodCaller
   *          the remote method caller
   * @param objectId
   *          the object id of this object
   */
  public NamedEntityProxy(IRemoteMethodCaller remoteMethodCaller,
      IObjectId objectId) {
    super(remoteMethodCaller, objectId);
  }

  @Override
  public int compareTo(INamedEntity o) {
    return (Integer) executeMethod("compareTo", new Object[] { o });
  }

  @Override
  public String getName() {
    return (String) executeMethod("getName", null);
  }

  @Override
  public void setName(String name) {
    executeMethod("setName", new Object[] { name });
  }

  @Override
  public String toString() {
    return (String) executeMethod("toString", null);
  }

}
