/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.vectors;

import java.util.Arrays;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * Static methods for dealing with vectors
 *
 * @author Arne Bittig
 * @date May 14, 2012
 */
public final class Vectors {

  /** Hidden utility class constructor */
  private Vectors() {
  }

  /**
   * Set each coordinate of this vector to the respective minimum of the
   * coordinatesof the given two vectors. Both vectors should usually be of the
   * same type and from the same {@link IVectorFactory VectorFactory}, otherwise
   * surprising results may occur e.g. for vectors from different periodic
   * boundary spaces.
   *
   * @param v1
   *          one vector
   * @param v2
   *          other vector
   * @return Vector with each coordinate the minimum of the respective
   *         coordinates
   */
  public static <T extends IVector<T>> T min(T v1, T v2) {
    T rv = v1.copy();
    for (int dim = rv.getDimensions(); dim > 0; dim--) {
      double v2d = v2.get(dim);
      if (rv.get(dim) > v2d) {
        rv.set(dim, v2d);
      }
    }
    return rv;
  }

  /**
   * Set each coordinate of this vector to the respective maximum of the
   * coordinates of the given two vectors. Both vectors should usually be of the
   * same type and from the same {@link IVectorFactory VectorFactory}, otherwise
   * surprising results may occur e.g. for vectors from different periodic
   * boundary spaces.
   *
   * @param v1
   *          one vector
   * @param v2
   *          other vector
   * @return Vector with each coordinate the maximum of the respective
   *         coordinates
   */
  public static <T extends IVector<T>> T max(T v1, T v2) {
    T rv = v1.copy();
    for (int dim = rv.getDimensions(); dim > 0; dim--) {
      double v2d = v2.get(dim);
      if (rv.get(dim) < v2d) {
        rv.set(dim, v2d);
      }
    }
    return rv;
  }

  /**
   * Cumulative product of all coordinates (may be negative!), e.g. to determine
   * size (area/volume) of axis-aligned cuboid with this extension
   *
   * @param v
   *          vector
   * @return cumulative product of all coordinates
   */
  public static double prod(IVector<?> v) {
    double p = v.get(1);
    for (int d = 2; d <= v.getDimensions(); d++) {
      p *= v.get(d);
    }
    return p;
  }

  /**
   * Polar/sperical coordinates to cartesian coordinates for 1 to 3 dimensions
   * (hyperspherical not yet supported; 1 is a trivial/degenerate case)
   *
   * @param r
   *          radius (i.e. length)
   * @param angles
   *          angular coordinate (2d), or polar and azimuth angle (3d)
   * @return coordinates of same point in cartesian coordinates
   * @see org.jamesii.core.math.geometry.vectors.IVectorFactory#newPositionVector(double...)
   * @see org.jamesii.core.math.geometry.vectors.IVectorFactory#newDisplacementVector(double...)
   */
  public static double[] sphericalToCartesian(double r, double... angles) {
    switch (angles.length) {
    case 0: // doesn't make much sense, but doesn't hurt either
      return new double[] { r };
    case 1: // polar coordinates
      return polarToCartesian(r, angles[0]);
    case 2: // spherical coordinates
      return sphericalToCartesian(r, angles[0], angles[1]);
    default: // hyperspherical coordinates
      throw new UnsupportedOperationException(
          "Hyperspherical coordinates not yet supported");
    }
  }

  /**
   * polar to cartesian coordinate conversion
   *
   * @param r
   *          length/radius
   * @param angle
   *          angle
   * @return cartesian coordinates
   */
  public static double[] polarToCartesian(double r, double angle) {
    return new double[] { r * Math.cos(angle), r * Math.sin(angle) };
  }

  /**
   * spherical to cartesian coordinate conversion; note that for sampling points
   * from a unit sphere surface (i.e. random directions), theta input should be
   * uniformly distributed while phi should be from acos([-1,1]); compare
   * http://mathworld.wolfram.com/SpherePointPicking.html
   *
   * @param r
   *          length/radius
   * @param theta
   *          polar angle
   * @param phi
   *          azimuth angle
   * @return cartesian coordinates
   */
  public static double[] sphericalToCartesian(double r, double theta,
      double phi) {
    double tmp = r * Math.sin(theta);
    return new double[] { tmp * Math.cos(phi), tmp * Math.sin(phi),
        r * Math.cos(theta) };
  }

  /**
   * Cartesian coordinates to polar ones (2d only at the moment, to be expanded
   * to spherical)
   *
   * @param c
   *          cartesian coordinate vector
   * @return Vector starting with length followed by angles (i.e. theta)
   */
  public static double[] cartesianToSpherical(double[] c) {
    if (c == null || c.length == 0) {
      throw new IllegalArgumentException();
    }
    double[] rv = new double[c.length];
    rv[0] = vecNormEuclid(c);
    switch (c.length) {
    case 1:
      break;
    case 2: // polar coordinates
      rv[1] = Math.atan2(c[1], c[0]);
      break;
    case 3: // spherical coordinates
      rv[1] = Math.acos(c[2] / rv[0]);
      rv[2] = Math.atan(c[1] / c[0]);
      break;
    default: // hyperspherical coordinates
      throw new UnsupportedOperationException(
          "Supports only up to 3d coordinates so far");
    }
    return rv;
  }

  /**
   * Rotate a 2d vector (given by its cartesian coordinates) by given angle
   * (effectively by conversion to spherical coordinates and back, but not
   * explicitly calling the respective methods in {@link Vectors})
   *
   * @param vec
   *          Vector as array
   * @param angle
   *          Angle to rotate
   * @return Rotated vector
   */
  public static double[] rotate2d(double[] vec, double angle) {
    if (vec.length != 2) {
      throw new IllegalArgumentException();
    }
    double r = vecNormEuclid(vec);
    double newAngle = Math.atan2(vec[1], vec[0]) + angle;
    return new double[] { r * Math.cos(newAngle), r * Math.sin(newAngle) };
  }

  /**
   * Modify displacement vector such that it points in the direction of the
   * given angle, keeping its length constant
   *
   * @param disp
   *          Displacement vector (only length is relevant)
   * @param angle
   *          Angle/direction after rotation (absolute)
   * @return previous angle
   */
  public static double rotate2dToAngle(IDisplacementVector disp, double angle) {
    double prevAngle = Math.atan2(disp.get(2), disp.get(1));
    double length = disp.length();
    disp.set(1, length * Math.sin(angle));
    disp.set(2, length * Math.cos(angle));
    return prevAngle;
  }

  /**
   * Modify displacement vector such that it points in the direction of the
   * given angle, keeping its length constant
   *
   * @param disp
   *          Displacement vector (only length is relevant)
   * @param theta
   *          theta (incination) of new vector
   * @param phi
   *          (azimuth) of new vector
   */
  public static void rotate3dToAngle(IDisplacementVector disp, double theta,
      double phi) {
    double length = disp.length();
    double[] coord = Vectors.sphericalToCartesian(length, theta, phi);
    for (int d = coord.length; d > 0;) {
      disp.set(d, coord[--d]);
    }
  }

  /**
   * Rotate 3d vector by given angle in plane defined by the vector and given
   * other plane reference vector (such that the returned vector points to the
   * same side of the plane relative to the original as the reference if the
   * angle is below PI).
   *
   * @param vec
   *          Vector's coordinates
   * @param angle
   *          Angle to rotate by
   * @param planeRef
   *          Coordinates of vector defining a plane together with vec
   * @return Coordinates of rotated vector (same length as vec)
   */
  public static double[] rotate3dTowards(double[] vec, double angle,
      double[] planeRef) {
    final int dim = 3;
    if (vec.length != dim) {
      throw new IllegalArgumentException();
    }
    double lenVec = vecNormEuclid(vec);

    double vecScaleFactor = dotProd(vec, planeRef) / (lenVec * lenVec);
    double[] rejectionOfVecOnRef = new double[dim];
    double lenReject = 0;
    for (int i = 0; i < dim; i++) {
      double val = planeRef[i] - vecScaleFactor * vec[i];
      rejectionOfVecOnRef[i] = val;
      lenReject += val * val;
    }
    lenReject = Math.sqrt(lenReject);

    // assert dotProd(rejectionOfVecOnRef, vec) < 1e-12;

    double cosAngle = Math.cos(angle);
    double scForReject = Math.sin(angle) * lenVec / lenReject;
    double[] rv = new double[dim];
    for (int i = 0; i < dim; i++) {
      rv[i] = cosAngle * vec[i] + scForReject * rejectionOfVecOnRef[i];
    }
    return rv;
  }

  /**
   * Dot product (a.k.a. scalar product) of two vectors
   *
   * @param vecA
   *          One vector's coordinates
   * @param vecB
   *          Another vector's coordinates
   * @return Dot product
   */
  public static double dotProd(double[] vecA, double[] vecB) {
    if (vecA.length != vecB.length) {
      throw new IllegalArgumentException();
    }
    double dp = 0;
    for (int i = vecA.length - 1; i >= 0; i--) {
      dp += vecA[i] * vecB[i];
    }
    return dp;
  }

  /**
   * Angle between two vectors.
   *
   * Calls {@link #dotProd(double[], double[])},
   * {@link #vecNormEuclid(double[])} and {@link Math#acos(double)}.
   *
   * @param vecA
   *          One vector's coordinates
   * @param vecB
   *          Another vector's coordinates
   * @return Angle between them
   */
  public static double getAngle(double[] vecA, double[] vecB) {
    return Math.acos(
        dotProd(vecA, vecB) / (vecNormEuclid(vecA) * vecNormEuclid(vecB)));
  }

  /**
   * Angle of vector relative to base vector in x-direction -- 2D ONLY!
   *
   * @param vec
   *          Vectors coordinates
   * @return 2D angle between vector an x-axis
   */
  public static Object getAnsoluteAngle2d(double[] vec) {
    return Math.atan2(vec[1], vec[0]);
  }

  /**
   * Check whether one vector is a scaled version of another, return
   * <code>null</code> if not, scaling factor otherwise.
   *
   * Returns <code>null</code> if any vector is the null vector (or each
   * coordinate is within tol of 0).
   *
   * @param vecA
   *          One vector's coordinates
   * @param vecB
   *          Another vector's coordinates
   * @param tol
   *          Tolerance
   * @return The factor f for which vecA = f*vecB if vectors are colinear
   */
  public static Double getScalingFactor(double[] vecA, double[] vecB,
      double tol) {
    Double f = null;
    for (int i = 0; i < vecA.length; i++) {
      double ai = vecA[i];
      double bi = vecB[i];
      if (isAlmostZero(ai, tol) != isAlmostZero(bi, tol)) {
        return null;
      }
      double fi = ai / bi;
      if (f == null) {
        f = fi;
      } else if (isAlmostZero(fi - f, tol)) {
        f = 0.5 * (fi + f);
      } else {
        return null;
      }
    }
    return f;
  }

  private static boolean isAlmostZero(double d, double tol) {
    return -tol < d && d < tol;
  }

  /**
   * Geometric mean of positive values, e.g. to determine side of cube of same
   * volume as given cuboid/box
   *
   * @param arr
   *          Array of values (at least 1)
   * @return geometric mean (undefined if arr contains negative values)
   */
  public static double geometricMean(double[] arr) {
    double prod = arr[0];
    int size = arr.length;
    for (int i = 1; i < size; i++) {
      prod *= arr[i];
    }
    return Math.pow(prod, 1. / size);
  }

  /**
   * Vector p-norm on given values (uses {@link Math#pow(double, double)}, which
   * is probably not very efficient if the second parameter is 1 or 2). The
   * parameter p must be 1 or greater.
   *
   * @param arr
   *          Array representation of vector, e.g. from
   *          {@link org.jamesii.core.math.geometry.vectors.IDisplacementVector#toArray()}
   * @param p
   *          Norm parameter (e.g. 1=taxicab, 2=euclidian, Inf=maximum,...)
   * @return p-norm of vector represented by arr
   * @see #vecNormMax(double[])
   * @see #vecNormMin(double[])
   * @see #vecNormEuclid(double[])
   */
  public static double vecNorm(double[] arr, double p) {
    if (p <= 1) {
      throw new IllegalArgumentException();
    }
    if (p == Double.POSITIVE_INFINITY) {
      return vecNormMax(arr);
    }
    double sum = 0;
    for (double a : arr) {
      sum += Math.pow(Math.abs(a), p);
    }
    return Math.pow(sum, 1.0 / p);
  }

  /**
   * @param arr
   *          Vector coordinates
   * @return Euclidian norm, i.e. root of sum of squares of arr's values
   */
  public static double vecNormEuclid(double[] arr) {
    double sqSum = 0;
    for (double a : arr) {
      sqSum += a * a;
    }
    return Math.sqrt(sqSum);
  }

  /**
   * @param arr
   *          Vector coordinates
   * @return Max absolute value in arr
   */
  public static double vecNormMax(double[] arr) {
    double max = 0;
    for (double a : arr) {
      if (Math.abs(a) > max) {
        max = Math.abs(a);
      }
    }
    return max;
  }

  /**
   * Min abs value in array (not actually a vector norm but named in analogy to
   * {@link #vecNormMax(double[])})
   *
   * @param arr
   *          Vector coordinates
   * @return Min absolute value in arr
   */
  public static double vecNormMin(double[] arr) {
    double min = Double.POSITIVE_INFINITY;
    for (double a : arr) {
      if (Math.abs(a) < min) {
        min = Math.abs(a);
      }
    }
    return min;
  }

  /**
   * Element-wise division, e.g. to see how often one vector fits into another
   *
   * @param vecA
   *          Dividend / numerator
   * @param vecB
   *          Divisor / denominator
   * @return result of division of each corresponding coordinate
   */
  public static double[] divide(double[] vecA, double[] vecB) {
    int dim = vecA.length;
    if (dim != vecB.length) {
      throw new IllegalArgumentException();
    }
    double[] res = new double[dim];
    for (int i = 0; i < dim; i++) {
      res[i] = vecA[i] / vecB[i];
    }
    return res;
  }

  /**
   * Scale each coordinate of a vector by a random value from [-1;1]
   * (independently).
   *
   * @param vector
   *          Original vector
   * @param rand
   *          Random number generator
   */
  public static void randomScaling(IDisplacementVector vector, IRandom rand) {
    for (int d = vector.getDimensions(); d > 0; d--) {
      vector.set(d, vector.get(d) * (rand.nextDouble() * 2 - 1));
    }
  }

  /**
   * Vector with value 1 in all dimensions (newly created, modifiable)
   *
   * @param vecFac
   *          Vector factory
   * @return Vector with value 1 in all dimensions
   */
  public static IDisplacementVector allOnesVector(IVectorFactory vecFac) {
    double[] arr = new double[vecFac.getDimension()];
    Arrays.fill(arr, 1.);
    return vecFac.newDisplacementVector(arr);
  }

  /**
   * Vector with same value in all dimensions. When using with periodic
   * boundaries, make sure value is less than period in any direction.
   *
   * Note: method created for use with shapes of equal extension in all
   * directions
   *
   * @param val
   *          Value (i.e. coordinate along each dimension)
   * @param dim
   *          Number of dimensions
   * @return Displacement vector
   */
  public static IDisplacementVector allSameVector(final double val,
      final int dim) {
    return new AllSameVector(val, dim);
  }

  private static class AllSameVector implements IDisplacementVector {

    private static final long serialVersionUID = -2816444411932814274L;

    private final int dim;

    private double val;

    AllSameVector(double val, int dim) {
      this.dim = dim;
      this.val = val;
    }

    @Override
    public double[] toArray() {
      double[] rv = new double[dim];
      for (int i = dim - 1; i >= 0; i--) {
        rv[i] = val;
      }
      return rv;
    }

    @Override
    public void set(int d, double newVal) {
      throw new UnsupportedOperationException();
    }

    @Override
    public boolean isNullVector() {
      return val == 0.;
    }

    @Override
    public boolean isEqualTo(IVector<?> v2) {
      if (v2.getDimensions() != dim) {
        return false;
      }
      for (double val2 : v2.toArray()) {
        if (val2 != val) { // NOSONAR: intentional
          return false;
        }
      }
      return true;
    }

    @Override
    public int getDimensions() {
      return dim;
    }

    @Override
    public double get(int d) {
      return val;
    }

    @Override
    public void add(IDisplacementVector disp) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void absolute() {
      throw new UnsupportedOperationException();
    }

    @Override
    public IDisplacementVector times(double sc) {
      return new AllSameVector(val * sc, dim);
    }

    @Override
    public void scale(double sc) {
      this.val *= sc;
    }

    @Override
    public IDisplacementVector plus(IDisplacementVector disp) {
      return disp.plus(val);
    }

    @Override
    public IDisplacementVector plus(double val2) {
      return new AllSameVector(val + val2, dim);
    }

    @Override
    public IDisplacementVector minus(IDisplacementVector disp) {
      return disp.times(-1).plus(val);
    }

    @Override
    public double lengthSquared() {
      return val * val * dim;
    }

    @Override
    public double length() {
      return Math.sqrt(lengthSquared());
    }

    @Override
    public boolean greaterThan(IDisplacementVector v2) {
      throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean greaterOrEqual(IDisplacementVector v2) {
      throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public IDisplacementVector copy() {
      return new AllSameVector(val, dim);
    }
  }

  /**
   * Unmodifiable view of position vector (does not imply that the wrapped
   * vector will not be modified ever)
   *
   * @param pos
   *          Position vector
   * @return Wrapped same vector
   */
  public static IPositionVector unmodifiableVector(IPositionVector pos) {
    return new UnmodifiablePositionVector(pos);
  }

  /**
   * Unmodifiable view of position vector (does not imply that the wrapped
   * vector will not be modified ever)
   *
   * @param disp
   *          IDisplacement vector
   * @return Wrapped same vector
   */
  public static IDisplacementVector unmodifiableVector(
      IDisplacementVector disp) {
    return new UnmodifiableDisplacementVector(disp);
  }

  private static class UnmodifiableVector<T extends IVector<T>>
      implements IVector<T> {

    private static final long serialVersionUID = 3378581498873833790L;

    private final T vector;

    UnmodifiableVector(T vector) {
      this.vector = vector;
    }

    T getVector() {
      return vector;
    }

    @Override
    public double get(int d) {
      return vector.get(d);
    }

    @Override
    public void set(int d, double val) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void absolute() {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(IDisplacementVector disp) {
      throw new UnsupportedOperationException();
    }

    @Override
    public T plus(double val) {
      return vector.plus(val);
    }

    @Override
    public T plus(IDisplacementVector disp) {
      return vector.plus(disp);
    }

    @Override
    public T minus(IDisplacementVector disp) {
      return vector.minus(disp);
    }

    @Override
    public boolean isNullVector() {
      return vector.isNullVector();
    }

    @Override
    public boolean isEqualTo(IVector<?> v2) {
      return vector.isEqualTo(v2);
    }

    @Override
    public int getDimensions() {
      return vector.getDimensions();
    }

    @Override
    public double[] toArray() {
      return vector.toArray();
    }

    @Override
    public T copy() {
      return vector.copy();
    }

    @Override
    public int hashCode() {
      return vector.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
      return vector.equals(obj);
    }

    @Override
    public String toString() {
      return vector.toString();
    }
  }

  private static class UnmodifiablePositionVector
      extends UnmodifiableVector<IPositionVector>implements IPositionVector {

    UnmodifiablePositionVector(IPositionVector vector) {
      super(vector);
    }

    private static final long serialVersionUID = -4239023660042749294L;

    @Override
    public IDisplacementVector displacementTo(IPositionVector p2) {
      return getVector().displacementTo(p2);
    }

    @Override
    public IDisplacementVector scaledDisplacementTo(IPositionVector p2,
        double sc) {
      return getVector().scaledDisplacementTo(p2, sc);
    }

    @Override
    public double distance(IPositionVector p2) {
      return getVector().distance(p2);
    }

    @Override
    public double distanceSquared(IPositionVector p2) {
      return getVector().distanceSquared(p2);
    }

    @Override
    public IPositionVector interpolate(double w1, IVector<?> v2, double w2) {
      return getVector().interpolate(w1, v2, w2);
    }

  }

  private static class UnmodifiableDisplacementVector extends
      UnmodifiableVector<IDisplacementVector>implements IDisplacementVector {

    private static final long serialVersionUID = 6708303187815463915L;

    UnmodifiableDisplacementVector(IDisplacementVector vector) {
      super(vector);
    }

    @Override
    public boolean greaterOrEqual(IDisplacementVector v2) {
      return getVector().greaterOrEqual(v2);
    }

    @Override
    public boolean greaterThan(IDisplacementVector v2) {
      return getVector().greaterThan(v2);
    }

    @Override
    public void scale(double sc) {
      throw new UnsupportedOperationException();
    }

    @Override
    public IDisplacementVector times(double sc) {
      return getVector().times(sc);
    }

    @Override
    public double length() {
      return getVector().length();
    }

    @Override
    public double lengthSquared() {
      return getVector().lengthSquared();
    }

  }

  /**
   * Format array without spaces, with parenthesis (not brackets like
   * {@link java.util.Arrays#toString(double[])}) and custom separator.
   *
   * @param arr
   *          Array
   * @param sepStr
   *          Separator string or character
   * @param formStr
   *          Format string (see {@link String#format(String, Object...)})
   * @return String representation of array
   */
  public static String arrayToString(double[] arr, String sepStr,
      String formStr) {
    StringBuilder s = new StringBuilder("(");
    s.append(String.format(formStr, arr[0]));
    for (int i = 1; i < arr.length; i++) {
      s.append(sepStr);
      s.append(String.format(formStr, arr[i]));
    }
    s.append(')');
    return s.toString();
  }
}
