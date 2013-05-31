/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.base;

import java.rmi.RemoteException;

import org.jamesii.core.base.INamedEntity;

/**
 * The Class NamedEntityRef. Remote accessible object which holds a reference to
 * a local named entity.
 * 
 * <br/>
 * This class "hides" an instance of an entity which is made remote accessible
 * by this class. This allows to have different mechanisms for remote
 * communication, and removes the burden to handle remote communication in the
 * local case, and, even more important, in the implementation of all classes.
 * 
 * @param <NL>
 *          the type of the named entity
 * 
 * @author Jan Himmelspach
 */
public class NamedEntityRef<NL extends INamedEntity> extends EntityRef<NL>
    implements INamedEntityRef {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -8387805667810083953L;

  /**
   * The Constructor.
   * 
   * @param local
   *          the local entity to be made accessible
   * 
   * @throws RemoteException
   *           the remote exception
   */
  public NamedEntityRef(NL local) throws RemoteException {
    super(local);
  }

  @Override
  public int remoteCompareTo(INamedEntity o) throws RemoteException {
    return getLocal().compareTo(o);
  }

  @Override
  public String remoteGetName() throws RemoteException {
    // System.out.println("getting a name from the remote site");
    return getLocal().getName();
  }

  @Override
  public void remoteSetName(String name) throws RemoteException {
    getLocal().setName(name);
  }

}
