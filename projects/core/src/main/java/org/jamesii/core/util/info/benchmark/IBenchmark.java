/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info.benchmark;

/**
 * The Interface IBenchmark. This interface has to be implemented by all
 * benchmarks which shall be used by the software to measure the performance of
 * computing hardware it is used on.<br/>
 * 
 * The benchmark will be executed by the software by the {@link #run()} method.
 * 
 * @author Jan Himmelspach
 */
public interface IBenchmark {

  /**
   * Run. Execute/Run the benchmark.
   * 
   * @return the benchmarking result
   */
  IBenchmarkingResult run();

}
