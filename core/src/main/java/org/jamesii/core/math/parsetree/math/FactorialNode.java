/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.math;

import org.jamesii.core.math.Factorial;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represents the factorial operation.
 * 
 * @author Oliver Röwer
 */
public class FactorialNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = 6361349500681391362L;

  /**
   * Instantiates a new factorial node.
   * 
   * @param val
   *          the val
   */
  public FactorialNode(INode val) {
    super(val);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    int x = ((Number) (v).getValue()).intValue();

    double returnValue = 1.0;
    // check for: x < 0
    // and for: given value is not a natural number (whereas x is)
    if (x < 0 || Double.compare(((Number) v.getValue()).doubleValue(), x) != 0) {
      returnValue = Double.NaN;
    } else {
      returnValue = Factorial.quickFac(x);
    }

    return (N) new ValueNode<>(returnValue);
  }

}
