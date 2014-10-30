/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.distributions.plugintype;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating Distribution objects.
 * 
 * @author Jan Himmelspach
 * @param <D>
 *          Distribution type
 */
public abstract class DistributionFactory<D extends IDistribution> extends
    Factory<D> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5077251648358580692L;

  /**
   * Create and return a distribution. A distribution is always initialized with
   * default parameters here!
   * 
   * Creates a default random number generator (usually {@link JavaRandom}) with
   * given seat. Prefer explicit rng specification as in
   * {@link #create(IRandom)}.
   * 
   * @param seed
   *          the seed
   * @return Distribution
   */
  @Deprecated
  public abstract D create(long seed);

  /**
   * Create and return a distribution. A distribution is always initialized with
   * default parameters here!
   * 
   * @param random
   *          Random number generator
   * @return Distribution
   */
  public abstract D create(IRandom random);

  /**
   * Create and return a distribution with specific parameters
   * 
   * @param block
   *          Parameters
   *          Random number generator
   * @return Distribution
   */
  public D create(ParameterBlock block, IRandom random) {
    return create(random);
  }

  @Override
  public D create(ParameterBlock block, Context context) {
    return create(block, (IRandom) block.getSubBlockValue("RANDOM"));
  }

}
