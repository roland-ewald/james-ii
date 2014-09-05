/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.info.benchmark.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.info.benchmark.IBenchmark;

/**
 * Basic factory for all factories that create benchmarking instances
 * 
 * @author Jan Himmelspach
 * 
 */
public abstract class BenchmarkFactory extends Factory<IBenchmark> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -310079751315801613L;

  /**
   * Creates an instance of a benchmark.
 * @param params
   *          parameters
 * @return benchmark
   */
  @Override
  public abstract IBenchmark create(ParameterBlock params, Context context);

}
