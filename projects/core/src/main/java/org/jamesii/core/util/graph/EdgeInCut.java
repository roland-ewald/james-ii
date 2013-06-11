/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * Defines an edge in the cut of a partitioning. Consider the graph G=(V,E) with
 * V=(v_1,v_2) and E={{v_1,v_2}}, i.e. this is a two-node graph with an edge
 * between the two vertices. If a partitioning of this graph will assign v_1 to
 * partition (block) 1 and v_2 to partition (block) 2, then e will be an edge in
 * the cut.
 * 
 * @author Roland Ewald
 */
public class EdgeInCut {

  /** Original index of child. */
  private final int childIndex;

  /** PArtition block index of the child. */
  private final int childPartition;

  /** Original index of parent. */
  private final int parentIndex;

  /** Partition block index of the parent. */
  private final int parentPartition;

  /**
   * Standard constructor.
   * 
   * @param childIndex
   *          the child index
   * @param parentIndex
   *          the parent index
   * @param childPartition
   *          the child partition
   * @param parentPartition
   *          the parent partition
   */
  public EdgeInCut(int childIndex, int parentIndex, int childPartition,
      int parentPartition) {
    this.childIndex = childIndex;
    this.childPartition = childPartition;
    this.parentIndex = parentIndex;
    this.parentPartition = parentPartition;
  }

  /**
   * To string.
   * 
   * @return the string
   * 
   * @see java.lang.Object#toString()
   */
  @Override
  public String toString() {
    return childIndex + "(" + childPartition + ");" + parentIndex + "("
        + parentPartition + ")";
  }
}