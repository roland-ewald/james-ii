/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii;

import java.io.File;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;

import junit.framework.TestCase;

/**
 * The Class TestSimSystem.
 */
public class TestSimSystem extends TestCase {

  /**
   * Test get registry.
   */
  public void testGetRegistry() {
    Registry r = SimSystem.getRegistry();
    assertTrue("Cannot get registry", r != null);
    Registry r2 = SimSystem.getRegistry();
    assertTrue(r == r2);
  }

  /**
   * Test set registry.
   */
  public void testSetRegistry() {

    Registry r = SimSystem.getRegistry();

    Registry r2 = null;

    SimSystem.setRegistry(r2);

    assertEquals(SimSystem.getRegistry(), r);

    r2 = new Registry();
    SimSystem.setRegistry(r2);

    assertEquals(SimSystem.getRegistry(), r);

  }

  /**
   * Test get uid.
   */
  public void testGetUid() {
    for (long c = SimSystem.getUId(); c < 1000; c++) {
      assertTrue(c + 1 == SimSystem.getUId());
    }
  }

  /**
   * Test get unique name.
   */
  public void testGetUniqueName() {
    // should never be null
    assertTrue(SimSystem.getUniqueName() != null);

    // should never be an emtpy string
    assertTrue(SimSystem.getUniqueName() != "");

    String[] names = new String[1000];
    for (int i = 0; i < 1000; i++) {
      names[i] = SimSystem.getUniqueName();
      // System.out.println(names[i]);
    }
    for (int i = 0; i < 1000; i++) {
      assertTrue(names[i] != null);
      assertTrue(names[i] != "");
      for (int j = i + 1; j < 1000; j++) {
        assertTrue("Name " + names[i] + " (" + i + ")"
            + " is identical to name " + names[j] + "(" + j + ")",
            names[i].compareTo(names[j]) != 0);
      }
    }
  }

  /**
   * Test get rng generator.
   */
  public void testGetRngGenerator() {
    assertTrue(SimSystem.getRNGGenerator() != null);
  }

  /**
   * Test get working directory.
   */
  public void testGetWorkingDirectory() {
    testDirectory(SimSystem.getWorkingDirectory());
  }

  /**
   * Test get config directory.
   */
  public void testGetConfigDirectory() {
    testDirectory(SimSystem.getConfigDirectory());
    // System.getenv().put("JAMES_CONFIG_DIR", "testname");
    // assertTrue(SimSystem.getTempDirectory().compareTo("testname") == 0);
  }

  /**
   * Test get temp directory.
   */
  public void testGetTempDirectory() {
    testDirectory(SimSystem.getTempDirectory());
    // System.getenv().put("JAMES_TEMPPATH", "testname");
    // assertTrue(SimSystem.getTempDirectory().compareTo("testname") == 0);
  }

  /**
   * Test directory. Is called by the get directory test methods to check some
   * basic constraints on the directories.
   * 
   * @param dir
   *          the dir
   */
  private void testDirectory(String dir) {
    assertTrue(dir != null);
    File cDir = new File(dir);
    assertTrue(cDir.exists());
    assertTrue(cDir.isDirectory());
  }

}
