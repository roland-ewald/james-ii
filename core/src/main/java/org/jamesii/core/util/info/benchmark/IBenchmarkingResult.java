/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info.benchmark;

import java.io.Serializable;

/**
 * The interface IBenchmarkingResult. Benchmarking plug-ins have to use this
 * interface to return the benchmarking results. The {@link #getMetric()} method
 * should return the summarized benchmark - depending on the benchmark used this
 * can be the sum/mean/or whatever of the partial benchmarks. The more different
 * things a benchmarks measures the better - additional information should be
 * contained in the objects implementing this interface whenever possible. It is
 * the responsibility of a user to handle the results with care and to apply
 * either the getMetric benchmark value or any other value contained in here for
 * the interpretation and normalization of the results achieved.
 * 
 * @author Jan Himmelspach
 */
public interface IBenchmarkingResult extends Serializable {

  /**
   * Gets the (overall) metric.
   * 
   * @return the metric
   */
  Double getMetric();

}
