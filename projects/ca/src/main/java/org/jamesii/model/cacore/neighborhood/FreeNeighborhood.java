/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore.neighborhood;

/**
 * Defines a free customizable neighborhood.
 * 
 * @author Stefan Rybacki
 * 
 */
public class FreeNeighborhood extends AbstractNeighborhood {

  /**
   * Instantiates a new free neighborhood.
   * 
   * @param dimensions
   *          the dimensions
   */
  public FreeNeighborhood(int dimensions) {
    this(dimensions, null);
  }

  /**
   * Instantiates a new free neighborhood.
   * 
   * @param dimensions
   *          the dimensions
   * @param comment
   *          the comment (can be null)
   */
  public FreeNeighborhood(int dimensions, String comment) {
    super(dimensions, comment);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Free Neighborhood:\n");

    builder.append("[\n");
    for (int i = 0; i < getCellCount(); i++) {
      int[] cell = getCell(i);
      builder.append("  ( ");
      for (int c : cell) {
        builder.append(c);
        builder.append(" ");
      }
      builder.append(")\n");
    }
    builder.append("]");
    return builder.toString();
  }
}
