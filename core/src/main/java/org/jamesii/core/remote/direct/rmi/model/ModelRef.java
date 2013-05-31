/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.model;

import java.rmi.RemoteException;

import org.jamesii.core.model.AccessRestriction;
import org.jamesii.core.model.IModel;
import org.jamesii.core.remote.direct.rmi.base.NamedEntityRef;

/**
 * The Class ModelRef.
 * 
 * @param <ML>
 */
public class ModelRef<ML extends IModel> extends NamedEntityRef<ML> implements
    org.jamesii.core.remote.direct.rmi.model.IModelRef {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 2264315080012969725L;

  /**
   * The Constructor.
   * 
   * @param model
   *          the model
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public ModelRef(ML model) throws RemoteException {
    super(model);
  }

  @Override
  public IModel remoteGetProxy() {
    return new ModelProxy<>(this);
  }

  @Override
  public void remoteSetAccessRestriction(AccessRestriction accessRestriction)
      throws RemoteException {
    getLocal().setAccessRestriction(accessRestriction);
  }

  @Override
  public void remoteCleanUp() throws RemoteException {
    if (getLocal() == null) {
      return;
    }
    getLocal().cleanUp();
    setLocal(null);
  }

  @Override
  public void remoteInit() throws RemoteException {
    if (getLocal() == null) {
      return;
    }
    getLocal().init();
  }

}
