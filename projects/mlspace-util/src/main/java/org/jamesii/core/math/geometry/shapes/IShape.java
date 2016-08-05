/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

import java.io.Serializable;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * /** Interface for bounded entities in space
 *
 * @author Arne Bittig
 * @date 17.10.2012 (separation from IModifiableShape)
 */
public interface IShape extends Serializable {

  /**
   * Center point of the shape
   *
   * @return Position vector of the shape's center
   */
  IPositionVector getCenter();

  /**
   * Maximum coordinate of shape along given dimension
   *
   * Example: for a sphere of radius 1.5 around the point (2,4,6), getMax(1)
   * should be 3.5, getMax(2) should be 5.5, etc.
   *
   * - works on actual values, NOT absolute ones
   *
   * @param dim
   *          Dimension along which to look
   * @return Maximum coordinate of shape's boundary along this dim
   */
  double getMax(int dim);

  /**
   * Minimum coordinate of shape along given dimension
   *
   * @param dim
   *          Dimension along which to look
   * @return Minimum coordinate of shape's boundary along this dim
   */
  double getMin(int dim);

  /**
   *
   * @return Vector with the maximal extension of the shape (seen from center)
   *         in each dimension (values may be positive even if max extension is
   *         in the negative direction)
   */
  IDisplacementVector getMaxExtVector();

  /**
   * Extension of shape along given dimension
   *
   * Result should be equal to getMax(dim) - getMin(dim), but may be calculated
   * differently; need not accept negative values for dim
   *
   * @param dim
   *          Dimension along which to look
   * @return Difference of maximum and minimum coordinate along this dim
   */
  double getExtension(int dim);

  /**
   *
   * @return Size (area or volume) of the shape
   */
  double getSize();

  /**
   * Bounding box that completely includes this one and can thus be used for an
   * exclusive overlap check (i.e. if this shape's bounding box does not overlap
   * another shape's bounding box, then the shapes do not overlap).
   *
   * Note that the result need not be distinct from the shape itself (if the
   * shape is an {@link AxisAlignedBox} itself, its {@link #boundingBox()}
   * method should "return this;")
   *
   * @return Simpler shape type surrounding this one
   */
  IShape boundingBox();

  /**
   * Is a given point within the boundaries of this shape?
   *
   * Be wary of rounding errors. For example, a circle with radius 0.1 around
   * (0,0.1) may not be found to include the point (0,0.2) due to rounding. Use
   * {@link IModifiableShape#includesPoint(IPositionVector, double)} to specify
   * a small tolerance (by which the shape is virtually extended).
   *
   * @param p
   *          Position vector
   * @return Point represented by p is within boundaries
   */
  boolean includesPoint(IPositionVector p);

  /**
   * Is a given point within a certain range of the boundaries of this shape?
   *
   * @param p
   *          Position vector
   * @param tol
   *          Tolerance
   * @return Point represented by p is within boundaries
   */
  boolean includesPoint(IPositionVector p, double tol);

  /**
   * Relation to another shape (see {@link ShapeRelation}). Each shape should at
   * least provide accurate results for relations to circles/spheres and
   * axis-aligned box. If the given shape is none that the implementing class is
   * aware of, a warning should be displayed and a check with the bounding box
   * should be performed (unless it is known that the other shape class
   * implements a specific relation check method for this type of shape, in
   * which case this should be called and {@link ShapeRelation#inverse()} be
   * used). See also {@link #getRelationTo(IShape, double)}.
   *
   * @param s2
   *          Other shape
   * @return Relation to that shape
   */
  ShapeRelation getRelationTo(IShape s2);

  /**
   * Relation to another shape (see {@link #getRelationTo(IShape)}). A tolerance
   * parameter may be supplied, which, if positive, shall bias the result away
   * from {@link ShapeRelation#OVERLAP}, i.e. the method shall return
   * {@link ShapeRelation#IDENTICAL} even if the two shape's centers and
   * boundaries differ slightly, but have a distance less than the given
   * tolerance value. A negative value may be used to get the negative effect,
   * but then the two shapes will never be found to be
   * {@link ShapeRelation#IDENTICAL} or to {@link ShapeRelation#TOUCH}.
   *
   * @param s2
   *          Other shape
   * @param tol
   *          Tolerance
   * @return Relation to that shape (see {@link ShapeRelation})
   */
  ShapeRelation getRelationTo(IShape s2, double tol);

  /**
   * How far to move given other shape so that it touches this one (from the
   * outside) -- movement direction should be chosen so that the resulting
   * displacement is minimal in terms of length, but this may be
   * implementation-dependent (as is the behavior in case of ambiguities here,
   * e.g. if both shapes have the same center)
   *
   * @param s2
   *          Other shape
   * @return Displacement vector to move s2 by (so that in absence of rounding
   *         {@link #getRelationTo(IShape)} == {@link ShapeRelation#TOUCH})
   */
  IDisplacementVector dispForTouchOutside(IShape s2);

  /**
   * * How far to move given other shape along a given line so that it touches
   * this one (from the outside)
   *
   * @param s2
   *          Other shape
   * @param oppositeDirection
   *          opposite of direction of line along which to move (e.g. direction
   *          by which this shape just moved, to be moved back slightly)
   * @return Displacement vector to move s2 by (so that in absence of rounding
   *         {@link #getRelationTo(IShape)} == {@link ShapeRelation#TOUCH})
   */
  IDisplacementVector dispForTouchOutside(IShape s2,
      IDisplacementVector oppositeDirection);

  /**
   * How far to move given other shape so that it is completely inside this one,
   * touching it from the inside-- movement direction should be chosen so that
   * the resulting displacement is minimal in terms of length, but this may be
   * implementation-dependent (as is the behaviour in case of ambiguities here,
   * e.g. if both shapes have the same center)
   *
   * @param s2
   *          Other shape
   * @return Displacement vector to move s2 by (so that in absence of rounding
   *         {@link #getRelationTo(IShape)} == {@link ShapeRelation#SUPERSET} ),
   *         or null if this is not possible)
   */
  IDisplacementVector dispForTouchInside(IShape s2);

}