/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.shapes;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.shapes.ShapeUtils;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;

/**
 * @author Arne Bittig
 * @date Apr 20, 2012
 */
public class AxisAlignedBoxTest extends ChattyTestCase {

  /** Vector factory for all test */
  private final IVectorFactory vecFac = new AVectorFactory(3);

  /** rounding tolerance for comparisons */
  private static final double DELTA = 1e-15;

  /** test box-box relation methods and displacement for touch, too */
  public final void testGetRelationTo() {
    // ApplicationLogger.log(Level.INFO,
    // "Starting to test box-box relations");
    IPositionVector c1 = vecFac.newPositionVector(2., 1., 1.);
    IPositionVector c2 = vecFac.newPositionVector(new double[] { 1., 2., 0. });
    IPositionVector c3 = vecFac.newPositionVector(0., 3., -1.);

    AxisAlignedBox box12 = new AxisAlignedBox(c1, c2);
    AxisAlignedBox box23 = new AxisAlignedBox(c2, c3);
    AxisAlignedBox box13 = new AxisAlignedBox(c1, c3);
    AxisAlignedBox farBox =
        new AxisAlignedBox(vecFac.newPositionVector(3., -5., -1.),
            vecFac.newPositionVector(6., -2., 2.));

    assertEquals(1., box12.getSize());
    assertEquals(1., box23.getSize());
    assertEquals(8., box13.getSize());

    assertEquals(ShapeRelation.TOUCH, box12.getRelationTo(box23));
    assertEquals(ShapeRelation.SUPERSET, box13.getRelationTo(box23));
    assertEquals(ShapeRelation.SUBSET, box12.getRelationTo(box13));
    assertEquals(ShapeRelation.SUPERSET, box13.getRelationTo(box12));
    assertEquals(ShapeRelation.DISTINCT, box12.getRelationTo(farBox));
    assertEquals(ShapeRelation.DISTINCT, farBox.getRelationTo(box23));

    IDisplacementVector farMove = box23.dispForTouchOutside(farBox);
    farBox.move(farMove);
    assertEquals(ShapeRelation.SUBSET, box12.getRelationTo(farBox));
    assertEquals(ShapeRelation.OVERLAP, box13.getRelationTo(farBox));
    assertEquals(ShapeRelation.TOUCH, box23.getRelationTo(farBox));

    IDisplacementVector smallMove = farBox.dispForTouchInside(box23);
    box23.move(smallMove);
    assertEquals(ShapeRelation.TOUCH, box23.getRelationTo(box12));
    box23.move(vecFac.newDisplacementVector(0., 0., 1.));
    assertEquals(ShapeRelation.IDENTICAL, box23.getRelationTo(box12));

    // ApplicationLogger.log(Level.INFO,
    // "Finished testing box-box relations");
  }

  /** Test {@link ShapeUtils#getNeighborRelation(IShape, IShape, double)} */
  public final void testGetNeighborRelation() {
    IPositionVector c1 = vecFac.newPositionVector(-1., -1., -1.);
    IPositionVector c2 = vecFac.newPositionVector(1., 1., 0.);
    IPositionVector c3 = vecFac.newPositionVector(-1., -1., 2.);

    AxisAlignedBox smallBox = new AxisAlignedBox(c1, c2);
    AxisAlignedBox mediumBox = new AxisAlignedBox(c3, c2);

    double[] smallToMed =
        ShapeUtils.getNeighborRelation(smallBox, mediumBox, DELTA);
    assertEquals(3, smallToMed.length);
    double[] medToSmall =
        ShapeUtils.getNeighborRelation(mediumBox, smallBox, DELTA);
    assertEquals(3, medToSmall.length);
    // System.out.println(Arrays.toString(smallToMed));
    // System.out.println(Arrays.toString(medToSmall));
    assertEquals(smallToMed[0], medToSmall[0], DELTA);
    assertEquals(smallToMed[1], medToSmall[2], DELTA);
    assertEquals(smallToMed[2], medToSmall[1], DELTA);

    double[] medToMed =
        ShapeUtils.getNeighborRelation(mediumBox, mediumBox, DELTA);
    // System.out.println(Arrays.toString(medToMed));
    assertEquals(3, medToMed.length);
    assertEquals(0., medToMed[0], DELTA);
    assertEquals(0., medToMed[1], DELTA);
    assertEquals(0., medToMed[2], DELTA);
  }

  public void testSurfacePointClosestTo() {
    AxisAlignedBox box =
        new AxisAlignedBox(vecFac.origin(), vecFac.newDisplacementVector(1.,
            2., 3.));
    assertEquals(vecFac.newPositionVector(1., 1., 2.),
        box.surfacePointClosestTo(vecFac.newPositionVector(0.5, 1., 2.)));
    assertEquals(vecFac.newPositionVector(1., -1., 2.),
        box.surfacePointClosestTo(vecFac.newPositionVector(0.5, -1., 2.)));
    assertEquals(vecFac.newPositionVector(.4, 2., 2.),
        box.surfacePointClosestTo(vecFac.newPositionVector(0.4, 1.5, 2.)));
    assertEquals(vecFac.newPositionVector(.4, 1.4, -3.),
        box.surfacePointClosestTo(vecFac.newPositionVector(0.4, 1.4, -2.5)));
    assertEquals(vecFac.newPositionVector(-1., 2., 2.),
        box.surfacePointClosestTo(vecFac.newPositionVector(-4., 3., 2.)));

  }
}
