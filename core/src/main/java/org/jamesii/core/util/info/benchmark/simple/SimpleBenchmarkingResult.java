/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info.benchmark.simple;

import java.util.Arrays;

import org.jamesii.core.util.info.benchmark.IBenchmarkingResult;

/**
 * The Class SimpleBenchmarkingResult. The result return by the
 * {@link org.jamesii.core.util.info.benchmark.simple.SimpleBenchmark} class.
 * 
 * @author Jan Himmelspach
 */
public class SimpleBenchmarkingResult implements IBenchmarkingResult {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2943966513740117034L;

  /**
   * The position of the metric
   */
  private static final int METRICPOS = 3;

  /** The vals. */
  private long[] vals;

  /**
   * Instantiates a new simple benchmarking result.
   * 
   * @param vals
   *          the vals
   */
  public SimpleBenchmarkingResult(long[] vals) {
    super();
    this.vals = Arrays.copyOf(vals, vals.length);
  }

  @Override
  public Double getMetric() {
    return Double.valueOf(vals[METRICPOS]);
  }

  /**
   * Gets the mflops.
   * 
   * @return the m flops
   */
  public long getMFlops() {
    return vals[0];
  }

  /**
   * Gets the lops.
   * 
   * @return the lops
   */
  public long getLops() {
    return vals[1];
  }

  /**
   * Gets the iops.
   * 
   * @return the iops
   */
  public long getIops() {
    return vals[2];
  }

}
