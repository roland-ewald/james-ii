/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import org.jamesii.core.util.collection.Heap;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * Tests the {@link Heap}.
 * 
 * @author Jan Himmelspach
 */
public class HeapTest extends TestCase {

  /**
   * The Class Obj.
   */
  private class Obj implements Comparable<Obj> {

    /** The s. */
    Integer s;

    /**
     * Instantiates a new obj.
     * 
     * @param str
     *          the str
     */
    public Obj(Integer str) {
      s = str;
    }

    /**
     * Gets the s.
     * 
     * @return the s
     */
    public Integer getS() {
      return s;
    }

    @Override
    public int compareTo(Obj o) {
      return s.compareTo(o.getS());
    }

  }

  /**
   * Test add.
   */
  public void testAdd() {
    Heap<Obj> h = new Heap<>();
    h.add(new Obj(1));
    assertTrue(h.size() == 1);
  }

  /**
   * Test extract top.
   */
  public void testExtractTop() {
    Heap<Obj> h = new Heap<>();
    Obj o1 = new Obj(1);
    h.add(o1);
    Obj o = h.extractTop();
    assertTrue(o1 == o);
    h.add(o1);
    Obj o2 = new Obj(3);
    Obj o3 = new Obj(5);
    Obj o4 = new Obj(4);
    h.add(o2);
    h.add(o3);
    h.add(o4);

    o = h.extractTop();
    assertTrue(o1 == o);
    o = h.extractTop();
    assertTrue(o2 == o);
    o = h.extractTop();
    assertTrue(o4 == o);
    o = h.extractTop();
    assertTrue(o3 == o);

    h.add(o2);
    h.add(o3);
    h.add(o4);
    o = h.extractTop();
    assertTrue(o2 == o);

    h.add(o1);
    o = h.extractTop();
    assertTrue(o1 == o);

    Heap<Double> heap2 = new Heap<>();
    Double[] d = new Double[6];
    heap2.add(d[2] = 0.000002);
    heap2.add(d[4] = 0.0000021);
    heap2.add(d[5] = 0.0000022);
    heap2.add(d[0] = 0.0000019);
    heap2.add(d[1] = 0.00000199);
    heap2.add(d[3] = 0.00000201);

    for (int i = 0; i < 6; i++) {
      Double t = heap2.extractTop();
      assertTrue(t.compareTo(d[i]) == 0);
    }

  }

  /**
   * Test is empty.
   */
  public void testIsEmpty() {
    Heap<Obj> h = new Heap<>();
    assertTrue(h.isEmpty());
    h.add(new Obj(2));
    assertFalse(h.isEmpty());
  }

  /**
   * Test remove e.
   */
  public void testRemoveE() {
    Heap<Obj> heap = new Heap<>();
    // remove on empty heap
    assertFalse(heap.remove(new Obj(2)));

    // remove on heap with one entry
    Obj o1 = new Obj(2);
    heap.add(o1);
    assertTrue(heap.remove(o1));

    heap.add(o1);
    Obj o2 = new Obj(3);
    heap.add(o2);

    // remove last/first element
    assertTrue(heap.remove(o2));

    heap.add(o2);

    // remove first/last element
    assertTrue(heap.remove(o1));
    assertTrue(heap.top() == o2);

    heap.add(o1);

    Obj o3 = new Obj(3);
    heap.add(o3);

    Obj o4 = new Obj(4);
    heap.add(o4);
    Obj o5 = new Obj(5);
    heap.add(o5);

    Obj o6 = new Obj(6);
    heap.add(o6);

    // remove an element from somewhere in the heap
    assertTrue(heap.remove(o3));
    assertTrue(heap.size() == 5);

  }

  /**
   * Test size.
   */
  public void testSize() {
    Heap<Obj> h = new Heap<>();
    assertTrue(h.size() == 0);
  }

  /**
   * Test top.
   */
  public void testTop() {
    Heap<Obj> h = new Heap<>();
    Obj o1 = new Obj(10);
    h.add(o1);
    Obj o = h.top();
    assertTrue(o1 == o);
    Obj o2 = new Obj(3);
    Obj o3 = new Obj(5);
    Obj o4 = new Obj(4);
    h.add(o2);
    h.add(o3);
    h.add(o4);
    o = h.top();
    assertTrue(o2 == o);
  }

  /**
   * Test get list.
   */
  public void testGetList() {
    Heap<Obj> h = new Heap<>();
    assertTrue(h.getList().size() == 1);
  }

}
