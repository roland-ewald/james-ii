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
import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory for creating Distribution objects.
 * 
 * @author Jan Himmelspach
 * @param <E>
 */
public abstract class DistributionFactory<E extends IDistribution> extends
    Factory<E> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5077251648358580692L;

  /**
   * Create and return a distribution. A distribution is always initialized with
   * default parameters here!
   * 
   * @param seed
   *          the seed
   * 
   * @return the E
   */
  public abstract E create(long seed);

  /**
   * Create and return a distribution. A distribution is always initialized with
   * default parameters here!
   * 
   * @param random
   *          the random
   * 
   * @return the E
   */
  public abstract E create(IRandom random);

  /**
   * Create and return a distribution. A distribution is always initialized with
   * default parameters here!
   * 
   * @param block
   *          the block
   * @param random
   *          the random
   * 
   * @return the E
   */
  public E create(ParameterBlock block, IRandom random) {
    return create(random);
  }

  @Override
  public E create(ParameterBlock block, Context context) {
    return create((IRandom) block.getSubBlockValue("RANDOM"));
  }

}
