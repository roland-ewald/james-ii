/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.direct.rmi.base;

import java.rmi.RemoteException;

import org.jamesii.SimSystem;
import org.jamesii.core.base.INamedEntity;

/**
 * The Class NamedEntityProxy. The Class NamedEntityProxy is a proxy for a
 * remote entity, it can be used at the local site as "local"
 * {@link org.jamesii.core.base.INamedEntity} replacement for the remote object
 * {@link INamedEntityRef}. Thus any handling of remote communication is wrapped
 * in here.
 * 
 * @author Jan Himmelspach
 * @param <NR>
 */
public class NamedEntityProxy<NR extends INamedEntityRef> extends
    EntityProxy<NR> implements org.jamesii.core.base.INamedEntity {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -501208524161163035L;

  /**
   * The Constructor.
   * 
   * @param ref
   *          the ref
   */
  public NamedEntityProxy(NR ref) {
    super(ref);
  }

  /**
   * Compare to.
   * 
   * @param o
   *          the o
   * 
   * @return the int
   */
  @Override
  public int compareTo(INamedEntity o) {
    try {
      getRef().remoteCompareTo(o);
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
    return 0;
  }

  /**
   * All named entities are carrying a name which shall be remote accessible
   * This method is implement by the.
   * 
   * @return the name
   * 
   *         {@link org.jamesii.core.base.NamedEntity} class.
   */
  @Override
  public String getName() {
    try {
      return getRef().remoteGetName();
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
    return "error";
  }

  /**
   * Sets the name.
   * 
   * @param name
   *          the name
   * 
   * @see org.jamesii.core.base.INamedEntity#setName(java.lang.String)
   */
  @Override
  public void setName(String name) {
    try {
      getRef().remoteSetName(name);
    } catch (RemoteException re) {
      SimSystem.report(re);
    }
  }

}
