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
 * The Class BinaryNode. <br/>
 * A node with two child nodes (left and right). Can be used as base for all
 * binary operations.
 * 
 * @author Jan Himmelspach
 */
public abstract class BinaryNode extends Node {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6932265932011439685L;

  /** The left child of this binary node. */
  private INode left;

  /** The right node of this binary node. */
  private INode right;

  /**
   * Create a new binary node with left and right as children.
   * 
   * @param left2
   *          the left
   * @param right2
   *          the right
   */
  public BinaryNode(INode left2, INode right2) {
    super();
    this.left = left2;
    this.right = right2;
  }

  /**
   * Return the left child.
   * 
   * @return the left
   */
  public INode getLeft() {
    return left;
  }

  /**
   * Return the right child.
   * 
   * @return the right
   */
  public INode getRight() {
    return right;
  }

  @SuppressWarnings("unchecked")
  @Override
  public <N extends INode> N calc(IEnvironment<?> cEnv) {
    ValueNode<?> lv = left.calc(cEnv);
    ValueNode<?> rv = right.calc(cEnv);
    N result = (N) calc(lv, rv);
    // System.out.println("bin op result "+result);
    return result;
  }

  /**
   * Calculate the binary node. This method is used from within the
   * {@link #calc(IEnvironment)} method. If evaluates the two passed nodes
   * according to the operation, and returns the computed result.
   * 
   * @param l
   *          the left node to be used
   * @param r
   *          the right node to be used
   * 
   * @return the result as a node
   */
  protected abstract INode calc(ValueNode<?> l, ValueNode<?> r);

  /**
   * Sets the left node of this binary node.
   * 
   * @param left
   *          the new left
   */
  public void setLeft(INode left) {
    this.left = left;
  }

  /**
   * Sets the right node of this binary node.
   * 
   * @param right
   *          the new right
   */
  public void setRight(INode right) {
    this.right = right;
  }

  /**
   * Gets the operation as string. The return value can be used to print a human
   * readable equation from the tree.
   * 
   * @return the operation as string
   */
  @Override
  public String getName() {
    return "*** noop ***";
  }

  @Override
  public List<INode> getChildren() {
    List<INode> result = new ArrayList<>();
    result.add(left);
    result.add(right);
    return result;
  }
}
