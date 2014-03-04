/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import org.hibernate.Session;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.hibernate.Machine;

/**
 * Test for {@link Machine}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestMachine extends TestHibernateEntity<Machine> {

  /** The test description. */
  static final String TEST_DESCRIPTION = "This is a test machine";

  /** The test benchmark value. */
  static final double TEST_BENCHMARK_VAL = 123.45;

  /** The test address. */
  static String testAddress = null;

  /** The test address2. */
  static String testAddress2 = null;

  /**
   * Instantiates a new test machine.
   * 
   * @throws Exception
   *           the exception
   */
  public TestMachine() throws Exception {
    super(true);
    testAddress = Strings.getMACAddressString(new byte[] { 1, 2, 3, 4 });
    testAddress2 = Strings.getMACAddressString(new byte[] { 5, 6, 7, 8 });
  }

  /**
   * Instantiates a new test machine.
   * 
   * @param s
   *          the session
   */
  public TestMachine(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(Machine entity) {
    entity.setName("Changed name");
    entity.setDescription("Changed desc");
    entity.setJavaSciMark(67.89);
    entity.setMacAddress(testAddress2);
  }

  @Override
  protected void checkEquality(Machine entity) {
    assertEquals(TEST_DESCRIPTION, entity.getDescription());
    assertEquals(TEST_BENCHMARK_VAL, entity.getJavaSciMark());
    assertEquals(testAddress, entity.getMacAddress());
  }

  @Override
  protected Machine getEntity(String name) {
    Machine m = new Machine();
    m.setName(name);
    m.setDescription(TEST_DESCRIPTION);
    m.setMacAddress(testAddress);
    m.setJavaSciMark(TEST_BENCHMARK_VAL);
    return m;
  }

}
