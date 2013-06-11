/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.model;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.model.AccessRestriction;
import org.jamesii.core.model.IModel;
import org.jamesii.core.remote.direct.rmi.base.NamedEntityProxy;

/**
 * The Class ModelProxy.
 * 
 * @param <MR>
 */
public class ModelProxy<MR extends IModelRef> extends NamedEntityProxy<MR>
    implements IModel {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 666918287743200386L;

  /**
   * The Constructor.
   * 
   * @param ref
   *          the ref
   */
  public ModelProxy(MR ref) {
    super(ref);
  }

  @Override
  public void setAccessRestriction(AccessRestriction accessRestriction) {
    try {
      getRef().remoteSetAccessRestriction(accessRestriction);
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
  }

  @Override
  public void cleanUp() {
    if (getRef() == null) {
      return;
    }
    try {
      getRef().remoteCleanUp();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
    setRef(null);
  }

  @Override
  public void init() {
    if (getRef() == null) {
      return;
    }
    try {
      getRef().remoteInit();
    } catch (RemoteException re) {
      handleRemoteException(re);
    }
  }
}
