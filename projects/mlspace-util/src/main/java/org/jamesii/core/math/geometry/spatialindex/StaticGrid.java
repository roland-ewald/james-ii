/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.spatialindex;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jamesii.core.math.geometry.SpatialException;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeUtils;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * @author Arne Bittig
 */
public class StaticGrid implements java.io.Serializable {

  private static final double DELTA = 1e-12;

  /** Serialization ID */
  private static final long serialVersionUID = 2487390383829911936L;

  /** Number of dimensions */
  private final int dim;

  /** size of the grid in each dimension */
  private final int gridSize[];

  /** distance of adjacent grid cells in grid cell list for each dimension */
  private final int cellIndDist[];

  /** minimum coordinates */
  private final double minGridPos[];

  // maximum coordinates not needed, equal to minGridPos + gridExt

  /** difference between maximum and minimum coordinates */
  private final double gridExt[];

  /** actual grid, i.e. array of shapes */
  private final List<AxisAlignedBox> grid;

  private final boolean toroidal;

  /**
   * Create grid given system boundaries
   *
   * @param surrounding
   *          shape defining system boundaries
   * @param sideLength
   *          target side length of a grid cell
   */
  public StaticGrid(IShape surrounding, double sideLength) {
    AxisAlignedBox sbb = (AxisAlignedBox) surrounding.boundingBox();
    ShapeUtils.SplitResult splitRes = ShapeUtils.splitAllDim(sbb, sideLength);
    grid = splitRes.getBoxes();
    gridSize = splitRes.getNumberPerDimension();
    dim = gridSize.length;
    cellIndDist = new int[gridSize.length];
    int cumProd = 1;
    for (int i = dim - 1; i >= 0; i--) { // NOSONAR: how is System.arraycopy
      // supposed to help here?!
      cellIndDist[i] = cumProd;
      cumProd *= gridSize[i];
    }
    assert grid.size() == cumProd;

    minGridPos = sbb.getCenter().minus(sbb.getMaxExtVector()).toArray();

    this.toroidal = surrounding instanceof TorusSurface;
    if (this.toroidal) {
      gridExt = ((TorusSurface) surrounding) // NOSONAR: not unchecked
          .getVecFac().getPeriod().toArray();
    } else {
      gridExt = sbb.getMaxExtVector().times(2.).toArray();
    }

  }

  /**
   * Get the grid cell corresponding to a given index (e.g. as returned by
   * {@link #findGridCellsInRangeIndices(IShape)})
   *
   * @param i
   *          grid block/cell index
   * @return grid cell shape
   */
  public final AxisAlignedBox get(int i) {
    return grid.get(i);
  }

  /**
   * @return number of blocks/cells in this grid
   */
  public final int getNumOfGridCells() {
    return grid.size();
  }

  /**
   * Size of the grid (in blocks/cells)
   *
   * @return number of grid blocks in each dimensions
   */
  public final int[] getGridSize() {
    return gridSize; // NOSONAR: We trust the user not to make changes to
    // the returned value
  }

  /**
   * @return size of each grid block/cell in each dimension
   */
  public final double[] getCellSize() {
    return grid.get(0).getMaxExtVector().times(2.).toArray();
  }

  /**
   * Get indices of grid cells in range of given component (not necessarily all
   * actually overlapped grid cells, but rather all grid cells overlapped by the
   * component's bounding box)
   *
   * @param shape
   *          Shape for which to find overlapped grid cells
   * @return List of grid cells (identified by their Integer index)
   */
  public List<Integer> findGridCellsInRangeIndices(IShape shape) {
    IPositionVector cCen = shape.getCenter();
    IDisplacementVector maxExtVector = shape.getMaxExtVector();
    int[] iMin = findGridCellNDIndex(cCen.minus(maxExtVector));
    int[] iMax = findGridCellNDIndex(cCen.plus(maxExtVector));
    // if (toroidal) { checkToroidalProblem(maxExtVector); }
    int numCells = 1;
    for (int i = 0; i < iMin.length; i++) {
      if (iMin[i] <= iMax[i]) {
        numCells *= iMax[i] - iMin[i] + 1;
      } else {
        assert toroidal; // only with periodic boundaries
        numCells *= iMax[i] - iMin[i] + gridSize[i] + 1;
      }
    }

    List<Integer> bbOvGCIdx = new ArrayList<>(numCells);
    gridCellsFromToRec(iMin, iMax, new int[iMin.length], 0, bbOvGCIdx);
    return bbOvGCIdx;
  }

  /**
   * Convert n-dimensional indices to linear index
   *
   * @param ndInd
   *          Position in n-dimensional array
   * @return Position in 1d (flattened) version of the array
   */
  protected int linearIndex(int... ndInd) {
    assert ndInd.length == dim;
    int ind = 0;
    for (int i = 0; i < dim; i++) {
      ind += ndInd[i] * cellIndDist[i];
    }
    return ind;
  }

  /**
   * N-dimensional index of grid cell that includes given position
   *
   * @param pos
   *          Position vector to find in grid
   * @return Index of cell including pos in n-dimensional grid
   */
  private int[] findGridCellNDIndex(IPositionVector pos) {
    int[] ndInd = new int[dim];
    for (int i = 0; i < dim; i++) {
      int d = i + 1;
      double r = (pos.get(d) - minGridPos[i]) / gridExt[i];
      if (r < -DELTA || r > 1. + DELTA) {
        throw new SpatialException("position out of bounds: " + pos);
      }
      ndInd[i] =
          r >= 1. ? gridSize[i] - 1 : r < 0 ? 0 : (int) (gridSize[i] * r);
    }
    return ndInd;
  }

  /**
   * helper function for the recursion* step in
   * {@link #findGridCellsInRangeIndices(org.jamesii.core.math.geometry.IShapedComponent)}
   * (* recursion over the spatial dimensions)
   *
   * @param from
   * @param to
   * @param fixed
   * @param iDim
   * @param gridCellIndices
   */
  private void gridCellsFromToRec(int[] from, int[] to, int[] fixed, int iDim,
      Collection<Integer> gridCellIndices) {
    if (iDim < from.length) {
      if (from[iDim] <= to[iDim]) {
        for (int i = from[iDim]; i <= to[iDim]; i++) {
          fixed[iDim] = i;
          gridCellsFromToRec(from, to, fixed, iDim + 1, gridCellIndices);
        }
      } else {
        assert toroidal; // periodic boundaries
        for (int i = 0; i <= to[iDim]; i++) {
          fixed[iDim] = i;
          gridCellsFromToRec(from, to, fixed, iDim + 1, gridCellIndices);
        }
        for (int i = from[iDim]; i < gridSize[iDim]; i++) {
          fixed[iDim] = i;
          gridCellsFromToRec(from, to, fixed, iDim + 1, gridCellIndices);
        }
      }

    } else {
      gridCellIndices.add(linearIndex(fixed));
    }
  }

}
