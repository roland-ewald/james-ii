/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.spatialindex;

import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.spatialindex.ISpatialIndex;
import org.jamesii.core.math.geometry.spatialindex.StaticGridSpatialIndex;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;

public class StaticGridSpatialIndexTest extends AbstractSpatialIndexTest {

  private static final int TEST_DIM = 3;

  private static final int TEST_NUM_OF_CELLS = 9;

  private final int targetNumOfCells;

  public StaticGridSpatialIndexTest() {
    this(new AVectorFactory(TEST_DIM), TEST_NUM_OF_CELLS);
  }

  public StaticGridSpatialIndexTest(IVectorFactory vecFac,
      int targetNumOfGridCells) {
    super(vecFac);
    this.targetNumOfCells = targetNumOfGridCells;
  }

  @Override
  protected ISpatialIndex<SimpleShapedComponent> setUpSpatialIndex(
      IShape surrounding) {
    StaticGridSpatialIndex<SimpleShapedComponent> si =
        new StaticGridSpatialIndex<>(surrounding, targetNumOfCells);
    return si;
  }

}
