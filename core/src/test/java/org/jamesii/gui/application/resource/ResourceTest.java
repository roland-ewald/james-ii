/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.application.resource;

import org.jamesii.gui.application.resource.Resource;

import junit.framework.TestCase;

/**
 * @author Stefan Rybacki
 * 
 */
public class ResourceTest extends TestCase {

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#Resource(java.lang.String, java.lang.Object)}
   * .
   */
  public final void testResource() {
    assertNotNull(new Resource("domain:location", null));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#getResource()}.
   */
  public final void testGetResource() {
    assertEquals(new Resource("domain:xxxxx", null).getResource(), null);
    for (int i = 0; i < 100; i++) {
      Object o = new Object();
      assertEquals(o, new Resource("domain:xxxxx", o).getResource());
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#getLastUsage()}.
   */
  public final void testGetLastUsage() {
    Resource resource = new Resource("domain:xxxxx", null);
    long l = resource.getLastUsage();
    long ll = l;
    for (int i = 0; i < 100; i++) {
      assertTrue((ll = new Resource("domain:xxxxx", null).getLastUsage()) > l);
      l = ll;
    }
    resource.getResource();
    assertTrue(resource.getLastUsage() > l);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#compareTo(org.jamesii.gui.application.resource.Resource)}
   * .
   */
  public final void testCompareTo() {
    assertTrue(new Resource("123", null).compareTo(new Resource("123", null)) == 0); // id
                                                                                     // equals
    assertTrue(new Resource("123", null).compareTo(new Resource("1234", null)) == 0); // null
                                                                                      // objects

    Resource resource1 = new Resource("123", new Object());
    Resource resource2 = new Resource("1234", new Object());
    assertTrue(resource1.compareTo(resource2) < 0);

    resource1.getResource();
    assertTrue(resource1.compareTo(resource2) > 0);

    resource2.getResource();
    assertTrue(resource1.compareTo(resource2) < 0);
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#getId()}.
   */
  public final void testGetId() {
    assertEquals("123", new Resource("123", new Object()).getId());
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#equals(java.lang.Object)}
   * .
   */
  public final void testEqualsObject() {
    Resource resource1 = new Resource("123", new Object());
    Resource resource2 = new Resource("123", new Object());
    Resource resource3 = new Resource("1234", new Object());
    assertTrue(resource1.equals(resource2));
    assertTrue(resource2.equals(resource1));
    assertFalse(resource1.equals(resource3));
    assertFalse(resource2.equals(resource3));
    assertFalse(resource3.equals(resource1));
    assertFalse(resource3.equals(resource2));
  }

  /**
   * Test method for
   * {@link org.jamesii.gui.application.resource.Resource#isNull()}.
   */
  public final void testIsNull() {
    assertTrue(new Resource("1234", null).isNull());
    assertFalse(new Resource("1234", new Object()).isNull());
  }

}
