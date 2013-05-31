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
 * Returns Euler's number e raised to the power of a double value.
 * 
 * @author Jan Himmelspach
 */
public class ExpNode extends ValueNode<INode> {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -6450914118108104773L;

  /**
   * Instantiates a new exp node.
   * 
   * @param val
   *          the val
   */
  public ExpNode(INode val) {
    super(val);
  }

  /**
   * Gets the function name as string. The return value can be used to print a
   * human readable equation from the tree.
   * 
   * @return the operation as string
   */
  @Override
  public String getName() {
    return "exp";
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> v = getValue().calc(cEnv);
    return (N) new ValueNode<>(
        Math.exp(Double.valueOf(((Number) (v).getValue()).doubleValue())));

  }
}
