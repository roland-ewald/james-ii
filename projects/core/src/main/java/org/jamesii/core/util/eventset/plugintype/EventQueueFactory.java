/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.eventset.plugintype;

import org.jamesii.core.data.report.IReport;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.IEventQueue;

/**
 * The base class for all factories for creating event queues.
 * 
 * @author Jan Himmelspach
 */
public abstract class EventQueueFactory extends Factory<IEventQueue<?, Double>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2223859699427961147L;

  /**
   * Return a new instance of the event queue to be used.
   * 
   * @param <E>
   *          type of the entries in the event queue
   * 
   * @param parameter
   *          event queue parameters
   * 
   * @return the event queue
   */
  public abstract <E> IEventQueue<E, Double> createDirect(
      ParameterBlock parameter);

  @Override
  public IEventQueue<?, Double> create(ParameterBlock parameter) {
    return createDirect(parameter);
  }

  /**
   * Return a new instance of the event queue to be used and report the creation
   * of the queue to the report passed.
   * 
   * @param report
   *          report
   * @param parameter
   *          the parameter
   * @return the i event queue<?, double>
   */
  @SuppressWarnings("unchecked")
  public <E> IEventQueue<E, Double> create(IReport report,
      ParameterBlock parameter) {
    IEventQueue<?, Double> result = createDirect(parameter);
    if (report != null) {
      report.addEntry("Event queue created", result, parameter);
    }
    return (IEventQueue<E, Double>) result;
  }

  /**
   * Return a rough efficiency index. A factory which returns a highly efficient
   * simulator will be preferably used.
   * 
   * @return 0 for an inefficient and 1 for a highly efficient one
   */
  public abstract double getEfficencyIndex();

  /**
   * Return an {@link EventIdentityBehavior}. Allows the
   * {@link AbstractEventQueueFactory} to filter out event queues with
   * unsuitable assumptions for the given problem.
   * 
   * @return the queue's behavior regarding identity checks
   */
  public abstract EventIdentityBehavior getEventIdentityBehaviour();

  /**
   * Return an {@link EventOrderingBehavior}. Allows the
   * {@link AbstractEventQueueFactory} to filter out event queues with
   * unsuitable assumptions for the given problem.
   * 
   * @return the queue's behavior regarding the order of events at the same time
   */
  public EventOrderingBehavior getEventOrderingBehaviour() {
    return EventOrderingBehavior.UNREPRODUCIBLE;
  }

  @Override
  public String toString() {
    String s = super.toString();
    s += "\n Ordering efficiency index: " + getEfficencyIndex();
    return s;
  }

}
