/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.vectors;

import java.util.Arrays;
import java.util.logging.Level;

import junit.framework.TestCase;

import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.shapes.TorusSurface;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * @author Arne Bittig
 */
public abstract class AbstractVectorTest extends TestCase {

  private IVectorFactory vecFac;

  @Override
  protected void setUp() {
    vecFac = createVectorFactory();
  }

  protected abstract IVectorFactory createVectorFactory();

  /**
   * Test vector operations in 3D
   */
  public final void test3DVectorOps() {
    if (vecFac.getDimension() > 0 && vecFac.getDimension() != 3) {
      return;
    }
    IPositionVector a = vecFac.newPositionVector(1.0, 2.0, 3.0);
    IPositionVector b = vecFac.newPositionVector(-0.5, -0.5, -0.5);
    try {
      @SuppressWarnings("unused")
      IPositionVector c = vecFac.newPositionVector(1.5, 2.5);
      fail("Creation of vectors of different dimensions with "
          + "the same vector factory should not be possible");
    } catch (RuntimeException e) {
      // ApplicationLogger.log(e);
    }

    IDisplacementVector aminusb = b.displacementTo(a);
    assertTrue(Arrays.equals(aminusb.toArray(), new double[] { 1.5, 2.5, 3.5 }));

    assertTrue(a.distanceSquared(vecFac.origin()) == 14.0);

    // either getVector or the constructor should copy the array
    IPositionVector a2 = vecFac.newPositionVector(a.toArray());
    assertTrue(a.isEqualTo(a2));
    assertNotSame(a2, a);
    assertNotSame(a2.toArray(), a.toArray());

    // CoordVector a10_3 = new CoordVector(10.0 / 3, 20.0 / 3, 10);
    // assertEquals(a.equalsSc(a10_3), 10.0 / 3);

    ApplicationLogger.log(Level.INFO, "3D ops test performed for " + vecFac);
  }

  /**
   * Test vector operations with periodic boundaries
   */
  public final void testPeriodicBoundaries() {
    if (vecFac.getPeriod() == null) {
      return;
    }
    IShape torus = new TorusSurface(vecFac);
    assertEquals(torus.getSize(), 48.);
    ApplicationLogger.log(Level.INFO, "Periodic boundaries test performed for "
        + vecFac);
  }

}
