/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.core.util.info.benchmark.simple;

import org.jamesii.core.factories.Context;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.info.benchmark.IBenchmark;
import org.jamesii.core.util.info.benchmark.plugintype.BenchmarkFactory;

/**
 * A factory for creating SimpleBenchmark objects.
 * 
 * @author Jan Himmelspach
 */
public class SimpleBenchmarkFactory extends BenchmarkFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 3661151579615236568L;

  @Override
  public IBenchmark create(ParameterBlock params, Context context) {
    return new SimpleBenchmark();
  }

}
