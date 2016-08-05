/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

import org.jamesii.core.math.geometry.vectors.IDisplacementVector;

/**
 * Interface to distinguish the two types of position update steps/events
 * generators: Update in steps of given size, adapting time intervals which are
 * needed to travel that far
 *
 * @see IContinuousPositionUpdater
 *
 * @author Arne Bittig
 */
public interface IDiscretePositionUpdater extends IPositionUpdater {

  /**
   * @return Step size in each dimension
   */
  IDisplacementVector getStepSize();

  /**
   * Set the step size, permanently overriding the value set previously
   * (optional operation if previously set step size was already used)
   * 
   * @param steps
   *          New step size
   * @return success value, i.e. if new step size will be used subsequently
   */
  boolean overrideStepSize(IDisplacementVector steps);
}
