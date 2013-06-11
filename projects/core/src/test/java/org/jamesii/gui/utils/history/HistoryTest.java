/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.utils.history;

// import java.util.Iterator;

import java.util.List;

import org.jamesii.gui.utils.history.History;

import junit.framework.TestCase;

/**
 * @author Enrico Seib
 * 
 */
public class HistoryTest extends TestCase {

  /**
   * @param name
   */
  public HistoryTest(String name) {
    super(name);
  }

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    History.clear();

    History.putValueIntoHistory("org.jamesii",
        new String("C:/Daten/daten1.dat"));
    // "C:/werte/x.xx1" occurs in "org.jamesii", "org.jamesii.exp" and
    // "org.jamesii.dat"
    History.putValueIntoHistory("org.jamesii", new String("C:/werte/x.xx1"));

    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp1.exp"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp2.exp"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/exp2.exp"));
    // same values, but duplicated in "org.jamesii.exp" and
    // "org.jamesii.exp.dat"
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/werte/bla/blub/exp1.exp"));
    History
        .putValueIntoHistory("org.jamesii.exp", new String("C:/werte/x.xx1"));
    History.putValueIntoHistory("org.jamesii.exp", new String(
        "C:/Daten/SubData/daten.exp"));
    // same values, but duplicated in "org.jamesii.exp" and
    // "org.jamesii.exp.dat"
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/bla/blub/exp1.exp"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/x.xx1"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/x.xx1"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/SubData/daten.exp"));

    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/bla/blub/exp2.exp"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/schnick/schneck/x.xx1"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten.dat"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten.dat"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten.nms"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten1.nms"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten.dat1"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/daten1.dat1"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/schnick/schneck/x.xx2"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/werte/schnick/schneck/x.xx2"));
    History.putValueIntoHistory("org.jamesii.exp.dat", new String(
        "C:/Daten/SubData/daten.dat"));

  }

  /**
   * Test for
   * {@link org.jamesii.gui.utils.history.History#getValues(String id, boolean includeSubKeys, int sortingOption, int number)}
   * 
   */
  public final void testGetValuesFromHistory() {

    // List<String> list = History.getValuesFromHistory("org.jamesii", false);
    /*
     * System.out.println(list.size()); String s; Iterator<String> i =
     * list.iterator(); while (i.hasNext() ) { s = i.next();
     * System.out.println(s); }
     */
    // assertEquals(2, list.size());
    List<String> list =
        History.getValues("org.jamesii.exp", false, History.UNSORTED,
            History.ALL);
    assertEquals(5, list.size());

    list =
        History.getValues("org.jamesii.exp.dat", false, History.UNSORTED,
            History.ALL);
    /*
     * System.out.println(list.size()); String s; Iterator<String> i =
     * list.iterator(); while (i.hasNext() ) { s = i.next();
     * System.out.println(s); }
     */
    assertEquals(12, list.size());

  }

  /**
   * Test for
   * {@link org.jamesii.gui.utils.history.History#getValues(String id, boolean includeSubKeys, int sortingOption, int number)}
   * (this test includes subKeys)
   */
  public final void test2GetValuesFromHistory() {
    /*
     * System.out.println("!-------------------------------!");
     * System.out.println("Key: org.jamesii");
     */
    List<String> list =
        History.getValues("org.jamesii", true, History.UNSORTED, History.ALL);
    /*
     * String s; Iterator<String> i = list.iterator(); while (i.hasNext()) { s =
     * i.next(); System.out.println(s); }
     */
    assertEquals(15, list.size());

    /*
     * System.out.println("!-------------------------------!");
     * System.out.println("Key: org.jamesii.exp");
     */
    list =
        History.getValues("org.jamesii.exp", true, History.UNSORTED,
            History.ALL);
    /*
     * String s; Iterator<String> i = list.iterator(); while (i.hasNext()) { s =
     * i.next(); System.out.println(s); }
     */
    assertEquals(14, list.size());

    /*
     * System.out.println("!-------------------------------!");
     * System.out.println("Key: org.jamesii.exp.dat");
     */
    list =
        History.getValues("org.jamesii.exp.dat", true, History.UNSORTED,
            History.ALL);
    /*
     * String s; Iterator<String> i = list.iterator(); while (i.hasNext()) { s =
     * i.next(); System.out.println(s); }
     */
    assertEquals(12, list.size());

    list = History.getValues("", true, History.UNSORTED, History.ALL);
    assertEquals(0, list.size());

  }

  /**
   * tests if "getMostUsedValues()" and "getLatestUsedValues" work properly
   */
  public final void test5GetValuesFromHistory() {
    List<String> list =
        History.getValues("org.jamesii.exp.dat", true, History.LATEST, 5);
    List<String> list1 =
        History.getValues("org.jamesii.exp.dat", true, History.MOST_USED, 5);

    /*
     * String s; Iterator<String> i = list.iterator(); while (i.hasNext()) { s =
     * i.next(); System.out.println(s); }
     * /*System.out.println("!---------------------!"); Iterator<String> i1 =
     * list1.iterator(); while (i1.hasNext()) { s = i1.next();
     * System.out.println(s); }
     */
    assertEquals(5, list.size());
    assertEquals(5, list1.size());
    // false because of different sorting method
    assertEquals(false, list.containsAll(list1));

    list = History.getValues("org.jamesii.exp", true, History.MOST_USED, 10);
    /*
     * System.out.println("!-------------------------------!"); String s;
     * Iterator<String> i2 = list.iterator(); while (i2.hasNext()) { s =
     * i2.next(); System.out.println(s); }
     */
    assertEquals(10, list.size());
  }

  /**
   * tests if "getMostUsedValues()" and "getLatestUsedValues" work properly
   */
  public final void test6GetValuesFromHistory() {
    List<String> list =
        History.getValues("org.jamesii", true, History.LATEST, 15);
    for (int i = 0; i < list.size(); i++) {
      assertEquals(i, list.lastIndexOf(list.get(i)));
    }

    list = History.getValues("org.jamesii", true, History.MOST_USED, 15);
    for (int i = 0; i < list.size(); i++) {
      assertEquals(i, list.lastIndexOf(list.get(i)));
    }

  }
}
