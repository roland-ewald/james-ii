/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

/**
 * 
 * org.jamesii.core.simulation.partitionizer.ConstraintConflictException -
 * 
 * Description: Exception to indicate that a certain constraint is invalid in
 * the given context and will be ignored
 * 
 * Created on 22:56:43 02.07.2005
 * 
 * @author Roland Ewald
 */
public class ConstraintConflictException extends Exception {

  /**
   * Serialisation ID
   */
  static final long serialVersionUID = -9022933703312517568L;

  /**
   * Index of *one* node on which the constraint cannot be applied
   */
  private int indexOfStrugglerNode;

  /**
   * Standard constructor
   * 
   * @param indexOfStrugglerNode
   */
  public ConstraintConflictException(int indexOfStrugglerNode) {
    super();
    this.indexOfStrugglerNode = indexOfStrugglerNode;
  }

  /**
   * @return Index of *one* struggler node
   */
  public int getIndexOfStrugglerNode() {
    return indexOfStrugglerNode;
  }
}
