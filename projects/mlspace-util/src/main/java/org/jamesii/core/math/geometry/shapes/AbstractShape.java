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
 * Base class for bounded shapes.
 *
 * Specifies the center coordinate, which should be initialized in the
 * constructor of each derived class -- otherwise getCenter() has to be
 * overridden, and toString() should be. Also, for methods available with or
 * without tolerance parameter, the latter are mapped to the former with
 * tolerance set to 0 (which may be slower than a direct implementation in some
 * cases).
 *
 * Note that if other relevant coordinates for a subclass are not stored
 * relative to the center, the move(...) method here will most likely not
 * produce the desired result (as it changes only the center coordinate) and
 * need to be overridden.
 *
 * @author Arne Bittig
 */
public abstract class AbstractShape implements IModifiableShape {

  /** Serialization ID */
  private static final long serialVersionUID = -5072081129552569544L;

  /** center coordinate (final, but usually mutable) */
  private final IPositionVector center;

  private final IPositionVector unmodViewOfCenter;

  /**
   * maximum extension away from center (final; may be null, i.e. unused in
   * subclass; otherwise usually mutable)
   */
  private final IDisplacementVector maxExtVector;

  private final IDisplacementVector unmodViewOfMaxExtVector;

  /**
   * size (area or size; cached; calculation from properties, e.g.
   * #maxExtVector, should always be possible)
   */
  private double size;

  protected AbstractShape(IPositionVector center,
      IDisplacementVector maxExtVector) {
    this.center = center.copy();
    this.unmodViewOfCenter = Vectors.unmodifiableVector(this.center);
    if (maxExtVector == null) {
      this.maxExtVector = null;
      this.unmodViewOfMaxExtVector = null;
    } else {
      this.maxExtVector = maxExtVector.copy();
      this.unmodViewOfMaxExtVector =
          Vectors.unmodifiableVector(this.maxExtVector);
    }
  }

  @Override
  public IPositionVector getCenter() {
    return unmodViewOfCenter;
  }

  @Override
  public IPositionVector getCenterForModification() {
    return center;
  }

  @Override
  public IDisplacementVector getMaxExtVector() {
    return unmodViewOfMaxExtVector;
  }

  protected IDisplacementVector getMaxExtVectorForModification() {
    return maxExtVector;
  }

  @Override
  public double getMax(int dim) {
    if (dim > 0) {
      return center.get(dim) + getMaxExtVector().get(dim);
    } else {
      return center.get(dim) - getMaxExtVector().get(dim);
    }
  }

  @Override
  public double getMin(int dim) {
    return center.get(dim) - getMaxExtVector().get(dim);
  }

  @Override
  public double getExtension(int dim) {
    if (dim > 0) {
      return getMaxExtVector().get(dim) * 2;
    } else {
      // may not be terribly useful
      return -getMaxExtVector().get(-dim) * 2;
    }
  }

  @Override
  public double getSize() {
    return size;
  }

  /**
   * Set internally stored size value
   *
   * @param size
   *          New size
   */
  protected final void setSize(Double size) {
    this.size = size;
  }

  @Override
  public boolean includesPoint(IPositionVector p) {
    return includesPoint(p, 0);
  }

  @Override
  public ShapeRelation getRelationTo(IShape s2) {
    return getRelationTo(s2, 0.);
  }

  /**
   * Helper methods that throws an exception if zero-size shapes are not allowed
   * and the size is 0
   */
  protected final void checkSize() {
    if (size <= 0.) {
      throw new IllegalArgumentException("Degenerate shape not allowed: "
          + this.toString()
          + (getMaxExtVector() == null ? "" : " with extensions "
              + getMaxExtVector()));
    }
  }

  @Override
  public void move(IDisplacementVector disp) {
    center.add(disp);
  }

  @Override
  public void scaleToSize(double targetVol) {
    scale(Math.pow(targetVol / getSize(), 1. / center.getDimensions()));
  }

  @Override
  public String toString() {
    if (center != null) {
      return className(this) + " at " + center;
    } else {
      // method should be overridden if this can happen in subclass
      return className(this) + " somewhere";
    }

  }

  /**
   * Somewhat generic warning if overlap or boundary size between two shapes
   * cannot be determined for lack of implementation of an appropriate method
   * for the two classes of shapes.
   *
   * @param s2
   *          Other shape whose relation to this cannot be determined exactly
   * @return Warning string (to be displayed or used as error description)
   */
  protected String unknownOtherShapeWarning(IShape s2) {
    return "Shape " + className(s2) + " not known to " + className(this)
        + ". Shape relation operation (e.g. overlap/inclusion check)"
        + "performed on its bounding box.";
    // + " Exceed test may assume maximum extensions along center.";
  }

  /**
   * Name of class of the parameter (used for error message if shape-shape
   * relation cannot be determined)
   *
   * @param o
   *          some object, usually a shape
   * @return name of class of o without package
   */
  private static String className(Object o) {
    String fullName = o.getClass().getName();
    return fullName.substring(fullName.lastIndexOf('.') + 1);
  }
}