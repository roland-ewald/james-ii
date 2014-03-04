/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.Session;
import org.jamesii.perfdb.hibernate.TestHibernateEntity;

/**
 * Test for entities of the Selector database.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class TestSelectionDBEntity<X> extends TestHibernateEntity<X> {

  /** The location of the hibernate configuration file for the ASF database. */
  private static final String ASF_DATABASE_HIBERNATE_CONFIG =
      "org/jamesii/asf/database/hibernate/hibernate.cfg.xml";

  /**
   * Instantiates a new test selection database entity.
   */
  public TestSelectionDBEntity() {
    super();
  }

  /**
   * Instantiates a new test selection database entity.
   * 
   * @param clean
   *          the clean flag
   */
  public TestSelectionDBEntity(boolean clean) {
    super(clean);
  }

  /**
   * Instantiates a new test selection database entity.
   * 
   * @param session
   *          the session
   */
  public TestSelectionDBEntity(Session session) {
    super(session);
  }

  @Override
  protected String[] getConfigLocations() {
    List<String> configLocations =
        new ArrayList<>(Arrays.asList(super.getConfigLocations()));
    configLocations.add(ASF_DATABASE_HIBERNATE_CONFIG);
    return configLocations.toArray(new String[0]);
  }

}
