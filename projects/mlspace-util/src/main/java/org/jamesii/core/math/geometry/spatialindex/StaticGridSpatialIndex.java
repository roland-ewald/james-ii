/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.spatialindex;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.util.collection.CollectionUtils;

/**
 * Grid-based spatial index where each grid cell can be overlapped by multiple
 * shaped components (and each component can overlap multiple grid cells). The
 * grid is static, i.e. there is no splitting of grid cells in relation to
 * occupancy or otherwise.
 *
 * @author Arne Bittig
 *
 * @param <C>
 *          type of shaped component to index
 */
public class StaticGridSpatialIndex<C extends IShapedComponent> implements
ISpatialIndex<C> {

  private static final long serialVersionUID = 2675698541867943434L;

  /** Components overlapping each grid cell (order as in #grid) */
  private List<Collection<C>> compsInGridCell;

  private final Map<C, IPositionVector> registeredPositions =
      new LinkedHashMap<>();

  private final Map<C, Collection<Integer>> overlappedGridCells =
      new LinkedHashMap<>();

  /** target grid cell side length for lazy initialization */
  private double targetGridCellSideLength;

  /** target grid cell number for lazy initialization */
  private final int targetGridCellAmount;

  /** The boundary-defining shape */
  private IShape surrounding;

  /** The actual grid on which all operations are performed */
  private StaticGrid grid;

  private static final double DELTA = 1e-12;

  /**
   * Grid-based spatial index with possibly multiple components in one grid
   * block.
   *
   * @param surrounding
   *          Shape providing system boundaries (if null, first registered shape
   *          is used)
   * @param numGridCells
   *          target number of cells in the (automatically constructed) grid
   */
  public StaticGridSpatialIndex(IShape surrounding, int numGridCells) {
    this(surrounding, numGridCells, 0); // TODO: tolerance parameter
  }

  /**
   * Grid-based spatial index with possibly multiple components in one grid
   * block. Caution: Another constructor takes an int as a second argument, so
   * make sure to write, e.g., 2. or 2.0 instead of 2 for a desired side length
   * of 2.
   *
   * @param surrounding
   *          Shape providing system boundaries (if null, first registered shape
   *          is used)
   * @param gridCellSideLength
   *          target side length of cells in the (automatically constructed)
   *          grid (actual side length may be slightly larger)
   */
  public StaticGridSpatialIndex(IShape surrounding, double gridCellSideLength) {
    this(surrounding, 0, gridCellSideLength);
  }

  private StaticGridSpatialIndex(IShape surrounding, int gridCellAmount,
      double gridCellSideLength) {
    targetGridCellAmount = gridCellAmount;
    targetGridCellSideLength = gridCellSideLength;
    if (surrounding != null) {
      init(surrounding);
    }
  }

  @Override
  @SuppressWarnings("unchecked")
  public final boolean init(IShape surroundingShape) {
    if (grid != null) {
      throw new IllegalArgumentException("Grid already initialized");
    }
    initGrid(surroundingShape);
    this.surrounding = surroundingShape;
    this.compsInGridCell =
        Arrays
        .asList((Collection<C>[]) new Collection[grid.getNumOfGridCells()]);
    return true;
  }

  private void initGrid(IShape newSurrounding) {
    if (targetGridCellSideLength <= 0) {
      if (targetGridCellAmount <= 0) {
        throw new IllegalStateException("Neither grid cell side length"
            + " nor grid cell number (amount) have a valid value.");
      } else {
        targetGridCellSideLength =
            StaticGridSpatialIndex.getUsefulGridCellSideLength(newSurrounding
                .getSize(), targetGridCellAmount, newSurrounding.getCenter()
                .getDimensions());
      }
    }
    grid = new StaticGrid(newSurrounding, targetGridCellSideLength);
  }

  /**
   * Get useful side length for roughly square grid cells given size and target
   * number of grid cells to split this volume into.
   *
   * @param size
   *          Area / volume
   * @param numGridCells
   *          Number of grid cells
   * @param dims
   *          dimensions
   * @return useful grid cell side length
   */
  public static double getUsefulGridCellSideLength(double size,
      int numGridCells, int dims) {
    return Math.pow(size / numGridCells, 1. / dims);
  }

  @Override
  public List<C> collidingComps(C c) {
    List<C> collComps = new ArrayList<>();
    IShape cShape = c.getShape();
    for (Integer gci : getOverlappedGridCells(c)) {
      for (C otherComp : compsInGridCell.get(gci)) {
        if (otherComp != c
            && cShape.getRelationTo(otherComp.getShape(), DELTA).isCollision()
            && !collComps.contains(otherComp)) {
          collComps.add(otherComp);
        }
      }
    }
    return collComps;
  }

  @Override
  public List<C> collidingComps(C c, Collection<C> otherCs) {
    Collection<Integer> ovGCComp;
    ovGCComp = getOverlappedGridCells(c);
    List<C> collComps = new ArrayList<>();
    IShape cShape = c.getShape();
    for (C otherComp : otherCs) {
      if (otherComp != c
          && CollectionUtils.sortedCollectionsIntersect(ovGCComp,
              getOverlappedGridCells(otherComp)) != null
              && cShape.getRelationTo(otherComp.getShape(), DELTA).isCollision()) {
        collComps.add(otherComp);
      }
    }
    return collComps;
  }

  /**
   * Get grid cells overlapped by given component, by lookup in internal map if
   * component is registered, or by recalculation if not
   *
   * @param c
   *          Component
   * @return Overlapped grid cells' Integer indices
   */
  private Collection<Integer> getOverlappedGridCells(C c) {
    Collection<Integer> ovGCComp;
    if (registeredPositions.containsKey(c)) {
      ovGCComp = overlappedGridCells.get(c);
    } else {
      ovGCComp = findOverlappedGridCells(c);
    }
    return ovGCComp;
  }

  @Override
  public IShape getBoundaries() {
    return surrounding;
  }

  @Override
  public boolean isOutOfBounds(C c) {
    return surrounding.getRelationTo(c.getShape()) != ShapeRelation.SUPERSET;
  }

  @Override
  public Collection<C> notUpToDate() {
    List<C> rv = new ArrayList<>();
    for (Map.Entry<C, IPositionVector> e : registeredPositions.entrySet()) {
      IPositionVector actPos = e.getKey().getPosition();
      IPositionVector regPos = e.getValue();
      if (actPos.distanceSquared(regPos) > DELTA) {
        rv.add(e.getKey());
      }
    }
    return rv;
  }

  @Override
  public void registerNewEntity(C c) {
    if (registeredPositions.containsKey(c)) {
      throw new IllegalStateException("Component already registered: " + c);
    }
    registerNewPosition(c);
  }

  /**
   * Update stored position as well as "comp->grid cells overlapped" map and
   * "grid cell->comps overlapping it" maps
   *
   * @param c
   *          Component
   */
  protected void registerNewPosition(C c) {
    registeredPositions.put(c, c.getPosition().copy());
    List<Integer> ovGCs = findOverlappedGridCells(c);
    overlappedGridCells.put(c, ovGCs);
    for (Integer gci : ovGCs) {
      compsInGridCell.get(gci).add(c);
    }
  }

  /**
   * (Re-)Calculate which grid cells a given component overlaps (does NOT add
   * parameter to list of components in each respective grid cell!)
   *
   * @param c
   *          Component
   * @return Overlapped grid cells identified by their Integer indices
   */
  private List<Integer> findOverlappedGridCells(C c) {
    Collection<Integer> gCIRIs = grid.findGridCellsInRangeIndices(c.getShape());
    List<Integer> ovGCs = new ArrayList<>(gCIRIs.size());
    for (Integer gci : gCIRIs) {
      AxisAlignedBox gc = grid.get(gci);
      ShapeRelation rel = c.getShape().getRelationTo(gc, DELTA);
      if (rel.isCollision()) {
        ovGCs.add(gci);
        if (compsInGridCell.get(gci) == null) {
          compsInGridCell.set(gci, new LinkedHashSet<C>());
        }
      }
    }
    return ovGCs;
  }

  @Override
  public IPositionVector updateCompPos(C c) {
    IPositionVector oldPos = registeredPositions.get(c);
    if (oldPos == null) {
      throw new IllegalArgumentException("Component not yet registered: " + c);
    }
    if (c.getPosition().equals(oldPos)) {
      return oldPos;
    }
    removeFromIndexMaps(c);
    registerNewPosition(c);
    return oldPos;
  }

  /**
   * remove given component from internal maps relating to grid cells
   * ("comp->cells overlapped" map and and "cell->comps overlapping it" map),
   * but not {@link #registeredPositions} map
   *
   * @param c
   */
  private void removeFromIndexMaps(C c) {
    Collection<Integer> remove = overlappedGridCells.remove(c);
    if (remove == null) { // DEBUG
      throw new IllegalStateException(c + " is unknown (i.e. not registered)");
    } else if (remove.isEmpty()) {
      throw new IllegalStateException(c + " overlaps no grid cell");
    }
    for (Integer gci : remove) {
      compsInGridCell.get(gci).remove(c);
    }
  }

  @Override
  public IPositionVector unregisterComp(C c) {
    removeFromIndexMaps(c);
    return registeredPositions.remove(c);
  }

  @Override
  public IPositionVector getRegisteredPosition(C c) {
    return registeredPositions.get(c);
  }
}
