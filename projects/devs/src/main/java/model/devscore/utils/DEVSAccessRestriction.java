/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.utils;

import org.jamesii.core.model.AccessRestriction;

/**
 * The Class DEVSAccessRestriction. Used to prevent illegal operations, i.e.,
 * modification of the state if state modifications are not allowed according to
 * the formalism.
 * 
 * @author Jan Himmelspach
 */
public class DEVSAccessRestriction extends AccessRestriction {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6013445790165927480L;

  /** The in. */
  private byte in = 0;

  /** The in lambda. */
  private static final byte IN_LAMBDA_C = 1;

  /** The in state transition. */
  private static final byte IN_STATE_TRANSITION_C = 2;

  /** The in structure change. */
  private static final byte IN_STRUCTURE_CHANGE_C = 4;

  /**
   * Creates a new instance of AccessRestriction.
   */
  public DEVSAccessRestriction() {
    super();
  }

  /**
   * In lambda.
   * 
   * @return true, if inlambda is true
   */
  public final boolean inLambda() {
    return (in & IN_LAMBDA_C) == IN_LAMBDA_C;
  }

  /**
   * In state transition.
   * 
   * @return true, if in state transition
   */
  public final boolean inStateTransition() {
    return (in & IN_STATE_TRANSITION_C) == IN_STATE_TRANSITION_C;
  }

  /**
   * In structure change.
   * 
   * @return true, if in structure change transition
   */
  public final boolean inStructureChange() {
    return (in & IN_STRUCTURE_CHANGE_C) == IN_STRUCTURE_CHANGE_C;
  }

  /**
   * In user function.
   * 
   * @return true, if successful
   */
  public final boolean inUserFunction() {
    return (inLambda() || inStateTransition());
  }

  /**
   * Sets the in lambda.
   * 
   * @param inLambda
   *          the new in lambda
   */
  public final void setInLambda(boolean inLambda) {
    this.in |= IN_LAMBDA_C;
  }

  /**
   * Sets the in state transition.
   * 
   * @param inStateTransition
   *          the new in state transition
   */
  public final void setInStateTransition(boolean inStateTransition) {
    this.in |= IN_STATE_TRANSITION_C;

  }

  /**
   * Sets the in structure change.
   * 
   * @param inStructureChange
   *          the new in structure change
   */
  public final void setInStructureChange(boolean inStructureChange) {
    this.in |= IN_STRUCTURE_CHANGE_C;
  }

}
