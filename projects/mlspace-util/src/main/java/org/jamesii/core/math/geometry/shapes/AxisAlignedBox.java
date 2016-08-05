/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * Rectangle or right cuboid (rectangular box) aligned with the cartesian axes
 * a.k.a. axis-aligned bounding box (AABB)
 *
 * Overlap check exact (rounding error notwithstanding) for other
 * CartesianCuboids and Spheres
 *
 * * @author Arne Bittig
 *
 */
public class AxisAlignedBox extends AbstractShape {

  /** Serialization ID */
  private static final long serialVersionUID = -2561518637327974850L;

  /** One half, absolutely no magic here */
  private static final double ONE_HALF = 0.5;

  /**
   * Constructor taking two opposite corners as input
   *
   * @param corner1
   *          One corner
   * @param corner2
   *          Opposite corner
   */
  public AxisAlignedBox(IPositionVector corner1, IPositionVector corner2) {
    super(corner1.interpolate(ONE_HALF, corner2, ONE_HALF), corner1
        .scaledDisplacementTo(corner2, ONE_HALF));
    getMaxExtVectorForModification().absolute(); // in-place method cannot
    // be integrated above
    this.setSize(getBoxVolumeFromHalfSideLengths(getMaxExtVector()));
    checkSize();
  }

  /**
   * Constructor taking center position and vector pointing to one corner
   *
   * @param center
   *          Centroid of cuboid / axis-aligned box
   * @param halfDiag
   *          Vector from center to one corner
   */
  public AxisAlignedBox(IPositionVector center, IDisplacementVector halfDiag) {
    super(center, halfDiag);
    this.getMaxExtVectorForModification().absolute();
    this.setSize(getBoxVolumeFromHalfSideLengths(getMaxExtVector()));
    checkSize();
  }

  /**
   * Area / volume of a cuboid (axis-aligned)
   *
   * @param halfExt
   *          Half the extension of cuboid in each dimension (i.e. distance from
   *          center to each side)
   * @return size
   */
  private static double getBoxVolumeFromHalfSideLengths(
      IDisplacementVector halfExt) {
    double vol = Math.abs(Vectors.prod(halfExt));
    for (int d = halfExt.getDimensions(); d > 0; d--) {
      vol *= 2;
    }
    return vol;
  }

  @Override
  public boolean includesPoint(IPositionVector p, double tol) {
    IDisplacementVector pdisp = getCenter().displacementTo(p);
    pdisp.absolute();
    return getMaxExtVector().greaterOrEqual(pdisp.plus(-tol));
  }

  @Override
  public ShapeRelation getRelationTo(IShape s2, double tol) {
    if (s2 == this) {
      return ShapeRelation.IDENTICAL;
    }
    if (s2 == null) {
      return null;
    }
    if (s2 instanceof AxisAlignedBox) {
      return getRelationToCC(s2, 1, tol);
    } else if (s2 instanceof Sphere) {
      return getRelationToSphere(s2, tol);
    } else {
      unknownOtherShapeWarning(s2);
    }
    // AxisAlignedBox <-> AxisAlignedBox comparison always possible
    return getRelationTo(s2.boundingBox());
  }

  /**
   * @param s2
   * @return
   */
  private ShapeRelation getRelationToSphere(IShape s2, double tol) {
    // adapted from 'A Simple Method for Box-Sphere Intersection
    // Testing', by James Arvo. In Graphics Gems, 1990. see
    // http://tog.acm.org/resources/GraphicsGems/gems/BoxSphere.c

    // center-halfDiag version here (with object's methods)
    double distBoxSphereCSq = 0;
    IDisplacementVector centerDisp =
        s2.getCenter().displacementTo(this.getCenter());
    centerDisp.absolute();
    double radius = s2.getExtension(1) / 2;

    if (getMaxExtVector().greaterOrEqual(centerDisp.plus(radius - tol))) {
      return ShapeRelation.SUPERSET;
    }

    double radSqr = radius * radius;
    for (int d = 1; d <= getCenter().getDimensions(); d++) {
      double diff = centerDisp.get(d) - getMaxExtVector().get(d);
      if (diff > 0) {
        distBoxSphereCSq += diff * diff;
      }
    }
    if (Math.abs(distBoxSphereCSq - radSqr) <= tol) {
      return ShapeRelation.TOUCH; // CHECK: tol square?!
    }
    if (distBoxSphereCSq + tol > radSqr) {
      return ShapeRelation.DISTINCT; // CHECK: tol square?!
    }
    // else: at least OVERLAP (maybe SUBSET; SUPERSET already handled)
    IDisplacementVector farCornerDisp = getMaxExtVector().copy();
    farCornerDisp.absolute();
    farCornerDisp.add(centerDisp);
    if (farCornerDisp.lengthSquared() < radSqr + tol) {
      return ShapeRelation.SUBSET;
    }
    return ShapeRelation.OVERLAP;
  }

  private ShapeRelation getRelationToCC(IShape s2, int startDim, double tol) {
    ShapeRelation relAlongDim;
    if (Math.abs(this.getMin(startDim) - s2.getMin(startDim)) <= tol) {
      relAlongDim = getRelationToCCAlongDimSame(s2, startDim, tol);
    } else if (this.getMin(startDim) < s2.getMin(startDim)) {
      relAlongDim = getRelationToCCAlongDimLower(s2, startDim, tol);
    } else {
      relAlongDim = getRelationToCCAlongDimHigher(s2, startDim, tol);
    }

    if (startDim == getCenter().getDimensions()
        || relAlongDim == ShapeRelation.DISTINCT) {
      // shortcut: no further check needed in case of DISTINCT
      return relAlongDim;
    }
    return shapeRelAnd(relAlongDim, getRelationToCC(s2, startDim + 1, tol));
  }

  /**
   * If an {@link AxisAlignedBox} has this relation to another when projected
   * into a lower number of dimensions, and relation r2 when projected into
   * another dimension, the relation of the projection into all of these
   * dimensions is returned by this function.
   *
   * @param r1
   *          A {@link ShapeRelation}
   * @param r2
   *          Another ShapeRelation
   * @return Analog of logical AND of this and r2 (see caveats)
   *
   * @note If shape A has relation r1 to B and B the relation r2 to C, then the
   *       relation of A to C is SUBSET, IDENTICAL or SUPERSET if r1.and(r2)
   *       returns the respective value, otherwise it is still unknown. (For
   *       example, if A is distinct from B and B is distinct from C, or if A is
   *       a subset of B and B is a superset of C, the relation of A to C may be
   *       any of the possibilities. However, for the purpose described above,
   *       the method will return DISTINCT or OVERLAP, respectively).
   */
  public static ShapeRelation shapeRelAnd(ShapeRelation r1, ShapeRelation r2) {
    if (r2 == ShapeRelation.IDENTICAL) {
      return r1;
    }
    if (r1 == ShapeRelation.IDENTICAL) {
      return r2;
    }
    if (r1 == ShapeRelation.TOUCH) {
      if (r2 == ShapeRelation.DISTINCT) {
        return ShapeRelation.DISTINCT;
      } else {
        return ShapeRelation.TOUCH;
      }
    }
    return shapeRelAndDistinctOrOverlap(r1, r2);
  }

  private static ShapeRelation shapeRelAndDistinctOrOverlap(ShapeRelation r1,
      ShapeRelation r2) {
    if (r1 == ShapeRelation.DISTINCT || r2 == ShapeRelation.DISTINCT) {
      return ShapeRelation.DISTINCT;
    }
    if (r1 == ShapeRelation.OVERLAP || r2 == ShapeRelation.OVERLAP) {
      return ShapeRelation.OVERLAP;
    }
    if (r1 == r2) { // NOSONAR: Duh! Enums!
      return r1; // both SUPERSET, both SUBSET, or both null
    }

    // one is SUBSET, the other SUPERSET
    return ShapeRelation.OVERLAP;
  }

  /**
   * @param s2
   * @param startDim
   * @return
   */
  private ShapeRelation getRelationToCCAlongDimLower(IShape s2, int startDim,
      double tol) {
    ShapeRelation relAlongDim;
    // this: |--
    // s2 : ~~~~|--
    if (Math.abs(this.getMax(startDim) - s2.getMin(startDim)) <= tol) {
      relAlongDim = ShapeRelation.TOUCH;
    } else if (this.getMax(startDim) < s2.getMin(startDim)) {
      relAlongDim = ShapeRelation.DISTINCT;
    } else if (this.getMax(startDim) < s2.getMax(startDim)) {
      relAlongDim = ShapeRelation.OVERLAP;
    } else {
      // (this.getMax(startDim) >= s2.getMax(startDim))
      relAlongDim = ShapeRelation.SUPERSET;
    }
    return relAlongDim;
  }

  /**
   * @param s2
   * @param startDim
   * @return
   */
  private ShapeRelation getRelationToCCAlongDimSame(IShape s2, int startDim,
      double tol) {
    ShapeRelation relAlongDim;
    // this: |---
    // s2 : ~|---
    if (Math.abs(this.getMax(startDim) - s2.getMax(startDim)) <= tol) {
      relAlongDim = ShapeRelation.IDENTICAL;
    } else if (this.getMax(startDim) < s2.getMax(startDim)) {
      relAlongDim = ShapeRelation.SUBSET;
    } else {
      // (this.getMax(startDim) >= s2.getMax(startDim))
      relAlongDim = ShapeRelation.SUPERSET;
    }
    return relAlongDim;
  }

  /**
   * @param s2
   * @param startDim
   * @return
   */
  private ShapeRelation getRelationToCCAlongDimHigher(IShape s2, int startDim,
      double tol) {
    ShapeRelation relAlongDim;
    // this: ~~|--
    // s2 : |--
    if (Math.abs(this.getMin(startDim) - s2.getMax(startDim)) <= tol) {
      relAlongDim = ShapeRelation.TOUCH;
    } else if (this.getMin(startDim) > s2.getMax(startDim)) {
      relAlongDim = ShapeRelation.DISTINCT;
    } else if (this.getMax(startDim) > s2.getMax(startDim)) {
      relAlongDim = ShapeRelation.OVERLAP;
    } else {
      // (this.getMax(startDim) <= s2.getMax(startDim))
      relAlongDim = ShapeRelation.SUBSET;
    }
    return relAlongDim;
  }

  @Override
  public IShape boundingBox() {
    return this; // AxisAlignedBox is one of the most simple shapes
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2) {
    if (s2 instanceof Sphere) {
      return dispForTouchOutsideSphere(s2);
    }
    if (!(s2 instanceof AxisAlignedBox)) {
      unknownOtherShapeWarning(s2);
    }
    // else move s2 along the dimension along which the bounding boxes
    // overlap least -- this is the optimal solution if s2 is a box, but may
    // be inaccurate (i.e. farther than necessary) if s2's center is outside
    // this shape and it overlaps only a corner
    return dispForTouchOutsideBox(s2);
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2,
      IDisplacementVector oppositeDirection) {
    if (s2 instanceof Sphere) {
      return dispForTouchOutsideSphere(s2, oppositeDirection);
    }
    if (!(s2 instanceof AxisAlignedBox)) {
      unknownOtherShapeWarning(s2);
    }
    // else move s2 along the dimension along which the bounding boxes
    // overlap least -- this is the optimal solution if s2 is a box, but may
    // be inaccurate (i.e. farther than necessary) if s2's center is outside
    // this shape and it overlaps only a corner
    return dispForTouchOutsideBox(s2, oppositeDirection);
  }

  private IDisplacementVector dispForTouchOutsideBox(IShape s2) {
    IDisplacementVector cDisp = s2.getCenter().displacementTo(this.getCenter());
    int dirOfMinOverlap = 0;
    double minOverlapSoFar = Double.MAX_VALUE;
    for (int d = 1; d <= cDisp.getDimensions(); d++) {
      int cDispInDirSign = cDisp.get(d) < 0 ? -1 : 1; // 0 is negative!
      double overlap = 
          cDispInDirSign < 0 ? this.getMax(d) - s2.getMin(d) : s2.getMax(d)
          - this.getMin(d);
      if (overlap < minOverlapSoFar) {
        if (minOverlapSoFar > 0 && dirOfMinOverlap > 0) {
          // no move required along dimension of previous min
          cDisp.set(dirOfMinOverlap, 0.);
        }
        cDisp.set(d, -overlap * cDispInDirSign);

        dirOfMinOverlap = d;
        minOverlapSoFar = overlap;
      } else if (overlap < 0) {
        // s2 is far from this one; move it closer
        cDisp.set(d, -overlap * cDispInDirSign);
      } else {
        cDisp.set(d, 0.);
      }
    }
    return cDisp;
  }

  /**
   * @param s2
   * @param direction
   * @return
   */
  private IDisplacementVector dispForTouchOutsideBox(IShape s2,
      IDisplacementVector oppositeDirection) {
    return dispForTouchOutsideBox(s2); // FIXME!
  }

  /**
   * {@link #dispForTouchOutside(IModifiableShape)} special case: parameter is
   * sphere
   *
   * @param s2
   *          Sphere
   * @return {@link #dispForTouchInside(IModifiableShape)}
   */
  private IDisplacementVector dispForTouchOutsideSphere(IShape s2) {
    /* attempt with #surfPointClosest... */
    IPositionVector s2center = s2.getCenter();
    IPositionVector closestSurfPoint = surfacePointClosestTo(s2center);
    IDisplacementVector move = s2center.displacementTo(closestSurfPoint);

    double s2radius = s2.getExtension(1) / 2;
    if (move.isNullVector()) // s2.center is on surface
    {
      assert lastSurfPointPointCache == s2center; // NOSONAR
      move.set(
          lastSurfPointClosestDimCache,
          s2center.get(lastSurfPointClosestDimCache) > this.getCenter().get(
              lastSurfPointClosestDimCache) ? s2radius : -s2radius);
      return move;
    }

    double moveLength = move.length();
    if (this.includesPoint(s2center)) {
      move.scale(1 + s2radius / moveLength);
    } else {
      move.scale(1 - s2radius / moveLength);
    }
    return move;
    /* end of attempt of using #surfPointClosest... */
  }

  /**
   * @param s2
   * @param direction
   * @return
   */
  private IDisplacementVector dispForTouchOutsideSphere(IShape s2,
      IDisplacementVector oppositeDirection) {
    return dispForTouchOutsideSphere(s2); // FIXME!
  }

  @Override
  public IDisplacementVector dispForTouchInside(IShape s2) {
    IDisplacementVector s2mev = s2.getMaxExtVector();
    if (!getMaxExtVector().greaterOrEqual(s2mev)) {
      return null;
      // throw new SpatialException(s2 + " cannot fit inside " + this);
    }
    IDisplacementVector cdisp = this.getCenter().displacementTo(s2.getCenter());
    IDisplacementVector cdispabs = cdisp.copy();
    cdispabs.absolute();
    // cdispabs could be used to store return value directly; more clear:
    IDisplacementVector rv = cdisp.copy();

    for (int d = 1; d <= cdispabs.getDimensions(); d++) {
      double over = s2mev.get(d) + cdispabs.get(d) - getMaxExtVector().get(d);
      // first, check if any displacement along d is needed:
      if (over <= 0) {
        rv.set(d, 0);
      } else {
        rv.set(d, cdisp.get(d) > 0 ? -over : over);
      }
    }
    return rv;
  }

  @Override
  public void scale(double sc) {
    getMaxExtVectorForModification().scale(sc);
    for (int i = 1; i <= getCenter().getDimensions(); i++) {
      this.setSize(this.getSize() * sc);
    }
  }

  @Override
  public String toString() {
    return "Cuboid from " + getCenter().minus(getMaxExtVector()) + " to "
        + getCenter().plus(getMaxExtVector());
  }

  /**
   * Get point on surface closest to given other point (may be in- or outside
   * this shape, or even on the surface).
   *
   * This function could be used to determine how to move another shape such
   * that it touches this one, but does not overlap it (however, if the
   * reference point p is already on the surface, special handling is required).
   * It could also be used for some shape relations (e.g. this box and a sphere
   * overlap iff the {@link #surfacePointClosestTo(IPositionVector)} a sphere's
   * center is inside the sphere, and they touch if it is on the sphere's
   * surface).
   *
   * @param p
   *          Point
   * @return Point on surface of this shape closest to p
   */
  public IPositionVector surfacePointClosestTo(IPositionVector p) {
    IPositionVector rv = p.copy();
    int ndim = rv.getDimensions();
    boolean possiblyInside = true;
    double minInsideMoveDist = Double.MAX_VALUE;
    double minInsideMoveVal = Double.NaN;
    int minInsideMoveDim = 0;
    for (int d = 1; d <= ndim; d++) {
      double coord = rv.get(d);
      double min = this.getMin(d);
      if (coord < min) {
        rv.set(d, min);
        possiblyInside = false;
        continue;
      }
      double max = this.getMax(d);
      if (coord > max) {
        rv.set(d, max);
        possiblyInside = false;
        continue;
      }
      double minDist = coord - min;
      double maxDist = max - coord;
      if (possiblyInside
          && (minDist < minInsideMoveDist || maxDist < minInsideMoveDist)) {
        if (minDist < maxDist) {
          minInsideMoveVal = min;
          minInsideMoveDist = minDist;
        } else {
          minInsideMoveVal = max;
          minInsideMoveDist = maxDist;
        }
        minInsideMoveDim = d;
      }
    }
    if (possiblyInside) { // now definitely inside
      rv.set(minInsideMoveDim, minInsideMoveVal);
    }
    // cache last dim and shape for the rare case of p being on the surface
    // (for the case that other methods needs to know which surface)
    lastSurfPointClosestDimCache = minInsideMoveDim;
    lastSurfPointPointCache = p;

    return rv;
  }

  /**
   * Cache for last position vector with which
   * {@link #surfacePointClosestTo(IPositionVector)} was called, but ONLY IF
   * this point happened to be inside this shape
   */
  private IPositionVector lastSurfPointPointCache = null;

  /** Cache for dimension belonging to {@link #lastSurfPointPointCache} */
  private int lastSurfPointClosestDimCache = 0;
}
