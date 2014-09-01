/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import org.jamesii.core.util.collection.list.SortedList;
import org.jamesii.core.util.eventset.Entry;

import junit.framework.TestCase;

// TODO: Auto-generated Javadoc
/**
 * The Class SortedListTest.
 * 
 * @author Jan Himmelspach
 */
public class SortedListTest extends TestCase {

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    junit.textui.TestRunner.run(SortedListTest.class);
  }

  /** The number test elements to be used. */
  int testEle = 100;

  /**
   * Instantiates a new sorted list test.
   * 
   * @param arg0
   *          the arg0
   */
  public SortedListTest(String arg0) {
    super(arg0);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
  }

  @Override
  protected void tearDown() throws Exception {
    super.tearDown();
  }

  /**
   * Test extract top. Test method for
   * 'org.jamesii.core.util.eventset.SortedList.extractTop()'
   */
  public final void testExtractTop() {
    SortedList<Entry<Object, Double>> list = new SortedList<>();

    for (int i = 0; i < testEle; i++) {
      list.add(new Entry<>(null, Math.random()));
    }

    Entry<Object, Double> oldE = list.extractTop();
    for (int i = 0; i < testEle - 1; i++) {
      Entry<Object, Double> e = list.extractTop();
      assertTrue("Got " + e.getTime() + " after " + oldE.getTime(),
              e.compareTo(oldE) >= 0);
    }

  }

  /**
   * Test get list. Test method for
   * 'org.jamesii.core.util.eventset.SortedList.getList()'
   */
  public final void testGetList() {
    // TODO Auto-generated method stub

  }

  /**
   * Test insert. Test method for
   * 'org.jamesii.core.util.eventset.SortedList.insert(E)'
   */
  public final void testInsert() {
    SortedList<Entry<Object, Double>> list = new SortedList<>();
    list.add(new Entry<>(null, 1.));
    list.add(new Entry<>(null, 2.));
    list.add(new Entry<>(null, 3.));
    list.add(new Entry<>(null, 4.));
    list.add(new Entry<>(null, 1.3));
    list.add(new Entry<>(null, 2.2));
    list.add(new Entry<>(null, 0.2));

    for (int i = 0; i < testEle; i++) {
      list.add(new Entry<>(null, Math.random()));
    }

    // System.out.println(list);
  }

  /**
   * Test is empty.Test method for
   * 'org.jamesii.core.util.eventset.SortedList.isEmpty()'
   */
  public final void testIsEmpty() {
    // TODO Auto-generated method stub

  }

  /**
   * Test iterator.Test method for
   * 'org.jamesii.core.util.eventset.SortedList.iterator()'
   */
  public final void testIterator() {
    // TODO Auto-generated method stub

  }

  /**
   * Test remove.Test method for
   * 'org.jamesii.core.util.eventset.SortedList.remove(E)'
   */
  public final void testRemove() {
    // TODO Auto-generated method stub

  }

  /**
   * Test size.Test method for
   * 'org.jamesii.core.util.eventset.SortedList.size()'
   */
  public final void testSize() {
    // TODO Auto-generated method stub

  }

  /**
   * Test top.Test method for 'org.jamesii.core.util.eventset.SortedList.top()'
   */
  public final void testTop() {
    // TODO Auto-generated method stub

  }

}
