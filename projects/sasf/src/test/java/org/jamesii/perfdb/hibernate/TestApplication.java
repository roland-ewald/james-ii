/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.util.Calendar;
import java.util.Date;

import org.hibernate.Session;
import org.jamesii.perfdb.entities.IResultDataProvider;

/**
 * Test for hibernate implementation of applications.
 * 
 * @author Roland Ewald
 * 
 */
public class TestApplication extends TestHibernateEntity<Application> {

  /** The hardwware setup. */
  HardwareSetup hwSetup;

  /** The problem instance. */
  ProblemInstance probInst;

  /** The runtime configuration. */
  RuntimeConfiguration rtConfig;

  /** The execucation date. */
  Date execDate;

  /**
   * Instantiates a new test application.
   */
  public TestApplication() {
    super(true);
  }

  /**
   * Instantiates a new test application.
   * 
   * @param s
   *          the session
   */
  public TestApplication(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(Application entity) {
    entity.setDataProvider(null);
  }

  @Override
  protected void checkEquality(Application entity) {
    assertEquals(hwSetup.getNetworkSpeed(), entity.getSetup().getNetworkSpeed());
    assertEquals(hwSetup.getNetworkTopology(), entity.getSetup()
        .getNetworkTopology());
    assertEquals(hwSetup.getDescription(), entity.getSetup().getDescription());
    assertEquals(probInst.getRandomSeed(), entity.getProblemInstance()
        .getRandomSeed());
    assertEquals(rtConfig.getSelectionTreeHash(), entity
        .getRuntimeConfiguration().getSelectionTreeHash());
    // Depending on storage delay etc., there might be a 60 s cut-off (i.e., ms
    // precision is lost by persistence (so this always happens!), and s
    // precision is lost by delayed
    // storage in this use case)
    assertTrue(Math.abs(entity.getExecutionDate().getTime()
        - execDate.getTime()) < 60000);
    assertNotNull(entity.getDataProvider());
  }

  @Override
  protected Application getEntity(String name) throws Exception {

    TestHardwareSetup hws = new TestHardwareSetup(session);
    hwSetup = hws.createEntity("hwSetup for app '" + name + "'");

    TestProblemInstance tpi = new TestProblemInstance(session);
    probInst = tpi.createEntity("problem inst for app '" + name + "'");

    TestRuntimeConfiguration trc = new TestRuntimeConfiguration(session);
    rtConfig = trc.createEntity("rt-config for app '" + name + "'");

    execDate = Calendar.getInstance().getTime();
    Application app =
        new Application(probInst, rtConfig, hwSetup,
            new MyResultDataProvider(), execDate);

    return app;
  }

}

/**
 * A mock for the result data provide.
 */
class MyResultDataProvider implements IResultDataProvider<String> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 821118759519372371L;

  @Override
  public String getData(Object... parameters) {
    return "data";
  }

  @Override
  public Class<String> getDataType() {
    return String.class;
  }

}