/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore.neighborhood;

/**
 * Defines the Moore neighborhood for the dimensions 1,2 and 3.
 * 
 * @author Stefan Rybacki
 * 
 */
public class MooreNeighborhood extends AbstractNeighborhood {

  /**
   * 
   */
  private static final long serialVersionUID = 3831502545998868211L;

  /**
   * Instantiates a new moore neighborhood.
   * 
   * @param dimensions
   *          the dimensions
   */
  public MooreNeighborhood(int dimensions) {
    this(dimensions, null);
  }

  /**
   * Instantiates a new Moore neighborhood for the given dimensions. Supported
   * are 1,2 and 3.
   * 
   * @param dimensions
   *          the dimensions the Moore neighborhood is created for
   * @param comment
   *          the neighborhoods comment
   */
  public MooreNeighborhood(int dimensions, String comment) {
    super(dimensions, comment);
    if (dimensions < 1 || dimensions > 3) {
      throw new IllegalArgumentException("Dimension must be between 1 and 3");
    }

    if (dimensions == 1) {
      // behave like neumann neighborhood
      super.addCell(-1);
      super.addCell(1);
    }
    if (dimensions == 2) {
      super.addCell(-1, 0);
      super.addCell(-1, 1);
      super.addCell(0, -1);
      super.addCell(1, -1);
      super.addCell(1, 0);
      super.addCell(1, 1);
      super.addCell(0, 1);
      super.addCell(-1, -1);
    } else if (dimensions == 3) {
      for (int z = -1; z < 2; z++) {
        super.addCell(-1, 0, z);
        super.addCell(-1, 1, z);
        super.addCell(0, -1, z);
        super.addCell(1, -1, z);
        super.addCell(1, 0, z);
        super.addCell(1, 1, z);
        super.addCell(0, 1, z);
        super.addCell(-1, -1, z);
      }
      super.addCell(0, 0, -1);
      super.addCell(0, 0, +1);
    }
  }

  /**
   * Checks if the given dimension is supported.
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
    builder.append("Moore Neighborhood:\n");

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
