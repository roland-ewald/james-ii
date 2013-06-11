/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.univariate;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.statistics.univariate.Analysis;
import org.jamesii.core.math.statistics.univariate.Variance;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * @author Jan Himmelspach *
 */
public class VarianceTest extends TestCase {

  private class TestSet {
    public double[] values;

    public double variance;

    public double mean;

    public TestSet(double[] values, double variance, double mean) {
      this.values = values;
      this.variance = variance;
      this.mean = mean;
    }
  }

  TestSet[] testDouble = {
      // even number of equal values
      new TestSet(new double[] { 1., 1., 1., 1., 1., 1. }, 0., 1.),
      // odd number of equal values
      new TestSet(new double[] { 1., 1., 1., 1., 1. }, 0., 1.),
      new TestSet(new double[] { 1., 1., 1., 1., 1., 100. }, 1633.5, 17.5),
      new TestSet(new double[] { 10e9 + 4, 10e9 + 7, 10e9 + 13, 10e9 + 16 },
          30, 10e9 + 10),
      new TestSet(
          new double[] { 10e19 + 4, 10e19 + 7, 10e19 + 13, 10e19 + 16 }, 30,
          10e19 + 10) };

  /**
   * @param name
   */
  public VarianceTest(String name) {
    super(name);
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
   * As list.
   * 
   * @param vals
   *          the vals
   * 
   * @return the list< double>
   */
  public static List<Double> asList(double[] vals) {
    List<Double> result = new ArrayList<>();
    for (double t : vals) {
      result.add(t);
    }
    return result;
  }

  /**
   * As list.
   * 
   * @param vals
   *          the vals
   * 
   * @return the list< double>
   */
  public static List<Integer> asList(int[] vals) {
    List<Integer> result = new ArrayList<>();
    for (int t : vals) {
      result.add(t);
    }
    return result;
  }

  /**
   * Test variance.
   */
  public void testVariance() {
    // double[] d0 = { 0.};
    // assertTrue ((Double.compare(Variance.variance(d0), 0) == 0));
    // assertTrue ((Double.compare(Variance.variance(asList(d0)), 0) == 0));

    double[] d1 = { 1., 1., 1., 1., 1., 1. };
    assertTrue((Double.compare(Variance.variance(d1), 0) == 0));

    assertTrue((Double.compare(Variance.varianceNStable(d1), 0) == 0));
    assertTrue((Double.compare(Variance.varianceNStable(asList(d1)), 0) == 0));

    assertTrue((Double.compare(Variance.varianceTwoPass(d1), 0) == 0));
    assertTrue((Double.compare(Variance.varianceTwoPass(asList(d1)), 0) == 0));

    assertTrue((Double.compare(Variance.variance(asList(d1)), 0) == 0));
    assertEquals(new Analysis().compute(d1).getVariance(),
        Variance.variance(d1), 0.0001);

    double[] d2 = { 1., 1., 1., 1., 1. };
    assertTrue((Double.compare(Variance.variance(d2), 0) == 0));

    assertTrue((Double.compare(Variance.varianceNStable(d2), 0) == 0));
    assertTrue((Double.compare(Variance.varianceNStable(asList(d2)), 0) == 0));

    assertTrue((Double.compare(Variance.varianceTwoPass(d2), 0) == 0));
    assertTrue((Double.compare(Variance.varianceTwoPass(asList(d2)), 0) == 0));

    assertTrue((Double.compare(Variance.variance(asList(d2)), 0) == 0));

    assertEquals(new Analysis().compute(d2).getVariance(),
        Variance.variance(d2), 0.0001);

    double[] d3 = { 1., 1., 1., 1., 1., 100. };
    assertTrue((Double.compare(Variance.variance(d3), 0) != 0));
    assertTrue((Double.compare(Variance.variance(d3), 1633.5) == 0));

    assertTrue((Double.compare(Variance.varianceNStable(d3), 1633.5) == 0));
    assertTrue((Double.compare(Variance.varianceNStable(asList(d3)), 1633.5) == 0));

    assertTrue((Double.compare(Variance.varianceTwoPass(d3), 1633.5) == 0));
    assertTrue((Double.compare(Variance.varianceTwoPass(asList(d3)), 1633.5) == 0));

    assertTrue((Double.compare(Variance.variance(asList(d3)), 0) != 0));
    assertTrue((Double.compare(Variance.variance(asList(d3)), 1633.5) == 0));

    assertEquals(new Analysis().compute(d3).getVariance(),
        Variance.variance(d3), 0.0001);

    // large values which only differs slightly
    double[] d4 = { 10e9 + 4, 10e9 + 7, 10e9 + 13, 10e9 + 16 };
    assertEquals(new Analysis().compute(d4).getVariance(),
        Variance.variance(d4), 0.0001);
    assertEquals(30, Variance.variance(d4), 0.0001);

    assertEquals(30, Variance.varianceNStable(d4), 0.0001);
    assertEquals(30, Variance.varianceNStable(asList(d4)), 0.0001);

    assertEquals(30, Variance.varianceTwoPass(d4), 0.0001);
    assertEquals(30, Variance.varianceTwoPass(asList(d4)), 0.0001);

    // assertEquals (30, Variance.varianceUnStable(d4), 0.0001);

    int[] i1 = { 1, 1, 1, 1, 1, 1 };
    int[] i2 = { 1, 1, 1, 1, 1 };
    assertTrue((Double.compare(Variance.variance(i1), 0) == 0));

    assertTrue((Double.compare(Variance.variance(i2), 0) == 0));

    int[] i3 = { 1, 1, 1, 1, 1, 100 };
    assertTrue((Double.compare(Variance.variance(i3), 0) != 0));
    assertTrue((Double.compare(Variance.variance(i3), 1633.5) == 0));

  }

  /**
   * Generic test variance.
   */
  public void genericTestVariance() {
    for (TestSet set : testDouble) {
      assertEquals(Variance.variance(set.values), set.variance, 0.0001);
    }
  }

  /**
   * Test variance.
   */
  public void testVarianceAndAM() {

    Pair<Double, Double> result;

    // double[] d0 = { 0.};
    //
    // result = Variance.varianceAndAM(d0);
    // assertTrue ((Double.compare(result.getFirstValue(), 0) == 0));
    // assertTrue ((Double.compare(result.getSecondValue(), 0) == 0));
    // result = Variance.varianceAndAM(asList(d0));
    // assertTrue ((Double.compare(result.getFirstValue(), 0) == 0));
    // assertTrue ((Double.compare(result.getSecondValue(), 0) == 0));

    double[] d1 = { 1., 1., 1., 1., 1., 1. };

    result = Variance.varianceAndAM(d1);
    assertTrue((Double.compare(result.getFirstValue(), 0) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 1.0) == 0));

    assertEquals(new Analysis().compute(d1).getVariance(),
        result.getFirstValue(), 0.0001);
    assertEquals(new Analysis().compute(d1).getMean(), result.getSecondValue(),
        0.0001);

    result = Variance.varianceAndAM(asList(d1));
    assertTrue((Double.compare(result.getFirstValue(), 0) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 1.0) == 0));

    double[] d2 = { 1., 1., 1., 1., 1. };

    result = Variance.varianceAndAM(d2);
    assertTrue((Double.compare(result.getFirstValue(), 0) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 1.0) == 0));
    assertEquals(new Analysis().compute(d2).getVariance(),
        result.getFirstValue(), 0.0001);
    assertEquals(new Analysis().compute(d2).getMean(), result.getSecondValue(),
        0.0001);

    result = Variance.varianceAndAM(asList(d2));
    assertTrue((Double.compare(result.getFirstValue(), 0) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 1.0) == 0));
    assertEquals(new Analysis().compute(d2).getVariance(),
        result.getFirstValue(), 0.0001);
    assertEquals(new Analysis().compute(d2).getMean(), result.getSecondValue(),
        0.0001);

    double[] d3 = { 1., 1., 1., 1., 1., 100. };

    result = Variance.varianceAndAM(d3);

    assertTrue((Double.compare(result.getFirstValue(), 0) != 0));
    assertTrue((Double.compare(result.getFirstValue(), 1633.5) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 17.5) == 0));
    assertEquals(new Analysis().compute(d3).getVariance(),
        result.getFirstValue(), 0.0001);
    assertEquals(new Analysis().compute(d3).getMean(), result.getSecondValue(),
        0.0001);

    result = Variance.varianceAndAM(asList(d3));

    assertTrue((Double.compare(result.getFirstValue(), 0) != 0));
    assertTrue((Double.compare(result.getFirstValue(), 1633.5) == 0));
    assertTrue((Double.compare(result.getSecondValue(), 17.5) == 0));

  }

}
