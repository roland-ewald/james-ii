/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import java.util.Arrays;
import java.util.Collection;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;
import org.jamesii.core.math.geometry.vectors.Vectors;

/**
 * Shape composed of other shapes
 *
 * Calls to getProperty-style methods of the composite shape result in calls of
 * the respective method for all included primitive shapes. Methods changing
 * this shape (e.g. move, scale) will change the included primitive shapes as
 * well.
 *
 * Creation date: 25.03.2011 (unfinished)
 *
 * @author Arne Bittig
 *
 */
public class CompositeShape implements IShape {

  private static final long serialVersionUID = -7397044005822367014L;

  private final Collection<IShape> shapes;

  private IPositionVector center = null;

  private IDisplacementVector maxExtPos = null;

  private IDisplacementVector maxExtNeg = null; // center is not always the

  // middle

  /**
   * Varargs constructor
   *
   * @param shapes
   *          Individual shapes or array of shapes (NOT copied)
   */
  public CompositeShape(IShape... shapes) {
    this.shapes = Arrays.asList(shapes);
  }

  /**
   * Average center of composite shapes, weighted by volume (may be outside any
   * of the actual shape)
   */

  @Override
  public IPositionVector getCenter() {
    if (this.center == null) {
      this.center = centerOfGravity(shapes);
    }
    return this.center;
  }

  /**
   * Center of a shape composed of smaller shapes
   *
   * @param s
   *          smaller shapes
   * @return center of gravity of combination of shapes in s (assuming mass
   *         proportional to their area/volume and no overlap between any pair
   *         of shapes in s)
   */
  public static IPositionVector centerOfGravity(Collection<IShape> s) {
    IPositionVector center = null; // null vector
    double volSum = 0.;
    for (IShape shape : s) {
      volSum += shape.getSize();
    }
    double volCumSum = 0.; // for correct weights for linComb
    for (IShape shape : s) {
      volCumSum += shape.getSize(); // inefficient, but working
      if (center == null) {
        center = shape.getCenter();
      } else {
        center =
            center.interpolate(volCumSum / volSum, shape.getCenter(),
                shape.getSize() / volSum);
      }
    }
    return center;

  }

  @Override
  public double getMax(int dim) {
    if (maxExtPos == null) {
      getMaxExtVector();
    }
    if (dim > 0) {
      return center.get(dim) + maxExtPos.get(dim);
    } else {
      return center.get(dim) - maxExtNeg.get(dim);
    }
  }

  @Override
  public double getMin(int dim) {
    if (maxExtPos == null) {
      getMaxExtVector();
    }
    return center.get(dim) - maxExtNeg.get(dim);
  }

  @Override
  public double getExtension(int dim) {
    if (maxExtPos == null) {
      getMaxExtVector();
    }
    if (dim > 0) {
      return maxExtPos.get(dim) + maxExtNeg.get(dim);
    } else {
      return -maxExtPos.get(dim) - maxExtNeg.get(dim);
    }
  }

  @Override
  public IDisplacementVector getMaxExtVector() {
    if (this.maxExtPos == null) {
      IPositionVector cen = getCenter(); // may not have been calculated
      // yet
      IPositionVector minExtAbs = cen.copy();
      IPositionVector maxExtAbs = cen.copy();

      for (IShape shape : this.shapes) {
        for (int d = 1; d <= cen.getDimensions(); d++) {
          double smin = shape.getMin(d);
          if (smin < minExtAbs.get(d)) {
            minExtAbs.set(d, smin);
          }
          double smax = shape.getMax(d);
          if (smax > maxExtAbs.get(d)) {
            maxExtAbs.set(d, smax);
          }
        }
      }
      this.maxExtNeg = minExtAbs.displacementTo(cen);
      this.maxExtPos = cen.displacementTo(maxExtAbs);
    }
    return Vectors.max(maxExtPos, maxExtNeg);
  }

  @Override
  public double getSize() {
    double volSum = 0.;
    for (IShape shape : this.shapes) {
      volSum += shape.getSize();
    }
    return volSum;
  }

  @Override
  public boolean includesPoint(IPositionVector p) {
    for (IShape shape : this.shapes) {
      if (shape.includesPoint(p)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean includesPoint(IPositionVector p, double tol) {
    for (IShape shape : this.shapes) {
      if (shape.includesPoint(p, tol)) {
        return true;
      }
    }
    return false;

  }

  @Override
  public ShapeRelation getRelationTo(IShape s2) {
    throw new UnsupportedOperationException();
  }

  @Override
  public ShapeRelation getRelationTo(IShape s2, double tol) {
    throw new UnsupportedOperationException();
    // complicated: if shapes.get(1).getRelTo(s2) == OVER &&
    // shapes.get(2).getRelTo(s2) == OVER, this.getRelTo not determined yet
  }

  @Override
  public IShape boundingBox() {
    return new AxisAlignedBox(center.minus(maxExtNeg), center.plus(maxExtPos));
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

}
