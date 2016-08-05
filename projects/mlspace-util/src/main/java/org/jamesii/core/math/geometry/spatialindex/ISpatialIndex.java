/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.geometry.spatialindex;

import java.util.Collection;
import java.util.List;

import org.jamesii.core.math.geometry.IShapedComponent;
import org.jamesii.core.math.geometry.shapes.IShape;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * Spatial index, holding references to shaped components and their positions
 * and performing collision checks on them. "Components" are, for the purpose of
 * spatial cell biological simulation, usually compartments, or all model
 * entities with spatial extension, but can also be the cells of an underlying
 * grid introduced for simulation (e.g. subvolumes).
 *
 * @author Arne Bittig
 *
 * @param <C>
 *          Shaped component type
 */

public interface ISpatialIndex<C extends IShapedComponent> extends
    java.io.Serializable {

  /**
   * "Lazy initialization" of the grid (and, if necessary, related variables in
   * subclasses). Must can only be called once (and usually not at all is so if
   * the surrounding shape is already known at the time of the constructor
   * call), i.e. an exception should be thrown if initialization is attempted
   * more than once.
   *
   * @param surroundingShape
   *          Shape surrounding all entities ever indexed
   * @return flag whether the surroundingShape is actually relevant to the
   *         implementing class
   * @throws IllegalStateException
   *           if spatial index is already initialized and changes are not
   *           supported
   */
  boolean init(IShape surroundingShape);

  /**
   * Check whether given component overlaps any registered component (this may
   * entail pairwise overlap check with every registered component; if so,
   * {@link org.jamesii.core.math.geometry.spatialindex.SpatialIndexFactory#supportsBetterThanPairwiseCollisionCheck()}
   * should return false). The given component may or may not be registered with
   * the spatial index, and if it is registered, its position need not be
   * {@link #updateCompPos(IShapedComponent) updated} beforehand. Its
   * registration status shall not change by invocation of this method.
   *
   * @param c
   *          Component
   * @return Collection of components overlapping c (empty collection if no
   *         collisions)
   */
  List<C> collidingComps(C c);

  /**
   * Check whether component overlaps any of the given other component (see
   * {@link IShape#getRelationTo(IShape)}). It is left to the implementation how
   * to deal with components in the second parameter that are unregistered (e.g.
   * register; ignore; throw RuntimeException; consider for collision check, but
   * no not register)
   *
   * @param c
   *          Component
   * @param cc
   *          Collection of components to check overlap with
   * @return Collection of components overlapping c (empty collection if no
   *         collisions)
   */
  List<C> collidingComps(C c, Collection<C> cc);

  /**
   * Boundaries of area covered (for resolution of cases where
   * {@link #isOutOfBounds(IShapedComponent)} returned true)
   *
   * @return Boundary shape
   */
  // if ever an infinite space implementation is added, this might as well
  // return null (adapt documentation in this case!)
  IShape getBoundaries();

  /**
   * Check whether components is within boundaries covered (may fail when called
   * before {@link #init(IShape)})
   *
   * @param c
   *          Component to check for being out of bounds (may or may not be
   *          registered, position is not registered/updated when calling this
   *          method)
   *
   * @return system boundaries
   */
  boolean isOutOfBounds(C c);

  /**
   * Components whose current position is not equal to their registered position
   * (mostly for debugging purposes)
   *
   * @return Components whose position (see {@link IShape#getCenter()}) is not
   *         equal to the one registered
   */
  Collection<C> notUpToDate();

  /**
   * Make shaped component known to spatial index (at position given by its
   * {@link IShapedComponent#getPosition()} method; note that the spatial index
   * shall make a copy of the position, so changes in the shape should not be
   * assumed to be directly known to the spatial index, but need to be
   * "announced" using {@link #updateCompPos(IShapedComponent)}).
   *
   * If the spatial index covers only a limited area and it supports lazy
   * initialization (i.e. this area does not have to be defined in the
   * constructor), the first component registered might be taken as the
   * definition of the surrounding area into which all other components must
   * fit. It may then be ignored for collision check purposes.
   *
   * @param c
   *          Component
   * @throws IllegalArgumentException
   *           if c is already registered
   */
  void registerNewEntity(C c);

  /**
   * "Announce" a position change of an already registered component (see
   * {@link #registerNewEntity(IShapedComponent)}; the new position need not not
   * actually be different from the old)
   *
   * @param c
   *          Component
   * @return Previously registered position of c
   * @throws IllegalArgumentException
   *           if c is not yet registered
   */
  IPositionVector updateCompPos(C c);

  /**
   * Get position associated with given component
   *
   * @param c
   *          Component
   * @return Position of c registered with spatial index (null if none)
   */
  IPositionVector getRegisteredPosition(C c);

  /**
   * Remove entry from spatial index
   *
   * @param c
   *          Component to remove from index
   * @return Previously registered position of c
   * @throws IllegalArgumentException
   *           if c is not registered
   */
  IPositionVector unregisterComp(C c);

}
