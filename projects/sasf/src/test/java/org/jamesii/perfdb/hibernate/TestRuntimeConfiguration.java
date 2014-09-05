/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.util.Calendar;

import org.hibernate.Session;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Test for {@link RuntimeConfiguration}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestRuntimeConfiguration extends
    TestHibernateEntity<RuntimeConfiguration> {

  /**
   * Instantiates a new test runtime configuration.
   */
  public TestRuntimeConfiguration() {
    super(true);
  }

  /**
   * Instantiates a new test runtime configuration.
   * 
   * @param s
   *          the session
   */
  public TestRuntimeConfiguration(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(RuntimeConfiguration entity) {
  }

  @Override
  protected void checkEquality(RuntimeConfiguration entity) {
    assertNotNull(entity.getSelectionTree());
    assertEquals(1250346074, entity.getSelectionTreeHash());
    assertEquals(1, entity.getVersion());
  }

  @Override
  protected RuntimeConfiguration getEntity(String name) {
    return new RuntimeConfiguration(new SelectionTree(null), Calendar
        .getInstance().getTime(), 1);
  }

}
