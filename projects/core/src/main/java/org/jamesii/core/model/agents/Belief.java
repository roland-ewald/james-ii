/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.agents;

/**
 * The class {@link Belief}.
 * 
 * Simple implementation of a {@link IBelief}. A belief reflects one aspect of
 * the state of the world, including the agent's state itself, as it is
 * perceived by an agent.
 * 
 * @author Alexander Steiniger
 * 
 * @param <T>
 *          type of the wrapped belief, e.g., a String representation
 */
public class Belief<T> implements IBelief<T> {

  /** actual belief of a certain type */
  private final T belief;

  /**
   * Creates a new instance of {@link Belief} and assigns the given argument to
   * it.
   * 
   * @param belief
   *          the actual belief object that should be assigned to {@link Belief}
   * @throws IllegalArgumentException
   *           if the given belief is <code>null</code>
   */
  public Belief(T belief) {
    // check if given belief is null
    if (belief == null) {
      throw new IllegalArgumentException(
          "Belief cannot be instantiated as the argument given in the constructor is null.");
    }
    this.belief = belief;
  }

  @Override
  public T getBelief() {
    return belief;
  }

  @Override
  public int hashCode() {
    return belief.hashCode();
  }

  /**
   * @throws IllegalArgumentException
   *           if the given object to check for equality is <code>null</code>
   */
  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      throw new IllegalArgumentException(
          "The given argument to check for equality is null.");
    }
    return super.equals(obj);
  }

  @Override
  public String toString() {
    return belief.toString();
  }

}
