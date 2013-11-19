/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks;

import java.io.Serializable;

import org.jamesii.core.util.Hook;

/**
 * @param <I>
 *          the type of the information to be forwarded to the computation task
 *          hook
 * @author Jan Himmelspach
 */
public abstract class ComputationTaskHook<I extends Serializable> extends
    Hook<I> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5966769612333185290L;

  /**
   * Instantiates a new computation task hook.
   * 
   * @param oldHook
   *          the old hook
   */
  public ComputationTaskHook(Hook<I> oldHook) {
    super(oldHook);
  }
  
  /**
   * Instantiates a new computation task hook.
   */
  public ComputationTaskHook() {
    super();
  }

}
