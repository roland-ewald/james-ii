/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry;

import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * Interface for everything that has a shape to allow some general operations on
 * their shapes and comparators for comparison by selected shape properties
 * (e.g. size/volume).
 *
 * @author Arne Bittig
 */
public interface IShapedComponent extends java.io.Serializable {

  /**
   * Get position of shaped component (some reference point; usually the center;
   * cf. {@link #getShape()} and {@link IShape#getCenter()})
   * 
   * @return Entity's position
   */
  IPositionVector getPosition();

  /**
   * @return Shape of the component
   */
  IShape getShape();

  /**
   * Get surrounding component, if any. When shaped components can be
   * hierarchically nested, each component may be inside another one. Usually,
   * the surrounding component's shape should completely include this
   * component's shape (in other words, {@link IShape#getRelationTo(IShape)}
   * used on this component's shape with parameter {@link #getEnclosingEntity()} .
   * {@link #getShape()} should return
   * {@link org.jamesii.core.math.geometry.shapes.ShapeRelation#SUBSET});
   * however, subclasses are not required to check this for each component and
   * it need not be generally true for each subclass.
   * 
   * Note: the enclosing component will usually be a
   * {@link model.mlspace.entities.spatial.SpatialEntity}
   * 
   * Note 2: the result of the call for one component may change over time, i.e.
   * subclasses may have a setter (which is not part of this interface)
   * 
   * @return Enclosing component, null if none
   */
  IShapedComponent getEnclosingEntity();

}
