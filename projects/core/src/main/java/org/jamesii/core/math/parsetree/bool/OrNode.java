/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree.bool;

import org.jamesii.core.math.parsetree.BinaryNode;
import org.jamesii.core.math.parsetree.INode;
import org.jamesii.core.math.parsetree.ValueNode;
import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * Node that represent a boolean "or" operation. Thus
 * {@link #calc(org.jamesii.core.math.parsetree.variables.IEnvironment)} returns
 * a ValueNode<Boolean> with a value of <code>true</code> if the "or" operation
 * on the two nodes returns <code>true</code>, otherwise a ValueNode<Boolean>
 * with a <code>false</code> value is returned. <br>
 * Child nodes can be of type <code>Boolean</code> or they can be of any
 * <code>Number</code> type. In this case a value > 0 is considered to be
 * equivalent to <code>true</code>, a value <= 0 is considered to be
 * <code>false</code>.
 * 
 * @author Jan Himmelspach
 */
public class OrNode extends BinaryNode {

  /**
   * The constant serial version id.
   */
  private static final long serialVersionUID = -4516371443868754187L;

  /**
   * Instantiates a new or node.
   * 
   * @param left
   *          the left
   * @param right
   *          the right
   */
  public OrNode(INode left, INode right) {
    super(left, right);
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> lv = getLeft().calc(cEnv);
    Boolean blv = null;
    Boolean brv = null;
    
    if (lv.getValue() instanceof Boolean) {
      blv = (Boolean) lv.getValue();
    } else if (lv.getValue() instanceof Number) {
      blv =
          Double.valueOf(((Number) lv.getValue()).doubleValue()).compareTo(0.) > 0;
    }
    if (blv != null && blv) {
      return (N) new ValueNode<>(true);
    }
    
    ValueNode<?> rv = getRight().calc(cEnv);

    if (rv.getValue() instanceof Boolean) {
      brv = (Boolean) rv.getValue();
    } else if (rv.getValue() instanceof Number) {
      brv =
          Double.valueOf(((Number) rv.getValue()).doubleValue()).compareTo(0.) > 0;
    }

    if ((blv == null) || (brv == null)) {
      return null;
    }
    
    // System.out.println("bin op result "+result);
    return (N) new ValueNode<>(brv);
  }

  @Override
  public String getName() {
    return "OR";
  }

  @Override
  protected INode calc(ValueNode<?> l, ValueNode<?> r) {
    throw new RuntimeException("Invalid!!!!!");
  }

}
