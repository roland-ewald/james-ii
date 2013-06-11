/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.parsetree;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.math.parsetree.variables.IEnvironment;

/**
 * The Class ValueNode.
 * 
 * A ValueNode holds a value of the given type V. If calc is executed and the
 * value type is not node the node as such will be returned. If the value type
 * is of type INode the return value of the value's calc method is returned
 * instead.
 * 
 * @param <V>
 *          the type of the value (e.g., String, Integer, ...)
 * @author Jan Himmelspach
 */
public class ValueNode<V> extends Node {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7894670474540343548L;

  /** The value. */
  private V value;

  /**
   * Instantiates a new value node.
   * 
   * @param val
   *          the val
   */
  public ValueNode(V val) {
    super();
    value = val;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {

    if (value instanceof INode) {
      return (N) ((INode) value).calc(cEnv);
    }
    return (N) this;
  }

  /**
   * Gets the value.
   * 
   * @return the value
   */
  public V getValue() {
    return value;
  }

  /**
   * Sets the value.
   * 
   * @param val
   * 
   */
  public void setValue(V val) {
    value = val;
  }

  /**
   * Checks if is double.
   * 
   * @return true, if is double
   */
  public boolean isDouble() {
    return value instanceof Double;
  }

  /**
   * Get the children of the node, if any.
   * 
   * @return the children
   */
  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>(super.getChildren());
    if (value instanceof INode) {
      result.add((INode) value);
    }
    return result;
  }

  /**
   * Determines whether the node deals with a {@link Long} value.
   * 
   * @return true if the value is of type {@link Long}
   */
  public boolean isLong() {
    return value instanceof Long;
  }
}
