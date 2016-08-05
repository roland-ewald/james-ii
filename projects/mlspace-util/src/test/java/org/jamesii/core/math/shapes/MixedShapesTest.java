/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.shapes;

import java.util.logging.Level;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.geometry.shapes.AxisAlignedBox;
import org.jamesii.core.math.geometry.shapes.IModifiableShape;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.ShapeRelation;
import org.jamesii.core.math.geometry.shapes.ShapeUtils;
import org.jamesii.core.math.geometry.shapes.Sphere;
import org.jamesii.core.math.geometry.vectors.AVectorFactory;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Periodic2DVectorFactory;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Test initialization of various shapes and their shape-relation methods
 * 
 * @author Arne Bittig
 * @date Mar 15, 2012
 */
public class MixedShapesTest extends ChattyTestCase {

  /** Vector factory for all test except {@link #testPeriodicBoundaries()} */
  private final IVectorFactory vecFac = new AVectorFactory();

  /** rounding tolerance for comparisons */
  private static final double DELTA = 1e-12;

  /** test sphere and box relations */
  public final void testShapes() {

    ApplicationLogger.log(Level.INFO,
        "Starting assortment of shape-related tests");
    // CoordVector c1 = new CoordVector(2.0, 1.0, 1.0);
    // CoordVector c2 = new CoordVector(new double[] { 1.0, 2.0, 0.0 });
    // CoordVector c3 = new CoordVector(0.0, 3.0, 1.0);
    IPositionVector c1 = vecFac.newPositionVector(2., 1., 1.);
    IPositionVector c2 = vecFac.newPositionVector(new double[] { 1., 2., 0. });
    IPositionVector c3 = vecFac.newPositionVector(0., 3., 1.);
    AxisAlignedBox cc12 = new AxisAlignedBox(c1, c2);
    AxisAlignedBox cc23 = new AxisAlignedBox(c2, c3);
    // AxisAlignedBox cc13 = new AxisAlignedBox(c1, c3);

    ApplicationLogger.log(Level.INFO, cc12.getRelationTo(cc23).toString());
    // ApplicationLogger.log(Level.INFO, cc13.getRelationTo(cc23));
    // ApplicationLogger.log(Level.INFO, cc23.getRelationTo(cc13));

    ApplicationLogger.log(Level.INFO, Double.toString(cc12.getSize()));
    // ApplicationLogger.log(Level.INFO, cc13.getVolume());
    ApplicationLogger.log(Level.INFO, Double.toString(cc23.getSize()));
    // ApplicationLogger.log(Level.INFO, cc13.getMaxExtVector());

    // ApplicationLogger.log(Level.INFO, cc12.splitEqually().toString());
    // // TODO: assert equality between previous and following ?!
    ApplicationLogger.log(Level.INFO, ShapeUtils.splitEqually(cc12, 2)
        .toString());
    ApplicationLogger.log(Level.INFO, ShapeUtils.splitEqually(cc12, 3)
        .toString());
    // ApplicationLogger.log(Level.INFO, cc12.splitEqually(1));
    //
    IModifiableShape sp1 = new Sphere(c1.interpolate(0.5, c2, 0.5), 0.75);
    IModifiableShape sp2 =
        new Sphere(vecFac.newPositionVector(0.6, 0.6, -0.4), 0.5);
    assertEquals(ShapeRelation.DISTINCT, sp2.getRelationTo(sp1));
    IDisplacementVector disp21 = sp2.dispForTouchOutside(sp1);
    IDisplacementVector disp12 = sp1.dispForTouchOutside(sp2);
    assertTrue(disp21.plus(disp12).isNullVector());
    sp1.move(disp21);
    assertEquals(sp2.getRelationTo(sp1, DELTA), ShapeRelation.TOUCH);
    assertNull(sp2.dispForTouchInside(sp1));

    sp2.move(sp1.dispForTouchInside(sp2));
    assertEquals(sp1.getRelationTo(sp2, DELTA), ShapeRelation.SUPERSET);

    // ApplicationLogger.log(Level.INFO, sp1 + " ... " + sp2);

    assertEquals(ShapeUtils.getNeighborRelation(cc12, sp1, 0)[0], 0.0);
    // assertEquals(sp2.getNeighborRelation(cc23)[0], 0.0);
    AxisAlignedBox ccx =
        new AxisAlignedBox(c2, vecFac.newPositionVector(1.6, 2.4, 0.8));
    assertEquals(ShapeUtils.getNeighborRelation(cc12, ccx, DELTA)[0], 0.48,
        DELTA);
    assertEquals(ShapeUtils.getNeighborRelation(cc12, ccx, 0)[0],
        ShapeUtils.getNeighborRelation(ccx, cc12, 0)[0]);
    assertEquals(ShapeUtils.getNeighborRelation(cc23, ccx, 0)[0], 0.32, DELTA);
  }

  /** test box-sphere relation methods incl. dispForTouch... */
  public final void testAABoxSphereRelation() {
    ApplicationLogger.log(Level.INFO, "Starting to test box-sphere relations");
    AxisAlignedBox box =
        new AxisAlignedBox(vecFac.newPositionVector(-.5, -1.5, -2.5),
            vecFac.newPositionVector(.5, 1.5, 2.5));
    Sphere spBig = new Sphere(vecFac.newPositionVector(0.6, 0.7, 0.8), 4.2);
    Sphere spMed = new Sphere(vecFac.newPositionVector(-4.1, -4., -4.2), 1.5);
    Sphere spSml = new Sphere(vecFac.newPositionVector(-1.1, 3., -2.3), 0.5);

    ApplicationLogger.log(Level.INFO,
        box + " to " + spBig + ": " + box.getRelationTo(spBig));
    ApplicationLogger.log(Level.INFO,
        box + " to " + spMed + ": " + box.getRelationTo(spMed));
    ApplicationLogger.log(Level.INFO,
        box + " to " + spSml + ": " + box.getRelationTo(spSml));
    // TODO: assertions
    assertEquals(ShapeRelation.OVERLAP, spSml.getRelationTo(spBig));
    assertEquals(ShapeRelation.DISTINCT, spSml.getRelationTo(spMed));
    assertEquals(ShapeRelation.DISTINCT, spMed.getRelationTo(spSml));

    ApplicationLogger.log(Level.INFO, spSml.dispForTouchOutside(spBig)
        .toString());
    ApplicationLogger.log(Level.INFO, spSml.dispForTouchOutside(spMed)
        .toString());
    ApplicationLogger.log(Level.INFO, spMed.dispForTouchOutside(spSml)
        .toString());
    // TODO: assertions
    ApplicationLogger
        .log(Level.INFO, box.dispForTouchOutside(spBig).toString());
    ApplicationLogger
        .log(Level.INFO, box.dispForTouchOutside(spMed).toString());
    ApplicationLogger
        .log(Level.INFO, box.dispForTouchOutside(spSml).toString());

    // try {
    assertNull(spSml.dispForTouchInside(spBig));
    assertNull(spSml.dispForTouchInside(spMed));
    // fail();
    // } catch (IllegalArgumentException e) {
    // }
    ApplicationLogger.log(Level.INFO, spMed.dispForTouchInside(spSml)
        .toString());
    // TODO: assertions
    // try {
    assertNull(box.dispForTouchInside(spBig));
    assertNull(box.dispForTouchInside(spMed));
    // fail();
    // } catch (IllegalArgumentException e) {
    // }
    ApplicationLogger.log(Level.INFO, box.dispForTouchInside(spSml).toString());

    ApplicationLogger.log(Level.INFO, "Finished testing box-sphere relations");

    /* OUTPUT AS OF REVISION 26317: */
    // Starting to test box-sphere relations
    // SUBSET
    // DISTINCT
    // DISTINCT
    // OVERLAP
    // DISTINCT
    // DISTINCT
    // (0,19434|-0,26294|0,35439)
    // (2,23559|5,21638|1,41587)
    // (-2,23559|-5,21638|-1,41587)
    // Warning: displacement for shapes touching each other calculated for
    // bounding box of Sphere at (0,60000|0,70000|0,80000)
    // (4,10000|0,00000|0,00000)
    // (2,10000|1,00000|0,20000)
    // (0,10000|-1,00000|0,00000)
    // Warning: displacement for shapes touching each other calculated for
    // bounding box of Sphere at (-4,10000|-4,00000|-4,20000)
    // Warning: displacement for shapes touching each other calculated for
    // bounding box of Sphere at (-1,10000|3,00000|-2,30000)

  }

  /** Test shapes in periodic boundary environment */
  public final void testPeriodicBoundaries() {
    IVectorFactory perVecFac = new Periodic2DVectorFactory(-4., -3., 2., 1.);
    IPositionVector farPos = perVecFac.newPositionVector(6., 7.);
    assertTrue(farPos.get(1) <= 1.);
    assertTrue(farPos.get(2) <= 0.);
    IPositionVector dispPos = farPos.plus(3.);
    assertTrue(farPos.get(1) > dispPos.get(1));
    assertTrue(farPos.get(2) > dispPos.get(2));

    IShape spBigTL = new Sphere(perVecFac.newPositionVector(-3., -0.5), 1.6);
    IShape boxSmallTL =
        new AxisAlignedBox(perVecFac.newPositionVector(-3.95, -2.95),
            perVecFac.newDisplacementVector(0.2, 0.2));
    assertEquals(1.8, spBigTL.getCenter().distance(boxSmallTL.getCenter()), 0.2);
    assertEquals(ShapeRelation.OVERLAP, spBigTL.getRelationTo(boxSmallTL));
    assertEquals(ShapeRelation.OVERLAP, boxSmallTL.getRelationTo(spBigTL));

    IShape spMedBL = new Sphere(perVecFac.newPositionVector(-2.5, -2.9), 0.8);
    assertEquals(1.67, spBigTL.getCenter().distance(spMedBL.getCenter()), 0.1);
    assertEquals(ShapeRelation.OVERLAP, spMedBL.getRelationTo(spBigTL));
    assertEquals(ShapeRelation.OVERLAP, spBigTL.getRelationTo(spMedBL));

    assertEquals(ShapeRelation.DISTINCT, boxSmallTL.getRelationTo(spMedBL));
    assertEquals(ShapeRelation.DISTINCT, spMedBL.getRelationTo(boxSmallTL));
  }

}
