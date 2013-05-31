/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.model;

import org.jamesii.core.model.AccessRestriction;
import org.jamesii.core.model.IModel;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;
import org.jamesii.core.remote.hostcentral.base.NamedEntityProxy;

/**
 * The Class ModelProxy.
 * 
 * Represents a remote model, typically being located at a different machine,
 * but usually at least in a different JVM. This means that not all methods of
 * {@link org.jamesii.core.model.IModel} are useful, and so some might throw a
 * {@link org.jamesii.core.remote.RemoteCallForbiddenException}. Most likely
 * this will be the case if the return value does not represent the value it is
 * ought to be on a remote machine.
 * 
 * @author Jan Himmelspach
 */
public class ModelProxy extends NamedEntityProxy implements IModel {

  /**
   * Instantiates a new model proxy.
   * 
   * @param remoteMethodCaller
   *          the remote method caller
   * @param objectId
   *          the object id
   */
  public ModelProxy(IRemoteMethodCaller remoteMethodCaller, IObjectId objectId) {
    super(remoteMethodCaller, objectId);
  }

  @Override
  public void setAccessRestriction(AccessRestriction accessRestriction) {
    executeMethod("setAccessRestriction", new Object[] { accessRestriction });
  }

  @Override
  public void init() {
    executeMethod("init");
  }

  @Override
  public void cleanUp() {
    executeMethod("cleanUp");
  }
}
