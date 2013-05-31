/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents the area cotangens hyperbolicus operation.
 * 
 * @author Oliver Röwer
 */
public class ArCothNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -360636803733096444L;

  /**
   * Instantiates a new arcoth node.
   * 
   * @param val
   *          the val
   */
  public ArCothNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    double x = ((Number) (v).getValue()).doubleValue();

    Double returnValue;
    if (Double.compare(Math.abs(x), 1.0) <= 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = 0.5 * Math.log((x + 1.0) / (x - 1.0));
    }

    return (N) new ValueNode<>(returnValue);
  }

}
