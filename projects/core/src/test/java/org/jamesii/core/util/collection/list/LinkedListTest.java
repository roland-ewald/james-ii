/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import junit.framework.TestCase;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class LinkedListTest extends TestCase {

  public void testSize() {
    LinkedList<Object> list = new LinkedList<>();
    assertTrue(list.size() == 0);
    list.add(new Object());
    assertTrue(list.size() == 1);
    for (int i = 0; i < 100; i++) {
      list.add(new Object());
      assertTrue(list.size() == 2 + i);
    }
    list.clear();
    assertTrue(list.size() == 0);
  }

  public void testAdd() {
    LinkedList<Object> list = new LinkedList<>();
    Object[] o = new Object[100];
    for (int i = 0; i < o.length; i++) {
      o[i] = new Object();
      list.add(o[i]);
      assertEquals(list.get(i), o[i]);
    }

  }

  public void testRemove() {
    LinkedList<Object> list = new LinkedList<>();
    try {
      list.remove();
      fail();
    } catch (Exception e) {
    }

    Object o = new Object();
    list.add(o);
    assertEquals(list.remove(), o);
    assertTrue(list.size() == 0);

    list.add(o);
    Object o2 = new Object();
    list.add(o2);
    Object o3 = new Object();
    list.add(o3);

    assertEquals(list.remove(), o);
    assertEquals(list.remove(), o2);
    assertEquals(list.remove(), o3);
    assertTrue(list.size() == 0);
  }

  public void testElement() {
    LinkedList<Object> list = new LinkedList<>();
    try {
      list.element();
      fail();
    } catch (Exception e) {
    }

    Object o = new Object();
    list.add(o);
    assertEquals(list.element(), o);
    assertTrue(list.size() == 1);

    Object o2 = new Object();
    list.add(o2);
    Object o3 = new Object();
    list.add(o3);

    assertEquals(list.element(), o);
    assertTrue(list.size() == 3);
  }

  public void testRemoveE() {
    LinkedList<Object> list = new LinkedList<>();
    assertFalse(list.remove(new Object()));
    Object o = new Object();
    list.add(o);
    assertTrue(list.remove(o));

    list.add(o);
    Object o2 = new Object();
    list.add(o2);
    assertTrue(list.remove(o2));
    list.add(o2);
    Object o3 = new Object();
    list.add(o3);
    assertTrue(list.remove(o3));
    assertTrue(list.remove(o2));
    list.add(o2);
    list.add(o3);
    assertTrue(list.remove(o2));
    assertTrue(list.remove(o));
    assertTrue(list.remove(o3));
    assertEquals(list.size(), 0);
  }

  public void testGet() {
    LinkedList<Object> list = new LinkedList<>();
    try {
      list.get(0);
      fail();
    } catch (Exception e) {
    }
    try {
      list.get(-1);
      fail();
    } catch (Exception e) {
    }
    try {
      list.get(1);
      fail();
    } catch (Exception e) {
    }
    try {
      list.get(10);
      fail();
    } catch (Exception e) {
    }
    Object o = new Object();
    list.add(o);
    try {
      list.get(-1);
      fail();
    } catch (Exception e) {
    }
    try {
      list.get(1);
      fail();
    } catch (Exception e) {
    }
    try {
      list.get(10);
      fail();
    } catch (Exception e) {
    }
    assertEquals(list.get(0), o);

    Object o2 = new Object();
    list.add(o2);
    assertEquals(list.get(0), o);
    assertEquals(list.get(1), o2);

    Object o3 = new Object();
    list.add(o3);
    assertEquals(list.get(0), o);
    assertEquals(list.get(1), o2);
    assertEquals(list.get(2), o3);

    list.remove();
    assertEquals(list.get(0), o2);
    assertEquals(list.get(1), o3);

    list.remove();
    assertEquals(list.get(0), o3);

  }

  public void testContains() {
    java.util.LinkedList<String> list1 = new java.util.LinkedList<>();
    org.jamesii.core.util.collection.list.LinkedList<String> list2 =
        new org.jamesii.core.util.collection.list.LinkedList<>();

    String hello1 = new String("Hello World");
    String hello2 = new String("Hello World");

    list1.add(hello1);
    list2.add(hello1);

    assertTrue(hello1 != hello2);
    assertTrue(hello1.equals(hello2));
    assertTrue(list1.contains(hello2));
    assertTrue(list2.contains(hello2));
  }

}
