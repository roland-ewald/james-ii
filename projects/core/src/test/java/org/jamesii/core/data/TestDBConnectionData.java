/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data;

import org.jamesii.core.data.DBConnectionData;

import junit.framework.TestCase;

public class TestDBConnectionData extends TestCase {

  public void testConstructors() {
    DBConnectionData dbd = new DBConnectionData();
    assertNotNull(dbd.getDriver());
    assertNotNull(dbd.getPassword());
    assertNotNull(dbd.getURL());
    assertNotNull(dbd.getUser());

    dbd = new DBConnectionData("a", "b", "c", "d");
    assertNotNull(dbd.getDriver());
    assertNotNull(dbd.getPassword());
    assertNotNull(dbd.getURL());
    assertNotNull(dbd.getUser());

    assertEquals(dbd.getDriver(), "d");
    assertEquals(dbd.getPassword(), "c");
    assertEquals(dbd.getURL(), "a");
    assertEquals(dbd.getUser(), "b");

    dbd.setDriver("4");
    dbd.setPassword("3");
    dbd.setUser("2");
    dbd.setURL("1");

    assertEquals(dbd.getDriver(), "4");
    assertEquals(dbd.getPassword(), "3");
    assertEquals(dbd.getURL(), "1");
    assertEquals(dbd.getUser(), "2");

  }

}
