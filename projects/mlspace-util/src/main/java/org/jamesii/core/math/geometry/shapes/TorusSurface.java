/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.IVectorFactory;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * n-torus surface. There should (reasonably) be only one per experiment.
 *
 * Can only be constructed from periodic boundaries defined in the VectorFactory
 * class and assumes no cheating (i.e. only arguments created by the same vector
 * factory) -- e.g. it includes every valid point and is a superset to every
 * other shape defined within.
 *
 * @author Arne Bittig
 *
 */
public class TorusSurface extends AbstractShape {

  /** Serialization ID */
  private static final long serialVersionUID = 5938064619695564710L;

  /** The completely magic-free constant 1/2 */
  private static final double ONE_HALF = 0.5;

  /** Vector factory (which contains information on min and max) */
  private final IVectorFactory vecFac;

  /**
   * Construct torus surface spanning the whole space of the periodic boundaries
   * defined in the vector factory
   *
   * @param vecFac
   *          periodic boundary vector factory
   * @throws NullPointerException
   *           if no boundaries defined in vector factory
   */
  public TorusSurface(IVectorFactory vecFac) {
    super(vecFac.getLowBoundary().interpolate(ONE_HALF,
        vecFac.getHighBoundary(), ONE_HALF), vecFac.getPeriod().times(ONE_HALF));
    if (this.getMaxExtVector() == null) {
      // redundant for the distinction unbounded<->periodic (NPE earlier)
      // useful for vector factories for bounded non-periodic space
      throw new IllegalArgumentException("Periodic boundaries"
          + " are not specified in given vector factory."
          + " Cannot create torus surface.");
    }
    this.setSize(Vectors.prod(vecFac.getPeriod()));
    this.vecFac = vecFac;
  }

  @Override
  public boolean includesPoint(IPositionVector p, double tol) {
    return true;
  }

  @Override
  public ShapeRelation getRelationTo(IShape s2, double tol) {
    return ShapeRelation.SUPERSET;
  }

  @Override
  public IShape boundingBox() {
    // System.out.println("Warning: Bounding box of torus surface "
    // + "requested. Rectangle of min and max coordinates returned.");
    return new AxisAlignedBox(vecFac.getLowBoundary(), vecFac.getHighBoundary());
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2,
      IDisplacementVector oppositeDirection) {
    throw new UnsupportedOperationException();
  }

  @Override
  public IDisplacementVector dispForTouchInside(IShape s2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void scale(double sc) {
    throw new UnsupportedOperationException();
  }

  /**
   * @return Vector factory creating vectors in this torus surface (and
   *         containing boundaries)
   */
  public IVectorFactory getVecFac() {
    return vecFac;
  }
}
