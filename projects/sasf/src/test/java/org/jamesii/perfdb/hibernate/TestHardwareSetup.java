/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.util.HashSet;

import org.hibernate.Session;
import org.jamesii.perfdb.entities.IMachine;

/**
 * Tests {@link HardwareSetup}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestHardwareSetup extends TestHibernateEntity<HardwareSetup> {

  /** The network speed used for testing. */
  static final long TEST_NW_SPEED = 1024;

  /** The network topoloy used for testing. */
  static final String TEST_NW_TOPOLOGY = "Butterfly";

  /**
   * Instantiates a new test hardware setup.
   */
  public TestHardwareSetup() {
    super(true);
  }

  /**
   * Instantiates a new test hardware setup.
   * 
   * @param s
   *          the session
   */
  public TestHardwareSetup(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(HardwareSetup entity) {
    entity.setNetworkSpeed(23);
    entity.setNetworkTopology("Iron Butterfly");
  }

  @Override
  protected void checkEquality(HardwareSetup entity) {
    assertEquals(TEST_NW_SPEED, entity.getNetworkSpeed());
    assertEquals(TEST_NW_TOPOLOGY, entity.getNetworkTopology());
  }

  @Override
  protected HardwareSetup getEntity(String name) throws Exception {

    HardwareSetup hws = new HardwareSetup();
    hws.setName(name);
    hws.setNetworkSpeed(TEST_NW_SPEED);
    hws.setNetworkTopology(TEST_NW_TOPOLOGY);

    TestMachine tm = new TestMachine(session);
    HashSet<IMachine> machineSet = new HashSet<>();
    for (int i = 0; i < 5; i++) {
      machineSet.add(tm.createEntity("Machine " + i + " for setup " + name));
    }
    hws.setMachines(machineSet);

    return hws;
  }

}
