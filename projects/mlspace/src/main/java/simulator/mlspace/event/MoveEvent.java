/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.event;

import model.mlspace.entities.spatial.SpatialEntity;

/**
 * @author Arne Bittig
 * @date 01.06.2012
 */
public class MoveEvent implements ISpatialEntityEvent {

  private final SpatialEntity comp;

  // private final boolean flexiblyTimed; // TODO

  /**
   * for compartment position updates, time when event entity was last updated
   * (relevant as these are not exponentially distributed, meaning that if the
   * event has to be updated due to outside influence, the time passed since the
   * last update has to be considered -- unlike with NSM-events and first-order
   * reactions whose intervals are exponentially distributed, i.e. following a
   * memory-less process
   */
  private double timeOfLastUpdate;

  /**
   * @param comp
   * @param timeOfLastUpdate
   */
  // public MoveEvent(SpatialEntity comp, double timeOfLastUpdate) {
  // this(comp, timeOfLastUpdate, false);
  // }
  //
  // /**
  // * @param comp
  // * @param timeOfLastUpdate
  // * @param flexiblyTimed
  // */
  public MoveEvent(SpatialEntity comp, double timeOfLastUpdate
  // ,boolean flexiblyTimed
  ) {
    this.comp = comp;
    this.timeOfLastUpdate = timeOfLastUpdate;
    // this.flexiblyTimed = flexiblyTimed;
  }

  // /**
  // * Return true if the event that happens depends on the elapsed time (e.g.
  // * the Brownian-motion-like position update in continuous space
  // simulation)
  // * and does not strictly need to be executed at the moment it was
  // scheduled
  // * for, return false if it really should be executed at the time it was
  // * scheduled for (unless an accurracy-losing approximation is used, as in
  // * GMP vs. NSM).
  // *
  // * @return Flag whether timing of event is flexible
  // */
  // public boolean isFlexiblyTimed() {
  // return flexiblyTimed;
  // }

  /**
   * @return the timeOfLastUpdate
   */
  public double getTimeOfLastUpdate() {
    return timeOfLastUpdate;
  }

  /**
   * @param timeOfLastUpdate
   *          the timeOfLastUpdate to set
   */
  public void setTimeOfLastUpdate(double timeOfLastUpdate) {
    this.timeOfLastUpdate = timeOfLastUpdate;
  }

  @Override
  public SpatialEntity getTriggeringComponent() {
    return comp;
  }

  @Override
  public String toString() {
    return "move of " + comp + " (last: " + timeOfLastUpdate
    // + (flexiblyTimed ? " (flex))" : "(fix))")
        + ')';
  }
}
