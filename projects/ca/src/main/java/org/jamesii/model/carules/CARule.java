/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import org.jamesii.core.model.symbolic.convert.ISymbolicModelElement;

/**
 * Implements a CA rule that is used in the {@link CARulesModel} and represents
 * one rule that can change a cell's state if {@link #getCurrentCondition()},
 * {@link #getPreCondition()} is met and the probability allows the rule to
 * fire.
 * 
 * @author Stefan Rybacki
 */
public class CARule implements ISymbolicModelElement {

  /**
   * The probability.
   */
  private double probability = 1d;

  /**
   * True is proability != 1d
   */
  private boolean hasProb = false;

  /**
   * The destination state.
   */
  private int destinationState;

  /**
   * The pre condition.
   */
  private ICACondition preCondition;

  /**
   * The current condition.
   */
  private ICACondition currentCondition;

  /**
   * The comment.
   */
  private String comment;

  /**
   * Instantiates a new CA rule.
   * 
   * @param currentCondition
   *          the current state condition for instance can restrict the rule to
   *          only specific cells
   * @param preCondition
   *          the pre condition is used to restrict the rule to only specific
   *          combination of neighborhood cells
   * @param destinationState
   *          the destination state
   * @param probability
   *          the probability the rule fires
   */
  public CARule(ICACondition currentCondition, ICACondition preCondition,
      int destinationState, double probability) {
    this(currentCondition, preCondition, destinationState, probability, null);
  }

  /**
   * Instantiates a new CA rule.
   * 
   * @param currentCondition
   *          the current state condition for instance can restrict the rule to
   *          only specific cells
   * @param preCondition
   *          the pre condition is used to restrict the rule to only specific
   *          combination of neighborhood cells
   * @param destinationState
   *          the destination state
   * @param probability
   *          the probability the rule fires
   * @param comment
   *          the rule's comment
   */
  public CARule(ICACondition currentCondition, ICACondition preCondition,
      int destinationState, double probability, String comment) {
    this.probability = probability;
    if (Double.compare(probability, 1d) != 0) {
      hasProb = true;
    }
    this.preCondition = preCondition;
    this.destinationState = destinationState;
    this.currentCondition = currentCondition;
    this.comment = comment;
  }

  @Override
  public String toString() {
    return probability + ":" + preCondition + "->" + destinationState;
  }

  /**
   * Gets the pre condition.
   * 
   * @return the pre condition
   */
  public ICACondition getPreCondition() {
    return preCondition;
  }

  /**
   * Gets the probability.
   * 
   * @return the probability
   */
  public double getProbability() {
    return probability;
  }

  /**
   * Gets the probability.
   * 
   * @return the probability
   */
  public boolean hasProbability() {
    return hasProb;
  }

  /**
   * Gets the destination state.
   * 
   * @return the destination state
   */
  public int getDestinationState() {
    return destinationState;
  }

  /**
   * Gets the current condition.
   * 
   * @return the current condition
   */
  public ICACondition getCurrentCondition() {
    return currentCondition;
  }

  @Override
  public String getComment() {
    return comment;
  }
}
