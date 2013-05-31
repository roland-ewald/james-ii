/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.random;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;
import org.jamesii.core.math.random.distributions.AbstractDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * A node representing a distribution. Can only be initialized once in the
 * beginning. No boundaries etc can be computed dynamically.
 * 
 * @author Jan Himmelspach
 */
public class DistributionNode extends Node {

  private static final long serialVersionUID = 1072250473142511961L;

  /**
   * The random number generator (initialized with the default Java random, with
   * a random seed). Should be replaced by a controlled rng by the user via the
   * constructor.
   */
  private IRandom random = new JavaRandom();

  /** The distribution. */
  private AbstractDistribution distrib = new UniformDistribution(random);

  /**
   * Instantiates a new uniform node.
   * 
   * @param rng
   *          the random number generator to be used
   * @param distrib
   *          the distribution to be used
   */
  public DistributionNode(IRandom rng, AbstractDistribution distrib) {
    super();
    random = rng;
    this.distrib = distrib;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    return (N) new ValueNode<>(distrib.getRandomNumber());
  }

  @Override
  public String toString() {
    return distrib.toString();
  }

}
