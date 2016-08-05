/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.shapes;

/**
 *
 * All possible relations one shape (i.e. the resp. solid) can have to another
 *
 * * entirely distinct (no points in common)
 *
 * * identical (all points in common and vice versa)
 *
 * * subset (shape is entirely contained in other shape, but not identical)
 *
 * * superset (shape contains all points of other shape, and more)
 *
 * * overlap (each shape contains points the other does not, but share some)
 */
public enum ShapeRelation {
  /** shape lies completely within other shape (boundaries may touch) */
  SUBSET(true),
  /** shape encompasses other shape completely (boundaries may touch) */
  SUPERSET(true),
  /** shape boundaries are identical */
  IDENTICAL(true),
  /**
   * shapes touch, i.e. they share a (non-empty, but maybe singleton) set of
   * points with a lower cardinality than the space they are embedded in
   */
  TOUCH(false),
  /**
   * shapes overlap, but are not identical and neither is a subset of the other
   * (i.e. both shapes share a set of points with the same cardinality as the
   * space they are embedded in, but each includes points the other does not;
   * touching boundaries are sufficient)
   */
  OVERLAP(true),
  /** shapes do not touch each other */
  DISTINCT(false);

  private final boolean collision;

  private ShapeRelation(boolean collision) {
    this.collision = collision;
  }

  /**
   * Does the value represent a collision? ({@link #SUBSET}, {@link #SUPERSET} ,
   * {@link #OVERLAP} or {@link #IDENTICAL})
   * 
   * @return true if value is not {@link #DISTINCT} or {@link #TOUCH}
   */
  public boolean isCollision() {
    return collision;
  }

  /**
   * If one shape has this relations to a second one, this function returns the
   * relation of the second to the first one
   * 
   * @return Inverse of relationship
   */
  public ShapeRelation inverse() {
    if (this == SUBSET) {
      return SUPERSET;
    } else if (this == SUPERSET) {
      return SUBSET;
    } else {
      return this;
    }
  }
}