/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.collision;

import java.io.Serializable;

import model.mlspace.entities.spatial.IMoveableEntity;
import model.mlspace.entities.spatial.SpatialEntity;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.vectors.IDisplacementVector;

/**
 * Interface for algorithms for placement of shapes, incl. collision resolution.
 * 
 * Implementations will most likely need access to a spatial index, which will
 * usually be passed into the constructor. However, the spatial index shall not
 * be updated by any method of the implementation itself, i.e. this step must be
 * performed independently! Implementations will usually also depend on a random
 * number generator and/or a position update method.
 * 
 * @author Arne Bittig
 * @param <C>
 *          Type of spatial component
 * @date 11.07.2012
 */
public interface ICollisionResolver<C extends IShapedComponent & IMoveableEntity>
    extends Serializable {

  /**
   * 
   * @param newComp
   *          Spatial component to place (inside given enclosing comp)
   * @param nearComp
   *          Component near which to place (optional; may be expanded to
   *          several comps in the future (varargs))
   * @return success value: true if placement without overlap was possible (and
   *         the respective position updates applied)
   */
  boolean placeNewCompNear(C newComp, C nearComp);

  /**
   * Resolve overlap between two components by moving the first one away from
   * the center of the second
   * 
   * @param movingComp
   *          Spatial component to move away from other
   * @param collComp
   *          Component the former collided with
   * @return Displacement vector that resolves the collision, or null if no
   *         collision-free placement found
   */
  IDisplacementVector resolveCollision(C movingComp, C collComp);

  /**
   * Resolve overlap between two components by moving the first one a bit in
   * opposite direction of its latest move
   * 
   * @param movingComp
   * @param collComp
   * @param recentMove
   * @return Displacement vector that resolves the collision, or null if no
   *         collision-free placement found
   */
  IDisplacementVector resolveCollision(SpatialEntity movingComp,
      SpatialEntity collComp, IDisplacementVector recentMove);

}
