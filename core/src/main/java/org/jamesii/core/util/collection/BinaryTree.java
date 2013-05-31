/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection;

import org.jamesii.core.util.misc.Pair;

/**
 * Very simple binary tree. Uses an array internally. Has a fixed size but
 * provides methods to access the sibling, the parent and the children. <br/>
 * It does not check for out of bounds requests!
 * 
 * @author Jan Himmelspach
 * @param <T>
 *          Node type
 */
public class BinaryTree<T> {

  /**
   * The internal data of the tree.
   */
  private T[] data;

  @SuppressWarnings("unchecked")
  public BinaryTree(int size) {
    data = (T[]) new Object[size];
  }

  /**
   * Gets the value at the given position.
   * 
   * @param index
   *          the index
   * 
   * @return the t
   */
  public T get(int index) {
    return data[index];
  }

  /**
   * Sets the value at the given position.
   * 
   * @param index
   *          the index
   * @param value
   *          the value
   */
  public void set(int index, T value) {
    data[index] = value;
  }

  /**
   * Get the index of the parent node.
   * 
   * @param index
   * @return
   */
  public int getParentIndex(int index) {
    return (index + 1) / 2 - 1;
  }

  /**
   * Get the index of the sibling.
   * 
   * @param index
   * @return
   */
  public int getSiblingIndex(int index) {
    return (index % 2) == 0 ? index - 1 : index + 1;
  }

  /**
   * Get the indices of the children.
   * 
   * @param index
   * @return
   */
  public Pair<Integer, Integer> getChildren(int index) {
    return new Pair<>((index + 1) * 2 - 1, (index + 1) * 2);
  }

  public int size() {
    return data.length;
  }

}
