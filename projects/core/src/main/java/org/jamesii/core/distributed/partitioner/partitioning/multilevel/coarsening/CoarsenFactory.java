/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.coarsening;

import java.util.List;
import java.util.Map;

import org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion.AbstractAbortCriterion;
import org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining.AbstractRefineAlgorithm;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * The base factory for all {@link AbstractCoarsenAlgorithm} implementations.
 * 
 * @author Roland Ewald
 */
public abstract class CoarsenFactory extends Factory<AbstractCoarsenAlgorithm> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4522034886109638127L;

  /**
   * Gets the coarsen algorithm.
   * 
   * @param input
   *          the input
   * @param aborter
   *          the aborter
   * 
   * @return the coarsen algorithm
   */
  public abstract AbstractCoarsenAlgorithm getCoarsenAlgorithm(
      ISimpleGraph input, AbstractAbortCriterion aborter);

  @Override
  public AbstractCoarsenAlgorithm create(ParameterBlock parameters) {
    return getCoarsenAlgorithm(
        (ISimpleGraph) getParameter("INPUT", parameters),
        (AbstractAbortCriterion) getParameter("ABORTER", parameters));
  }

}
