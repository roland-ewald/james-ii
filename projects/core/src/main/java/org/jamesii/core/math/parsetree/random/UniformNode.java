/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.random;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;
import org.jamesii.core.math.random.distributions.IDistribution;
import org.jamesii.core.math.random.distributions.UniformDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * A node representing a uniform distribution.
 * 
 * @author Jan Himmelspach
 */
public class UniformNode extends Node {

  private static final long serialVersionUID = 6072032484175805861L;

  /**
   * The random number generator (initialized with the default Java random, with
   * a random seed). Should be replaced by a controlled rng by the user via the
   * constructor.
   */
  private IRandom random = new JavaRandom();

  /** The distrib. */
  private IDistribution distrib = new UniformDistribution(random);

  /** The lower b. */
  private INode lowerB;

  /** The upper b. */
  private INode upperB;

  /**
   * Instantiates a new uniform node.
   * 
   * @param rng
   *          the random number generator to be used
   */
  public UniformNode(IRandom rng) {
    super();
    random = rng;
    distrib = new UniformDistribution(random);
  }

  /**
   * Instantiates a new uniform node.
   * 
   * @param lowerBorder
   *          the lower border
   * @param upperBorder
   *          the upper border
   */
  public UniformNode(INode lowerBorder, INode upperBorder) {
    super();
    lowerB = lowerBorder;
    upperB = upperBorder;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {

    distrib =
        new UniformDistribution(random,
            ((ValueNode<Double>) lowerB.calc(cEnv)).getValue(),
            ((ValueNode<Double>) upperB.calc(cEnv)).getValue());

    return (N) new ValueNode<>(distrib.getRandomNumber());
  }

  // // @Override
  // public Object calc(List<INode> nodes) {
  // distrib = new UniformDistribution(random, (Double) nodes.get(0), (Double)
  // nodes.get(1));
  //
  // return distrib.getRandomNumber();
  // }

  @Override
  public String toString() {
    return distrib.toString();
  }

  @Override
  public List<INode> getChildren() {
    ArrayList<INode> result = new ArrayList<>();
    result.add(lowerB);
    result.add(upperB);
    return result;
  }
}
