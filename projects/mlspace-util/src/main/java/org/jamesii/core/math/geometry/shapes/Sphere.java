/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import java.util.logging.Level;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Sphere/ball, circle/disk, or, more generally, hypersphere
 *
 * Overlap check exact (rounding error notwithstanding) for other Spheres and
 * CartesianCuboids (via the latter's respective method)
 *
 * @author Arne Bittig
 */
public class Sphere extends AbstractShape {

  private static final long serialVersionUID = 4113109402630516445L;

private double radius;

  /**
   * Disk/ball/n-ball constructor given center and radius
   *
   * @param center
   *          Position vector of shape's center (gives number of dimensions)
   * @param radius
   *          Sphere/circle's radius
   */
  public Sphere(IPositionVector center, double radius) {
    super(center, null);
    if (radius < 0) {
      throw new IllegalArgumentException("Sphere/disk radius must be >0");
    } else {
      this.radius = radius;
    }
    this.setSize(calculateVolume(radius, center.getDimensions()));
    checkSize();
  }

  @Override
  public IDisplacementVector getMaxExtVector() {
    return Vectors.allSameVector(radius, this.getCenter().getDimensions());
  }

  @Override
  public double getMax(int dim) {
    if (dim > 0) {
      return getCenter().get(dim) + radius;
    } else {
      return getCenter().get(-dim) - radius;
    }
  }

  @Override
  public double getMin(int dim) {
    return getCenter().get(dim) - radius;
  }

  @Override
  public double getExtension(int dim) {
    return radius * 2;
  }

  /**
   * Volume of n-sphere of given radius
   *
   * @param radius
   *          Radius of the n-sphere
   * @param dim
   *          Number of dimensions
   * @return Volume of n-sphere with radius r
   */
  public static double calculateVolume(double radius, int dim) {
    switch (dim) {
    case 2:
      return Math.PI * radius * radius;
    case 3: // NOSONAR
      return Math.PI * Math.pow(radius, 3) * 4. / 3.; // NOSONAR
    default: // also applicable to 2 and 3, but more calculations
      return Math.pow(Math.PI, dim / 2.)
          / org.jamesii.core.math.statistics.univariate.GammaFunction
          .gammaHalfInt(dim + 2) * Math.pow(radius, dim);
    }
  }

  private static final double ONE_BY_SQRT_PI = 1./Math.sqrt(Math.PI);

/**
   * Radius of n-sphere of given volume
   *
   * @param volume
   *          Volume of the n-sphere
   * @param dim
   *          Number of dimensions
   * @return Volume of n-sphere with radius r
   */
  public static double calculateRadius(double volume, int dim) {
    switch (dim) {
    case 2:
      return Math.sqrt(volume / Math.PI);
    case 3: // NOSONAR: 3D is no magic
      return Math.pow(volume / Math.PI / 4. * 3., 1. / 3.); // NOSONAR
    case 1:
      return volume / 2.;
    default:
      return Math.pow(org.jamesii.core.math.statistics.univariate.GammaFunction
      .gammaHalfInt(dim + 2) * volume, 1./dim) * ONE_BY_SQRT_PI;
    }
  }

  @Override
  public ShapeRelation getRelationTo(IShape s2, double tol) {
    if (s2 == this) {
      return ShapeRelation.IDENTICAL;
    }
    if (s2 == null) {
      return null;
    }
    if (s2 instanceof Sphere) {
      return getRelationToSphere(s2, tol);
    } else if (s2 instanceof AxisAlignedBox) {
      return s2.getRelationTo(this, tol).inverse();
    }
    unknownOtherShapeWarning(s2);
    // boundingBox (AxisAlignedBox) <-> Sphere comparison always possible
    return s2.boundingBox().getRelationTo(this).inverse();
  }

  /**
   * @param s2
   * @param tol
   * @return
   */
  private ShapeRelation getRelationToSphere(IShape s2, double tol) {
    double cdist = this.getCenter().distance(s2.getCenter());
    double r1 = this.radius;
    double r2 = s2.getExtension(1) / 2.0;
    if (cdist <= tol && Math.abs(r1 - r2) <= tol - cdist) {
      return ShapeRelation.IDENTICAL;
    }
    if (Math.abs(r1 + r2 - cdist) <= tol) {
      return ShapeRelation.TOUCH;
    }
    if (cdist + tol > r1 + r2) { // CHECK: tol relevant?!
      return ShapeRelation.DISTINCT;
    }
    if (cdist <= r1 - r2 + tol) {
      return ShapeRelation.SUPERSET;
    }
    if (cdist <= r2 - r1 + tol) {
      return ShapeRelation.SUBSET;
    }
    return ShapeRelation.OVERLAP;
  }

  @Override
  public boolean includesPoint(IPositionVector p, double tol) {
    double rt = radius + tol;
    return p.distanceSquared(getCenter()) < rt * rt;
  }

  @Override
  public IShape boundingBox() {
    return new AxisAlignedBox(getCenter().plus(-radius), getCenter().plus(
        radius));
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2) {
    if (s2 instanceof Sphere) {
      return dispForTouchOutsideSphere((Sphere) s2);
    } else if (s2 instanceof AxisAlignedBox) {
      return s2.dispForTouchOutside(this).times(-1.);
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public IDisplacementVector dispForTouchOutside(IShape s2,
      IDisplacementVector oppositeDirection) {
    if (s2 instanceof Sphere) {
      return dispForTouchOutsideSphere((Sphere) s2, oppositeDirection);
    } else if (s2 instanceof AxisAlignedBox) {
      return s2.dispForTouchOutside(this, oppositeDirection).times(-1.); // CHECK!
    } else {
      throw new UnsupportedOperationException();
    }
  }

  private IDisplacementVector dispForTouchOutsideSphere(Sphere s2) {
    IDisplacementVector disp2to1 =
        s2.getCenter().displacementTo(this.getCenter());
    double dist = disp2to1.length();
    if (dist == 0.) {
      disp2to1.set(1, this.radius + s2.radius);
      return disp2to1;
    }
    IDisplacementVector disp =
        disp2to1.times(1 + -(this.radius + s2.radius) / dist);
    return disp;
  }

  /**
   * @param s2
   *          other sphere
   * @param oppositeDirection
   *          opposite of direction in which to move (e.g. recent move towards
   *          each other that went too far)
   * @return Displacement so spheres touch
   * @see #dispForTouchOutside(IShape, IDisplacementVector)
   */
  private IDisplacementVector dispForTouchOutsideSphere(Sphere s2,
      IDisplacementVector oppositeDirection) {
    IDisplacementVector currDisp =
        this.getCenter().displacementTo(s2.getCenter());
    double currDistSquared = currDisp.lengthSquared();
    double rSumSquared = this.radius + s2.radius;
    rSumSquared *= rSumSquared;
    double a = oppositeDirection.lengthSquared();
    // if (a == 0.) { // CHECK: null vector cannot be scaled anyway
    // return oppositeDirection;
    // }
    double b =
        2 * Vectors.dotProd(currDisp.toArray(), oppositeDirection.toArray());
    double c = currDistSquared - rSumSquared;
    double sc = quadraticFormulaNegativeResult(a, b, c);
    // return dispForTouchOutsideSphere(s2); // FIXME!
    return oppositeDirection.times(sc);
  }

  /**
   * Get the negative solution for a*x²+bx+c=0. Assert that two solutions exist
   * and that one is positive and one negative.
   *
   * @param a
   * @param b
   * @param c
   * @return x (the lower of the two)
   */
  private static double quadraticFormulaNegativeResult(double a, double b,
      double c) {
    double root = Math.sqrt(b * b - 4 * a * c);
    // System.out.println(a + "=a #" + b + "=b #" + c + "=c # "
    // + (b * b - 4 * a * c) + " root: " + root);
    assert root >= 0; // false if root is NaN
    assert a < 0 ? (root - b) < 0 : (root - b > 0);
    // sign of (-b+root)/2a is positive, of (-b-root)/2a negative
    assert a < 0 ? root + b < 0 : root + b > 0;
    return 0.5 * (-b - root) / a;
  }

  @Override
  public IDisplacementVector dispForTouchInside(IShape s2) {
    if (s2 instanceof Sphere) {
      double s2radius = s2.getExtension(1) / 2.;
      if (s2radius > this.radius) {
        return null; // does not fit
      }
      IDisplacementVector disp =
          s2.getCenter().displacementTo(this.getCenter());
      double dist = disp.length();
      disp.scale(1. - (this.radius - s2radius) / dist);
      return disp;
    } else if (s2 instanceof AxisAlignedBox) {
      double s2maxExt = s2.getMaxExtVector().length();
      if (s2maxExt > radius) {
        return null; // does not fit
      }
      ApplicationLogger.log(Level.WARNING, "Warning: Move-box-into-sphere "
          + "calculations may be inexact.");
      return dispForTouchInside(new Sphere(s2.getCenter(), radius));
      // TODO: get far corner, project onto surface, move?!
    } else {
      throw new UnsupportedOperationException();
    }
  }

  @Override
  public void scale(double sc) {
    this.radius *= sc;
    for (int d = 1; d <= getCenter().getDimensions(); d++) {
      this.setSize(this.getSize() * sc);
    }
  }

  // Overridden because the one in AbstractShape does not work if original
  // volume is 0 (does not make sense for cuboids, but is possible here)
  @Override
  public void scaleToSize(double targetVol) {
    this.radius = calculateRadius(targetVol, getCenter().getDimensions());
    this.setSize(targetVol);
  }

  @Override
  public String toString() {
    return super.toString() + " (r=" + radius + ")";
  }

}