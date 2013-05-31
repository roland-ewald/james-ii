/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.agents;

/**
 * The interface {@link IBelief}.
 * 
 * General interface for beliefs that reflect the current state of the world
 * perceived by an agent.
 * 
 * @author Alexander Steiniger
 * 
 * @param <T>
 *          the type of the wrapped belief, e.g., a String representation
 */
public interface IBelief<T> {

  /**
   * Gets the actual belief.
   * 
   * @return the actual belief
   */
  T getBelief();
}
