/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.Node;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents the root operation.
 * 
 * @author Oliver Röwer
 */
public class RootNode extends Node {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 268476818831079201L;

  /** The degree. */
  private INode degree;

  /** The node. */
  private INode number;

  /**
   * Instantiates a new root node.
   * 
   * @param degree
   *          the degree
   * @param number
   *          the number
   */
  public RootNode(INode degree, INode number) {
    this.degree = degree;
    this.number = number;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> vd = degree.calc(cEnv);
    ValueNode<?> vn = number.calc(cEnv);
    double d = ((Number) (vd).getValue()).doubleValue();
    double n = ((Number) (vn).getValue()).doubleValue();

    Double returnValue;
    if (Double.compare(d, 0.) == 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Math.pow(n, 1.0 / d);
    }

    return (N) new ValueNode<>(returnValue);
  }

  @Override
  public String toString() {
    return "root (" + degree + ", " + number + ")";
  }

  @Override
  public List<INode> getChildren() {
    ArrayList<INode> result = new ArrayList<>();
    result.add(degree);
    result.add(number);
    return result;
  }
}
