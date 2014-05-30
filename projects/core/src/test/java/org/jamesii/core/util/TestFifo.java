/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import org.jamesii.core.util.collection.Fifo;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class TestFifo.
 * 
 * @author Johannes Rössel
 */
public class TestFifo extends TestCase {

  /** The f. */
  private Fifo<Integer> f;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    f = new Fifo<>();
  }

  /**
   * Test empty.
   */
  public void testEmpty() {
    assertTrue(f.isEmpty());
    f.add(5);
    assertFalse(f.isEmpty());
  }

  /**
   * Test add size get out.
   */
  public void testAddSizeGetOut() {
    assertTrue(f.isEmpty());

    f.add(2);
    f.add(3);
    f.add(5);
    f.add(7);
    f.add(11);

    assertEquals(5, f.size());

    assertEquals(new Integer(2), f.get());

    assertEquals(5, f.size());

    assertEquals(new Integer(2), f.out());
    assertEquals(new Integer(3), f.out());

    assertEquals(3, f.size());

    assertEquals(new Integer(5), f.get());

    assertEquals(3, f.size());

    assertEquals(new Integer(5), f.out());
    assertEquals(new Integer(7), f.out());

    assertEquals(1, f.size());

    assertEquals(new Integer(11), f.out());

    assertEquals(0, f.size());
    assertTrue(f.isEmpty());
  }

  /**
   * Test remove all.
   */
  public void testRemoveAll() {
    assertTrue(f.isEmpty());

    f.add(42);

    assertFalse(f.isEmpty());

    f.removeAllElements();

    assertTrue(f.isEmpty());
  }

  /**
   * Test clone.
   */
  @SuppressWarnings("unchecked")
  public void testClone() {
    assertTrue(f.isEmpty());

    f.add(42);

    Fifo<Integer> g = (Fifo<Integer>) f.clone();

    assertFalse(f.isEmpty());
    assertFalse(g.isEmpty());

    assertEquals(new Integer(42), g.out());
    assertTrue(g.isEmpty());
    assertFalse(f.isEmpty());
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
    f = null;
  }

}
