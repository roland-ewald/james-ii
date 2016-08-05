/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.brownianmotion;

/**
 * Interface to distinguish the two types of position update steps/events
 * generators: Update by random distance according to given elapsed time
 *
 * @see IDiscretePositionUpdater
 *
 * @author Arne Bittig
 */
public interface IContinuousPositionUpdater extends IPositionUpdater {

  /**
   * Get the currently set average distance of updates returned by
   * {@link #getPositionUpdate(double, model.mlspace.entities.spatial.IMoveableEntity)}
   * 
   * @return average distance of update steps created
   */
  double getTravelDistance();
}
