/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees.binary;

/**
 * Vertex of a binary tree.
 * 
 * @author Jan Himmelspach
 * @param <NL>
 *          type of the adjacent vertices
 */
public class BinaryTreeVertex<NL> {

  /** The value of the node. */
  private NL value;

  /** The left son. */
  private BinaryTreeVertex<NL> left;

  /** The right son. */
  private BinaryTreeVertex<NL> right;

  /** The parent node. */
  private BinaryTreeVertex<NL> parent;

  /**
   * Gets the value of the node.
   * 
   * @return the value
   */
  public NL getValue() {
    return value;
  }

  /**
   * Sets the value of the node.
   * 
   * @param value
   *          the new value
   */
  public void setValue(NL value) {
    this.value = value;
  }

  /**
   * Gets the left son.
   * 
   * @return the left son
   */
  public BinaryTreeVertex<NL> getLeft() {
    return left;
  }

  /**
   * Sets the left son.
   * 
   * @param left
   *          the new left son
   */
  public void setLeft(BinaryTreeVertex<NL> left) {
    this.left = left;
  }

  /**
   * Gets the right son.
   * 
   * @return the right son
   */
  public BinaryTreeVertex<NL> getRight() {
    return right;
  }

  /**
   * Sets the right son.
   * 
   * @param right
   *          the new right son
   */
  public void setRight(BinaryTreeVertex<NL> right) {
    this.right = right;
  }

  /**
   * Gets the parent node.
   * 
   * @return the parent node
   */
  public BinaryTreeVertex<NL> getParent() {
    return parent;
  }

  /**
   * Sets the parent node.
   * 
   * @param parent
   *          the new parent node
   */
  public void setParent(BinaryTreeVertex<NL> parent) {
    this.parent = parent;
  }

}
