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
import org.jamesii.core.math.random.distributions.NormalDistribution;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * A node representing a normal distribution.
 * 
 * @author Jan Himmelspach
 */
public class NormalNode extends Node {

  private static final long serialVersionUID = 3198006525530547429L;

  /**
   * The random number generator (initialized with the default Java random, with
   * a random seed). Should be replaced by a controlled rng by the user via the
   * constructor.
   */
  private IRandom random = new JavaRandom();

  /** The distribution. */
  private IDistribution distrib = new NormalDistribution(random);

  /** The lower bound. */
  private INode lowerB;

  /** The upper bound. */
  private INode upperB;

  /**
   * Instantiates a new normal node.
   * 
   * @param val
   *          the val
   */
  public NormalNode(IRandom val) {
    super();
    random = val;
    distrib = new NormalDistribution(random);
  }

  /**
   * Instantiates a new normal node.
   * 
   * @param lowerBorder
   *          the lower border
   * @param upperBorder
   *          the upper border
   */
  public NormalNode(INode lowerBorder, INode upperBorder) {
    super();
    lowerB = lowerBorder;
    upperB = upperBorder;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {

    distrib =
        new NormalDistribution(random,
            ((ValueNode<Double>) lowerB.calc(cEnv)).getValue(),
            ((ValueNode<Double>) upperB.calc(cEnv)).getValue());

    return (N) new ValueNode<>(distrib.getRandomNumber());
  }

  @Override
  public String toString() {
    return distrib.toString();
  }

  @Override
  public List<? extends INode> getChildren() {
    ArrayList<INode> result = new ArrayList<>();
    result.add(lowerB);
    result.add(upperB);
    return result;
  }
}
