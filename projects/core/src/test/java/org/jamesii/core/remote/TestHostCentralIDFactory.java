/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote;

import junit.framework.TestCase;

import org.jamesii.SimSystem;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.remote.hostcentral.BasicRemoteObjectId;
import org.jamesii.core.remote.hostcentral.HostCentralIDFactory;
import org.jamesii.core.remote.hostcentral.IObjectId;

/**
 * Tests the creation of remote IDs.
 * 
 * @author Simon Bartels
 * 
 */
public class TestHostCentralIDFactory extends TestCase {

  /**
   * Tests the method create(...).
   */
  public void testIDcreation() {
    HostCentralIDFactory hcIDfac = new HostCentralIDFactory();
    Object o = new Object();
    ParameterBlock p = new ParameterBlock(o, HostCentralIDFactory.PARAM_OBJECT);
    IObjectId id = hcIDfac.create(p, SimSystem.getRegistry().createContext());
    IObjectId id2 =
        new BasicRemoteObjectId(id.getStringRep(), o.getClass().getName());

    // System.out.println(id.getStringRep());
    // System.out.println(id2.getStringRep());

    // for the same object we should always receive the same id
    assertTrue(id.equals(id2));
    assertEquals(id.hashCode(), id2.hashCode());
  }

}
