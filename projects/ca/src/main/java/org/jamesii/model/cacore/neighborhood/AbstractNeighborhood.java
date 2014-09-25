/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.cacore.neighborhood;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractNeighborhood implements INeighborhood {
  /**
   * 
   */
  private static final long serialVersionUID = 2309851131305723019L;

  /**
   * The cells in the neighborhood.
   */
  private final List<int[]> cells = new ArrayList<>();

  /**
   * The dimension the neighborhood was created for.
   */
  private final int dimensions;

  /**
   * The comment.
   */
  private final String comment;

  /**
   * Instantiates a new abstract neighborhood for the given dimensions.
   * 
   * @param dimensions
   *          the dimensions to create a neighborhood for
   * @param comment
   *          the neighborhoods comment
   */
  public AbstractNeighborhood(int dimensions, String comment) {
    this.dimensions = dimensions;
    this.comment = comment;
  }

  @Override
  public void addCell(int... cell) {
    // check cell dimensions to match initial dimensions
    if (cell == null || cell.length != getDimensions()) {
      throw new IllegalArgumentException("Provided cell dimension ("
          + (cell != null ? cell.length : "null")
          + ") does not fit the specified dimension (" + getDimensions()
          + ") this neighborhood is defined for.");
    }
    if (!containsCell(cell)) {
      cells.add(cell);
    }
  }

  @Override
  public int[] getCell(int cellIndex) {
    return cells.get(cellIndex);
  }

  @Override
  public int getCellCount() {
    return cells.size();
  }

  @Override
  public int getDimensions() {
    return dimensions;
  }

  @Override
  public boolean containsCell(int... cell) {
    if (cell == null || cell.length != getDimensions()) {
      return false;
    }
    for (int[] c : cells) {
      boolean contains = true;
      for (int i = 0; i < c.length; i++) {
        contains &= (c[i] == cell[i]);
      }
      if (contains) {
        return true;
      }
    }
    return false;
  }

  @Override
  public String getComment() {
    return comment;
  }

  @Override
  public Iterator<int[]> iterator() {
    return cells.iterator();
  }

}
