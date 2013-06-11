/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.random.ComparableNumberPoint;
import org.jamesii.core.math.random.RandomSampler;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * Tests the {@link RandomSampler} class.
 * 
 * @author Johannes Rössel
 */
public class TestRandomSampler extends TestCase {

  /**
   * ArrayList of input values. Will be filled with numbers from 1 to 10 in
   * {@link #setUp()}.
   */
  private List<Integer> x;

  /**
   * Random number generator that will be re-used for each test. Just to save me
   * some typing. Will be set to {@link JavaRandom} in {@link #setUp()}.
   */
  private IRandom r;

  @Override
  protected void setUp() throws Exception {
    // prepare array list
    x = new ArrayList<>();
    for (int i = 0; i < 10; i++) {
      x.add(i + 1);
    }
    r = new JavaRandom();
  }

  @Override
  protected void tearDown() throws Exception {
    x = null;
    r = null;
  }

  /**
   * Tests whether
   * {@link RandomSampler#sample(Integer, Integer, Integer, java.util.Collection, IRandom)}
   * throws the correct exceptions when invalid arguments or conditions are
   * encountered.
   */
  public void testSampleCollectionExceptions() {
    try {
      // try sampling with negative count
      RandomSampler.sample(-1, 2, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with negative bounds
      RandomSampler.sample(1, -1, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with bounds reversed (note: this MAY call for silent
      // correction in the future)
      RandomSampler.sample(1, 8, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with upper bound out of range
      RandomSampler.sample(1, 1, 15, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with lower and upper bound out of range
      RandomSampler.sample(1, 11, 15, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Tests the
   * {@link RandomSampler#sample(Integer, Integer, Integer, java.util.Collection, IRandom)}
   * method.
   */
  public void testSampleCollection() {
    List<Integer> y;

    // trivial case, empty list.
    y = RandomSampler.sample(0, 1, 1, x, r);
    assertTrue(y.size() == 0);

    // checking lower end - exactly one value
    y = RandomSampler.sampleSet(1, 0, 0, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 1);

    // checking something in the middle - exactly one value
    y = RandomSampler.sampleSet(1, 4, 4, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 5);

    // checking upper end - exactly one value
    y = RandomSampler.sampleSet(1, 9, 9, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 10);

    // test whether count works correctly with being greater than interval
    // length
    y = RandomSampler.sample(5, 1, 1, x, r);
    assertTrue(y.size() == 5);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) == 2);
    }

    // test with a sample source length of two
    y = RandomSampler.sample(7, 3, 4, x, r);
    assertTrue(y.size() == 7);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) == 4 || y.get(i) == 5);
    }

    // and three
    y = RandomSampler.sample(6, 2, 4, x, r);
    assertTrue(y.size() == 6);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) == 3 || y.get(i) == 4 || y.get(i) == 5);
    }
  }

  /**
   * Tests whether
   * {@link RandomSampler#sampleSet(Integer, Integer, Integer, java.util.Collection, IRandom)}
   * throws the correct exceptions when invalid arguments or conditions are
   * encountered.
   */
  public void testSampleSetCollectionExceptions() {
    try {
      // try sampling with negative count
      RandomSampler.sampleSet(-1, 2, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with lower bound out of range (negative)
      RandomSampler.sampleSet(1, -1, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with bounds reversed
      RandomSampler.sampleSet(1, 8, 5, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with upper bound out of range
      RandomSampler.sampleSet(1, 1, 15, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with lower and upper bound out of range
      RandomSampler.sampleSet(1, 11, 16, x, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Tests the
   * {@link RandomSampler#sampleSet(Integer, Integer, Integer, java.util.Collection, IRandom)}
   * method.
   */
  public void testSampleSetCollection() {
    List<Integer> y;

    // trivial case, empty list.
    y = RandomSampler.sample(0, 1, 1, x, r);
    assertTrue(y.size() == 0);

    // checking lower end - exactly one value
    y = RandomSampler.sampleSet(1, 0, 0, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 1);

    // checking something in the middle - exactly one value
    y = RandomSampler.sampleSet(1, 4, 4, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 5);

    // checking upper end - exactly one value
    y = RandomSampler.sampleSet(1, 9, 9, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 10);

    // check for correct cap
    y = RandomSampler.sampleSet(5, 1, 1, x, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 2);

    // check for all numbers being present
    y = RandomSampler.sampleSet(5, 0, 4, x, r);
    assertTrue(y.size() == 5);
    for (int i = 0; i < 5; i++) {
      assertTrue(y.contains(i + 1));
    }

    // again, range in the middle of our array
    y = RandomSampler.sampleSet(6, 3, 8, x, r);
    assertTrue(y.size() == 6);
    for (int i = 3; i <= 8; i++) {
      assertTrue(y.contains(i + 1));
    }
    // ensure no values outside the sample range appear.
    for (int i = 0; i < 3; i++) {
      assertFalse(y.contains(i + 1));
    }
    for (int i = 9; i < 10; i++) {
      assertFalse("Value " + (i + 1) + " existed unexpectedly in collection.",
          y.contains(i + 1));
    }

    // check sampling from something else than 1..n
    List<String> x2 = Arrays.asList("42", "23", "a", "c", "e", "g", "z");
    List<String> y2 = RandomSampler.sampleSet(3, 1, 5, x2, r);
    assertEquals(3, y2.size());
    int numContained = 0;
    for (int i = 1; i <= 5; i++) {
      if (y2.contains(x2.get(i))) {
        numContained++;
      }
    }
    assertEquals(3, numContained);
  }

  /**
   * Tests whether
   * {@link RandomSampler#sample(Integer, Integer, Integer, IRandom)} throws the
   * correct exceptions when invalid arguments or conditions are encountered.
   */
  public void testSampleNumbersExceptions() {
    try {
      // try sampling with negative count
      RandomSampler.sample(-1, 1, 2, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // try sampling with bounds reversed
      RandomSampler.sample(1, 5, 3, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Tests the {@link RandomSampler#sample(Integer, Integer, Integer, IRandom)}
   * method.
   */
  public void testSampleNumbers() {
    List<Long> y;

    // trivial case, empty list.
    y = RandomSampler.sample(0, 1, 2, r);
    assertTrue(y.size() == 0);

    // exactly one known value
    y = RandomSampler.sample(1, 1, 1, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 1);

    // exactly one unknown value
    y = RandomSampler.sample(1, 1, 5, r);
    assertTrue(y.size() == 1);
    // has to be within [1, 5]
    assertTrue(y.get(0) >= 1 && y.get(0) <= 5);

    // multiple values
    y = RandomSampler.sample(500, -10, 10, r);
    assertTrue(y.size() == 500);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) >= -10 && y.get(i) <= 10);
    }
  }

  /**
   * Tests whether
   * {@link RandomSampler#sampleSet(Integer, Integer, Integer, IRandom)} throws
   * the correct exceptions when invalid arguments or conditions are
   * encountered.
   */
  public void testSampleSetNumbersExceptions() {
    try {
      // test for negative count
      RandomSampler.sampleSet(-1, 2, 3, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
    try {
      // test for reversed bounds
      RandomSampler.sampleSet(2, 5, 1, r);
      fail();
    } catch (IllegalArgumentException e) {
      assertTrue(true);
    } catch (Exception e) {
      fail();
    }
  }

  /**
   * Tests the
   * {@link RandomSampler#sampleSet(Integer, Integer, Integer, IRandom)} method.
   */
  public void testSampleSetNumbers() {
    List<Long> y;

    // trivial case, empty list
    y = RandomSampler.sampleSet(0, 1, 2, r);
    assertTrue(y.size() == 0);

    // exactly one known value
    y = RandomSampler.sampleSet(1, 1, 1, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) == 1);

    // exactly one unknown value
    y = RandomSampler.sampleSet(1, 1, 3, r);
    assertTrue(y.size() == 1);
    assertTrue(y.get(0) >= 1 && y.get(0) <= 3);

    // multiple values from a known range, not filling the range completely
    y = RandomSampler.sampleSet(20, -10, 20, r);
    assertTrue(y.size() == 20);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) >= -10 && y.get(i) <= 20);
    }

    // multiple values, filling the sampling range completely
    y = RandomSampler.sampleSet(3, 1, 3, r);
    assertTrue(y.size() == 3);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) == 1 || y.get(i) == 2 || y.get(i) == 3);
    }

    // multiple values, sample range smaller than count, testing whether capping
    // works correctly
    y = RandomSampler.sampleSet(100, 10, 30, r);
    assertTrue(y.size() == 21);
    for (int i = 0; i < y.size(); i++) {
      assertTrue(y.get(i) >= 10 && y.get(i) <= 30);
    }
  }

  /**
   * Tests {@link RandomSampler#sampleUnique(int, int, IRandom)}.
   */
  public void testSampleUniqueNumbers() {

    int upperBound = 100;
    int sampleCount = 10;

    // Empty sample
    int[] sample = RandomSampler.sampleUnique(0, 0, r);
    assertEquals(0, sample.length);

    // Sample numbers
    sample = RandomSampler.sampleUnique(sampleCount, upperBound, r);
    assertEquals(10, sample.length);
    List<Integer> sampleList = new ArrayList<>();
    for (int s : sample) {
      assertTrue(s >= 0 && s < upperBound);
      assertFalse(sampleList.contains(s));
      sampleList.add(s);
    }

    boolean exec = false;
    try {
      RandomSampler.sampleUnique(11, 10, r);
    } catch (IllegalArgumentException ex) {
      exec = true;
    }
    assertTrue(exec);
  }

  /**
   * Test {@link RandomSampler#permuteRouletteWheel(Map, IRandom)}
   */
  public void testPermuteRouletteWheel() {
    final int numRuns = 20;
    final Map<Integer, Double> testWeights = new LinkedHashMap<>();
    testWeights.put(2, 0.9);
    testWeights.put(1, 0.09);
    testWeights.put(0, 0.01);
    int startsWith0 = 0, startsWith2 = 0;
    for (int iRun = 0; iRun < numRuns; iRun++) {
      Integer start = RandomSampler.permuteRouletteWheel(testWeights, r).get(0);
      if (start == 0) {
        startsWith0++;
      } else if (start == 2) {
        startsWith2++;
      }
    }
    assertTrue("Event that should happen with p=0.01 happened "
        + "more than 3 times out of " + numRuns, startsWith0 <= 3);
    assertTrue("Event that should happen with p=0.9 happened "
        + "less than 10 times out of " + numRuns, startsWith2 >= 10);
    // TODO?: test borderline cases ?! (0 weights, negative weights?
    // (may require further checks in code under test))
  }

  /**
   * Test double point cloud sampling.
   */
  public void testDoublePointCloudSampling() {
    Pair<Double, Double> dimOne = new Pair<>(-5., 5.);
    Pair<Double, Double> dimTwo = new Pair<>(5., 25.);
    List<Pair<Double, Double>> bounds = new ArrayList<>();
    bounds.add(dimOne);
    bounds.add(dimTwo);

    int amount = 1000;
    List<Double[]> pointCloud =
        RandomSampler.samplePointCloud(r, amount, bounds);

    assertEquals(amount, pointCloud.size());
    for (Double[] point : pointCloud) {
      assertTrue(point[0] >= -5 && point[0] < 5);
      assertTrue(point[1] >= 5 && point[1] < 25);
    }
  }

  /**
   * Test {@link ComparableNumberPoint}, which is used for point cloud sampling.
   */
  public void testComparableNumberPoints() {
    ComparableNumberPoint simplePoint =
        new ComparableNumberPoint(new Double[] { 1., 2. });
    ComparableNumberPoint equalPoint =
        new ComparableNumberPoint(new Double[] { 1., 2. });

    ComparableNumberPoint unequalPoint1 =
        new ComparableNumberPoint(new Integer[] { 1, 2 });
    ComparableNumberPoint unequalPoint2 =
        new ComparableNumberPoint(new Double[] { 2., 1. });

    assertEquals(simplePoint, equalPoint);
    assertFalse(simplePoint.equals(unequalPoint1));
    assertFalse(simplePoint.equals(unequalPoint2));
  }

  /**
   * Test integer point cloud sampling.
   */
  public void testIntegerPointCloudSampling() {
    Pair<Integer, Integer> dimOne = new Pair<>(-5, 5);
    Pair<Integer, Integer> dimTwo = new Pair<>(5, 25);
    List<Pair<Integer, Integer>> bounds = new ArrayList<>();
    bounds.add(dimOne);
    bounds.add(dimTwo);

    checkSuitableAmountCreation(1, bounds);
    checkSuitableAmountCreation(100, bounds);
    checkUnsuitableAmountCreation(101, bounds);
    checkUnsuitableAmountCreation(200, bounds);
    checkUnsuitableAmountCreation(1000, bounds);
  }

  /**
   * Check creation of an unsuitable amount of random points.
   * 
   * @param bounds
   *          the bounds
   * @param amount
   *          the amount
   */
  private void checkUnsuitableAmountCreation(int amount,
      List<Pair<Integer, Integer>> bounds) {
    boolean exceptionCaught = false;
    try {
      checkSuitableAmountCreation(amount, bounds);
    } catch (IllegalArgumentException ex) {
      exceptionCaught = true;
    }
    assertTrue(exceptionCaught);
  }

  /**
   * Check creation of a suitable amount of random points.
   * 
   * @param bounds
   *          the bounds
   */
  private void checkSuitableAmountCreation(int amount,
      List<Pair<Integer, Integer>> bounds) {
    List<Integer[]> pointCloud =
        RandomSampler.sampleIntegerPointCloud(r, amount, bounds);

    assertEquals(amount, pointCloud.size());

    for (Integer[] point : pointCloud) {
      assertTrue(point[0] >= -5 && point[0] < 5);
      assertTrue(point[1] >= 5 && point[1] < 25);
    }
  }

}
