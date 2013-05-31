/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.HashMap;
import java.util.Map;

import org.jamesii.core.experiments.optimization.parameter.representativeValue.SimpleRepValueComparator;

import junit.framework.TestCase;

/**
 * Test for {@link SimpleRepValueComparator}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestSimpleRepValueComparator extends TestCase {

  /** The comparator. */
  SimpleRepValueComparator comparator;

  Map<String, Double> map1 = new HashMap<>();
  {
    map1.put("a", 1.);
    map1.put("b", 2.);
    map1.put("c", 3.);
  }

  Map<String, Double> map2 = new HashMap<>();
  {
    map2.put("a", 2.);
    map2.put("b", 3.);
    map2.put("c", 4.);
  }

  Map<String, Double> map3 = new HashMap<>();
  {
    map3.put("a", 4.);
    map3.put("b", 3.);
    map3.put("c", 2.);
  }

  @Override
  public void setUp() {
    comparator = new SimpleRepValueComparator();
  }

  @Override
  public void tearDown() {
    comparator = null;
  }

  public void testSorting() {
    assertEquals(0, comparator.compare(new HashMap<String, Double>(), map1));
    assertEquals(0, comparator.compare(map2, new HashMap<String, Double>()));
    assertEquals(0, comparator.compare(new HashMap<String, Double>(),
        new HashMap<String, Double>()));
    assertEquals(-1, comparator.compare(map1, map2));
    assertEquals(1, comparator.compare(map2, map1));
    assertEquals(0, comparator.compare(map2, map3));
    assertEquals(0, comparator.compare(map3, map2));
  }

}
