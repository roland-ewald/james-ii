/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.event;

/**
 * Basic interface for events in ML-Space (i.e. the classes used in the event
 * queue)
 *
 * @author Arne Bittig
 * @param <T>
 *          Type of triggering component
 * @date 01.06.2012
 */
public interface IMLSpaceEvent<T> {

  /**
   * Get the component that the event refers to (usually a subvolume or a
   * compartment)
   * 
   * @return Event-triggering component
   */
  T getTriggeringComponent();
}