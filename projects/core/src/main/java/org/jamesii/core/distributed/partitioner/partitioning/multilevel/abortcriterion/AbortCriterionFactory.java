/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion;

import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * The base factory for all {@link AbstractAbortCriterion} implementations.
 * 
 * @author Ragnar Nevries
 */
public abstract class AbortCriterionFactory extends
    Factory<AbstractAbortCriterion> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5963848815620169716L;

  /**
   * Gets the abort criterion.
   * 
   * @return abort criterion
   */
  public abstract AbstractAbortCriterion getAbortCriterion();

  @Override
  public AbstractAbortCriterion create(ParameterBlock parameters, Context context) {
    return getAbortCriterion();
  }
}
