/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * Collection of static methods for dealing with shapes
 *
 * @author Arne Bittig
 * @date Apr 20, 2012 (some earlier methods moved here)
 */
public final class ShapeUtils {

  /**
   * Container for the result of
   * {@link ShapeUtils#splitAllDim(AxisAlignedBox, double)}
   *
   * @author Arne Bittig
   * @date 24.11.2012
   */
  public static class SplitResult implements Serializable {

    private static final long serialVersionUID = 7138200818479835030L;

    private final List<AxisAlignedBox> boxes;

    private final int[] numberPerDimension;

    /**
     * @param boxes
     * @param numberPerDimension
     */
    SplitResult(List<AxisAlignedBox> boxes, int[] numberPerDimension) {
      this.boxes = boxes;
      this.numberPerDimension = numberPerDimension;
    }

    /**
     * @return The (smaller) boxes resulting from the split
     */
    public List<AxisAlignedBox> getBoxes() {
      return boxes;
    }

    /**
     * @return Number of boxes in each dimension (first dimension comes ... TODO
     *         in return value of {@link #getBoxes()})
     */
    public int[] getNumberPerDimension() {
      return numberPerDimension;
    }

    @Override
    public String toString() {
      return Arrays.toString(numberPerDimension) + " boxes: " + boxes;
    }
  }

  private ShapeUtils() {
  }

  /**
   * Get neighbor relation of shapes: calulate area of shared surface / length
   * of shared side and distances from center to intersection. Wrapper method
   * that tests whether both shapes are {@link AxisAlignedBox}es and calls
   * {@link #getNeighborRelation(AxisAlignedBox, AxisAlignedBox, double)} if so,
   * otherwise returns null if any shape is a sphere and throws an
   * {@link IllegalArgumentException} if neither is.
   *
   * @param s1
   *          One shape
   * @param s2
   *          Second shape (probably) touching s1
   * @param delta
   *          Tolerance parameter (>= 0)
   * @return Area of shared surface and distances from surface
   * @see #getNeighborRelation(AxisAlignedBox, AxisAlignedBox, double)
   */
  public static double[] getNeighborRelation(IShape s1, IShape s2, double delta) {
    if (s1 instanceof AxisAlignedBox && s2 instanceof AxisAlignedBox) {
    return getNeighborRelation((AxisAlignedBox) s1, (AxisAlignedBox) s2, delta);
    }
    if (s1 instanceof Sphere || s2 instanceof Sphere) {
      return new double[3]; // NOSONAR: 3-element return values
    } else {
      throw new UnsupportedOperationException(
          "Neighbor relation can only be determined for boxes!");
    }
  }

  /**
   * Calculate the area of the boundary shared between two neighboring shapes,
   * and the direction in which to find this boundary. Shall return 0 if the
   * shapes do not touch, i.e. do not share a boundary (beware of rounding
   * errors!). If n is the dimension of the shape (e.g. 3), boundary refers to
   * an n-1 dimensional structure (e.g. surface). If no (hyper)surface is
   * shared, 0 is returned, too, even if the shapes overlap. Two overlapping
   * spheres, for example, would share a part of their surfaces, but this part
   * is a circle and thus n-2 dimensional, i.e. has size 0 in n-1 dimensions.
   *
   * Note that any non-0 result should be correct for disjoint structures only
   * (i.e. when this.overlaps(s2) is still false), i.e. if there are points
   * within both shapes that are not on either surface, the result need not be
   * correct. Note also that in periodic boundary conditions, touching on both
   * sides along the same dimension (i.e. the shapes together cover the entire
   * space in that dimension) will not be discernible from the more usual case.
   *
   * @param s1
   *          One shape
   * @param s2
   *          Second shape (probably) touching s1
   * @param delta
   *          Tolerance parameter (>= 0)
   * @return Area of shared surface / length of shared side (first value);
   *         distance from this shape's center orthogonal to shared surf (second
   *         value); distance from other shape's center to ~ (third) (note: if
   *         the first returned value is 0, so are the two others)
   */
  public static double[] getNeighborRelation(AxisAlignedBox s1,
      AxisAlignedBox s2, double delta) {
    IDisplacementVector centerDisp =
        s1.getCenter().displacementTo(s2.getCenter());
    centerDisp.absolute();

    IDisplacementVector s1maxExt = s1.getMaxExtVector();
    IDisplacementVector s2maxExt = s2.getMaxExtVector();
    IDisplacementVector meSum = s1maxExt.plus(s2maxExt);
    IDisplacementVector diff = centerDisp.minus(meSum);
    // CHECK: diff.absolute(); ?! for later < delta comparison?

    int dim = s1.getCenter().getDimensions();
    int touchDim = 0;
    for (int d = 1; d <= dim; d++) {
      if (Math.abs(diff.get(d)) <= delta && Math.abs(meSum.get(d)) > delta) {
        // (meSum.get(d) corrects for "vector overflow" in case of
        // periodic boundaries and shapes spanning the entire space in
        // one dimension)
        if (touchDim == 0) {
          touchDim = d; // dimension in which shapes touch found
        } else {
          // shapes "touch" in more than one dimension...
          // i.e. <=(n-2)-dim. intersection
          return new double[3]; // NOSONAR: 3-element return values
        }
      }
    }
    if (touchDim == 0) {
      return new double[3]; // NOSONAR: 3-element return values
    }

    double sharedSurf = getSharedSurface(s1, s2, touchDim, dim);
    return new double[] { sharedSurf, s1maxExt.get(touchDim),
        s2maxExt.get(touchDim) };
  }

  private static double getSharedSurface(AxisAlignedBox box1,
      AxisAlignedBox box2, int touchDim, int totalDims) {
    double sharedSurf = 1; // 1 is returned if dim == 1
    for (int d = 1; d <= totalDims; d++) {
      if (d != touchDim) {
        double sSurfMinD = Math.max(box1.getMin(d), box2.getMin(d));
        double sSurfMaxD = Math.min(box1.getMax(d), box2.getMax(d));
        sharedSurf *= Math.max(sSurfMaxD - sSurfMinD, 0);
        // if sSurfMaxD - sSurfMinD < 0, then this and s2 actually
        // share only a dim-2 dimensional entity or do not touch at
        // all (but are aligned in a way that could be mistaken for
        // touching)
      }
    }
    return sharedSurf;
  }

  /**
   * Get shape of the intersection of this and another cuboid (i.e.
   * {@link AxisAlignedBox}). A reference to one of the shapes may be returned
   * if the intersection is identical to it, i.e. if one encompasses the other
   * or they are identical; otherwise, a new {@link AxisAlignedBox} is created.
   *
   * @param s1
   *          One cuboid
   * @param s2
   *          Other cuboid
   * @return new Cuboid of the intersection (null if none)
   */
  public static AxisAlignedBox intersection(AxisAlignedBox s1, AxisAlignedBox s2) {
    ShapeRelation rel = s1.getRelationTo(s2);
    switch (rel) {
    case DISTINCT:
    case TOUCH:
      return null;
    case IDENTICAL:
    case SUBSET:
      return s1;
    case SUPERSET:
      return s2;
    case OVERLAP:
      IPositionVector newLowerCorner =
      Vectors.max(s1.getCenter().minus(s1.getMaxExtVector()), s2
          .getCenter().minus(s2.getMaxExtVector()));
      IPositionVector newUpperCorner =
          Vectors.min(s1.getCenter().plus(s1.getMaxExtVector()), s2.getCenter()
              .plus(s2.getMaxExtVector()));
      return new AxisAlignedBox(newLowerCorner, newUpperCorner);
    default: // all shape relation cases should be covered above
      throw new UnsupportedOperationException("Cannot handle relation " + rel
          + " between " + s1 + " and " + s2 + ".");
    }
  }

  /**
   *
   * @param dim
   *          Number of dimensions
   * @return Minimum volume of n-sphere with lattice point covering property
   */
  public static Double minSphereVolForLatticePointCoveringProperty(int dim) {
    return Sphere.calculateVolume(Math.sqrt(dim) / 2., dim);
  }

  /**
   * Shapes representing this one split into n parts along each dimension with
   * same proportions (i.e. n^dim shapes in total, this one left unchanged)
   *
   * @param boxToSplit
   *          Shape to split
   * @param n
   *          Number of splits along each dimension
   * @return List of (n^dim) cuboids created from this one
   */
  public static Collection<AxisAlignedBox> splitEqually(
      AxisAlignedBox boxToSplit, int n) {
    if (n <= 1) {
      throw new IllegalArgumentException("Cannot split shape in less than two.");
    }
    Collection<AxisAlignedBox> splits = new ArrayList<>();
    splits.add(boxToSplit);
    for (int d = 1; d <= boxToSplit.getCenter().getDimensions(); d++) {
      double[] where = new double[n - 1];
      double min = boxToSplit.getMin(d);
      double steps = boxToSplit.getExtension(d) / n;
      for (int i = 1; i < n; i++) {
        where[i - 1] = min + i * steps;
      }
      Collection<AxisAlignedBox> newSplits = new ArrayList<>();
      for (AxisAlignedBox prevSplit : splits) {
        newSplits.addAll(ShapeUtils.split1dActualWork(prevSplit, d, where));
      }
      splits = newSplits;
    }
    return splits;
  }

  /**
   * Split into (almost) square/cubic sub-shapes. Returns a list of shapes (
   * {@link AxisAlignedBox}es) and an int array containing the number of boxes
   * along each dimension. The lists starts with the box with the lowest
   * coordinates in each dimension, followed by the next ones along the _last_
   * dimension. If numSplits is the second return value, the list element at (0
   * based) index numSplits[numSplits.length-1] is the shape neighboring the
   * first one (i.e. at index 0) in the last-but-1st dimension. The shape
   * neighboring the one at index 0 along the first dimension is at the list
   * position given by the product of all numbers in numSplits except for the
   * first one (i.e. at index 0; the product of all numbers in numSplits is the
   * size of the returned list).
   *
   * @param boxToSplit
   *          Shape to split
   * @param targetLength
   *          desired size length of resulting sub-shapes (next higher integer
   *          ratio of shape side length used if necessary)
   * @return List of resulting shapes and number of shapes in each dimension
   */
  public static SplitResult splitAllDim(AxisAlignedBox boxToSplit,
      double targetLength) {
    if (targetLength <= 0) {
      throw new IllegalArgumentException("Target side length"
          + " needs to be positive, not " + targetLength);
    }
    ArrayList<AxisAlignedBox> splits = new ArrayList<>();
    splits.add(boxToSplit);
    int dimensions = boxToSplit.getCenter().getDimensions();
    int[] nPerDim = new int[dimensions];
    for (int d = 1; d <= dimensions; d++) {
      double nd = boxToSplit.getExtension(d) / targetLength;
      int n = (int) nd;
      if (n == 0 || nd - n > 0.) { // CHECK: epsilon?
        n++;
      }
      double steps = boxToSplit.getExtension(d) / n;
      double[] where = new double[n - 1];
      nPerDim[d - 1] = n;
      double min = boxToSplit.getMin(d);
      for (int i = 1; i < n; i++) {
        where[i - 1] = min + i * steps;
      }
      if (where.length > 0) {// in one or more dimensions, splitting may
        // not be required
        ArrayList<AxisAlignedBox> newSplits = new ArrayList<>();
        for (AxisAlignedBox prevSplit : splits) {
          newSplits.addAll(ShapeUtils.split1dActualWork(prevSplit, d, where));
        }
        splits = newSplits;
      }
    }
    return new SplitResult(splits, nPerDim);
  }

  /**
   * Split AxisAlignedBox along given (hyper)plane, if possible
   *
   * @param boxToSplit
   *          Shape to split
   * @param dim
   *          Dimension along which to split
   * @param where
   *          Coordinates for splitting
   * @return List of new Cuboids (in ascending order by coordinates along dim;
   *         empty list if where is empty)
   * @throws IllegalArgumentException
   *           if where contains values outside the extension of the shape along
   *           the given dimension
   */
  public static List<AxisAlignedBox> split1d(AxisAlignedBox boxToSplit,
      int dim, double... where) {
    // special cases: no or wrong splitting coordinates
    if (where.length == 0) {
      return Collections.emptyList();
    }
    Arrays.sort(where);
    if (where[0] <= boxToSplit.getMin(dim)) {
      throw new IllegalArgumentException("Splitting coordinate " + where[0]
          + " is not within shape " + boxToSplit);
    }
    if (where[where.length - 1] >= boxToSplit.getMax(dim)) {
      throw new IllegalArgumentException("Warning: Splitting coordinate "
          + where[where.length - 1] + " is not within shape " + boxToSplit);
    }
    return split1dActualWork(boxToSplit, dim, where);
  }

  /**
   * Actual splitting for split1d, without the checks for valid and sorted
   * splitting coordinates
   *
   * @param boxToSplit
   *          Shape to split
   * @param dim
   *          Dimension along which to split
   * @param where
   *          Coordinates for splitting
   * @return List of new Cuboids (in ascending order by coordinates along dim;
   *         empty list if where is empty)
   */
  private static List<AxisAlignedBox> split1dActualWork(
      AxisAlignedBox boxToSplit, int dim, double[] where) {
    // the actual method
    List<AxisAlignedBox> newShapes = new ArrayList<>(where.length - 1);
    for (int i = 0; i <= where.length; i++) {
      double lower = i > 0 ? where[i - 1] : boxToSplit.getMin(dim);
      double upper = i < where.length ? where[i] : boxToSplit.getMax(dim);
      if (lower == upper) { // NOSONAR: really! for sure!
        continue;
      }
      IPositionVector newCenter = boxToSplit.getCenter().copy();
      newCenter.set(dim, (upper + lower) / 2.0);
      IDisplacementVector newHalfDiag = boxToSplit.getMaxExtVector().copy();
      newHalfDiag.set(dim, (upper - lower) / 2.0);
      newShapes.add(new AxisAlignedBox(newCenter, newHalfDiag));
    }
    return newShapes;
  }
}