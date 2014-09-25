/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore.neighborhood;

/**
 * Defines a von Neumann neighborhood for a given dimension. Supported
 * dimensions are 1,2 and 3;
 * 
 * @author Stefan Rybacki
 * 
 */
public class NeumannNeighborhood extends AbstractNeighborhood {

  /**
   * Instantiates a new neumann neighborhood.
   * 
   * @param dimensions
   *          the dimensions
   */
  public NeumannNeighborhood(int dimensions) {
    this(dimensions, null);
  }

  /**
   * Instantiates a new von Neumann neighborhood for the given dimensions.
   * Supported dimensions are 1,2 and 3;
   * 
   * @param dimensions
   *          the dimensions the neighborhood should be created for
   * @param comment
   *          the neighborhood's comment
   */
  public NeumannNeighborhood(int dimensions, String comment) {
    super(dimensions, comment);
    if (dimensions < 1 || dimensions > 3) {
      throw new IllegalArgumentException("Dimension must be between 1 and 3");
    }

    if (dimensions == 1) {
      super.addCell(-1);
      super.addCell(1);
    } else if (dimensions == 2) {
      super.addCell(-1, 0);
      super.addCell(0, -1);
      super.addCell(1, 0);
      super.addCell(0, 1);
    } else if (dimensions == 3) {
      super.addCell(-1, 0, 0);
      super.addCell(0, -1, 0);
      super.addCell(1, 0, 0);
      super.addCell(0, 1, 0);

      super.addCell(0, 0, -1);
      super.addCell(0, 0, +1);
    }

  }

  /**
   * Checks if given dimension is supported.
   * 
   * @param dimension
   *          the dimension to check
   * 
   * @return true, if dimension is supported
   */
  public static boolean isDimensionSupported(int dimension) {
    return (dimension >= 1 && dimension <= 3);
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    builder.append("Neumann Neighborhood:\n");

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
