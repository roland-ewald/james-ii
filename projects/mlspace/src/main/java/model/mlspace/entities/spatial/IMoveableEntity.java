/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.spatial;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;
import org.jamesii.core.math.geometry.vectors.IPositionVector;

/**
 * Interface exposing the methods needed for determining position updates (as
 * done by the classes implementing, for example,
 * {@link simulator.mlspace.brownianmotion.IPositionUpdater})
 *
 * @author Arne Bittig
 */
public interface IMoveableEntity {

  /**
   * @return entity's position (some reference point; usually the center)
   */
  IPositionVector getPosition();

  /**
   * Drift of the entity
   * 
   * @return value of the entity's drift attribute
   */
  IDisplacementVector getDrift();

  /**
   * Diffusion constant of the entity
   * 
   * @return value of the entity's diffusion attribute
   */
  double getDiffusionConstant();

  /**
   * @param disp
   *          Displacement
   */
  void move(IDisplacementVector disp);

}