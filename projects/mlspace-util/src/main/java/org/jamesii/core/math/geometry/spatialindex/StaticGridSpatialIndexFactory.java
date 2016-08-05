/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.spatialindex;

import java.io.Serializable;
import java.util.logging.Level;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * @author Arne Bittig
 *
 */
public class StaticGridSpatialIndexFactory extends SpatialIndexFactory {

  private static final long serialVersionUID = -6368465172761864172L;

  /** Default number of grid cells */
  public static final int DEFAULT_NUM_GRID_CELLS = 64;

  /**
   * Identifier for the grid cell size parameter (may be omitted if
   * {@link #NUMBER_OF_GRID_CELLS} is given)
   */
  public static final String GRID_CELL_SIDE_LENGTH = "GridCellSideLength";

  /**
   * Identifier for the grid cell amount parameter (overrides
   * {@link #GRID_CELL_SIDE_LENGTH} if both are given)
   */
  public static final String NUMBER_OF_GRID_CELLS = "NumGridCells";

  /** Identifier for the system boundary parameter */
  public static final String BOUNDING_SHAPE = "SurroundingShape";

  @Override
  public <C extends IShapedComponent & Serializable> ISpatialIndex<C> createDirect(
      ParameterBlock params) {
    if (params == null || params.getValue() == null) {
      return new StaticGridSpatialIndex<>(null, DEFAULT_NUM_GRID_CELLS);
    }

    IShape surround =
        params.getSubBlockValue(StaticGridSpatialIndexFactory.BOUNDING_SHAPE);
    Integer numGridCells = params.getSubBlockValue(NUMBER_OF_GRID_CELLS);
    Double gridCellLength = params.getSubBlockValue(GRID_CELL_SIDE_LENGTH);
    if (numGridCells != null) {
      if (gridCellLength != null) {
        ApplicationLogger.log(Level.WARNING, "Grid cell number _and_  "
            + "side length given for grid-based spatial index; "
            + "using only the former");
      }
      return new StaticGridSpatialIndex<>(surround, numGridCells);
    } else if (gridCellLength != null) {
      return new StaticGridSpatialIndex<>(surround, gridCellLength);
    } else {
      return new StaticGridSpatialIndex<>(surround, DEFAULT_NUM_GRID_CELLS);
    }

  }
}
