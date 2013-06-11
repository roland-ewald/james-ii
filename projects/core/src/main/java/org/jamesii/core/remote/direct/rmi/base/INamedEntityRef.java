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
 * The Interface INamedEntityRef is the counterpart of the
 * {@link org.jamesii.core.base.INamedEntity} interface. It is a remotely
 * accessible interface (by using Java RMI) and it is implemented by the the
 * {@link NamedEntityRef} class.
 * 
 * @author Jan Himmelspach
 */
public interface INamedEntityRef extends IEntityRef {

  /**
   * Compare to.
   * 
   * @param o
   *          the object to be comapred to
   * 
   * @return the int
   * 
   * @throws RemoteException
   *           the remote exception
   * @see org.jamesii.core.base.INamedEntity#compareTo(INamedEntity)
   */
  int remoteCompareTo(INamedEntity o) throws RemoteException;

  /**
   * All named entities are carrying a name which shall be remote accessible.
   * 
   * @return the name of the entity on the remote side.
   * 
   * @throws RemoteException
   *           the remote exception
   * 
   * @see org.jamesii.core.base.INamedEntity#getName()
   */
  String remoteGetName() throws RemoteException;

  /**
   * Remotely sets a name of an entity.
   * 
   * @param name
   *          the name
   * 
   * @throws RemoteException
   *           the remote exception
   * @see org.jamesii.core.base.INamedEntity#setName(String)
   */
  void remoteSetName(String name) throws RemoteException;
}
