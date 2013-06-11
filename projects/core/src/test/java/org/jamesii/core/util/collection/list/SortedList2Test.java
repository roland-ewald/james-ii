/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.collection.list.SortedList2;
import org.jamesii.core.util.sorting.SortOrder;

/**
 * @author Stefan Rybacki
 * 
 */
public class SortedList2Test extends ChattyTestCase {

  private Random rnd;

  @Override
  protected void setUp() throws Exception {
    long seed = (long) (Math.random() * Long.MAX_VALUE);
    addParameter("seed", seed);

    rnd = new Random(seed);
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.list.SortedList2#add(java.lang.Comparable)}
   * .
   */
  public void testAddE() {
    List<Long> checkList = new ArrayList<>();
    List<Long> sorted = new SortedList2<>(new ArrayList<Long>());
    for (int i = 0; i < 1000; i++) {
      long next = rnd.nextLong();
      checkList.add(next);
      sorted.add(next);
    }

    // sort checkList
    Collections.sort(checkList);
    int index = 0;
    for (long l : sorted) {
      assertEquals(checkList.get(index).longValue(), l);
      index++;
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.list.SortedList2#add(int, java.lang.Comparable)}
   * .
   */
  public void testAddIntE() {
    List<Long> sorted = new SortedList2<>(new ArrayList<Long>());
    try {
      sorted.add(0, 1000L);
      fail("Exception should be thrown before reaching here!");
    } catch (Exception e) {

    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.list.SortedList2#addAll(java.util.Collection)}
   * .
   */
  public void testAddAllCollectionOfQextendsE() {
    List<Long> checkList = new ArrayList<>();
    List<Long> sorted = new SortedList2<>(new ArrayList<Long>());
    for (int i = 0; i < 1000; i++) {
      long next = rnd.nextLong();
      checkList.add(next);
    }

    sorted.addAll(checkList);

    // sort checkList
    Collections.sort(checkList);
    int index = 0;
    for (long l : sorted) {
      assertEquals(checkList.get(index).longValue(), l);
      index++;
    }
  }

  /**
   * Test method for
   * {@link org.jamesii.core.util.collection.list.SortedList2#setSortOrder(org.jamesii.core.util.sorting.SortOrder)}
   * .
   */
  public void testSetSortOrder() {
    List<Long> checkList = new ArrayList<>();
    SortedList2<Long> sorted = new SortedList2<>(new ArrayList<Long>());
    for (int i = 0; i < 1000; i++) {
      long next = rnd.nextLong();
      checkList.add(next);
      sorted.add(next);
    }

    // sort checkList
    Collections.sort(checkList);
    int index = 0;
    for (long l : sorted) {
      assertEquals(checkList.get(index).longValue(), l);
      index++;
    }
    assertEquals(index, checkList.size());

    sorted.setSortOrder(SortOrder.DESCENDING);
    index = checkList.size() - 1;
    for (long l : sorted) {
      assertEquals(checkList.get(index).longValue(), l);
      index--;
    }

    assertEquals(index, -1);
  }

}
