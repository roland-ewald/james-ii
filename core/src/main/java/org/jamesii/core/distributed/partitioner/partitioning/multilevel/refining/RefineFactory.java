/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining;

import java.util.List;
import java.util.Map;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * base factory for {@link AbstractRefineAlgorithm} instances.
 * 
 * @author Roland Ewald
 */
public abstract class RefineFactory extends Factory<AbstractRefineAlgorithm> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -517210918109913419L;

  /**
   * Gets the refine algorithm.
   * 
   * @param coarseLevels
   *          the coarse levels
   * @param coarseMappings
   *          the coarse mappings
   * 
   * @return the refine algorithm
   */
  public abstract AbstractRefineAlgorithm getRefineAlgorithm(
      List<ISimpleGraph> coarseLevels,
      List<Map<Integer, Integer>> coarseMappings);

  @Override
  public AbstractRefineAlgorithm create(ParameterBlock parameters) {
    return getRefineAlgorithm(
        (List<ISimpleGraph>) getParameter("COARSE_LEVELS", parameters),
        (List<Map<Integer, Integer>>) getParameter("COARSE_MAPPINGS",
            parameters));
  }

}
