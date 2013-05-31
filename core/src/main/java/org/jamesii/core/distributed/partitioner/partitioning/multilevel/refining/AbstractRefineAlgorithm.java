/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.refining;

import java.util.List;
import java.util.Map;

import org.jamesii.core.distributed.partitioner.PartitionMapping;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * AbstractRefineAlgorithm is used for the refining phase in Multilevel
 * Partitioning Algorithms.
 * 
 * Created on September 15, 2007
 * 
 * @author Ragnar Nevries
 */
public abstract class AbstractRefineAlgorithm {

  /** The mappings. */
  private List<Map<Integer, Integer>> mappings;

  /** The coarse levels. */
  private List<ISimpleGraph> coarseLevels;

  /**
   * Instantiates a new abstract refine algorithm.
   * 
   * @param coarseLevels
   *          the coarse levels
   * @param coarseMappings
   *          the coarse mappings
   */
  public AbstractRefineAlgorithm(List<ISimpleGraph> coarseLevels,
      List<Map<Integer, Integer>> coarseMappings) {
    this.mappings = coarseMappings;
    this.coarseLevels = coarseLevels;
  }

  /**
   * Uncoarsen and refine the actual graph (last level from nextCoarseningLevel)
   * with given PartitionMapping.
   * 
   * @param partitionMap
   *          a PartitionMapping of the actual coarse graph
   * 
   * @return a PartitionMapping of the original Graph derived from given one
   */
  public PartitionMapping unCoarsenGraph(PartitionMapping partitionMap) {
    for (int actualLevel = coarseLevels.size() - 1; actualLevel > 0; actualLevel--) {
      partitionMap = refinePartition(actualLevel, partitionMap);
    }
    return partitionMap;
  }

  /**
   * Refines the given partition at the given level to match the bigger
   * (refined) graph.
   * 
   * @param level
   *          the level of the graph the partition matches to
   * @param partOfSmallOne
   *          the partition of the coarsened graph
   * 
   * @return the partition mapping
   */
  public abstract PartitionMapping refinePartition(int level,
      PartitionMapping partOfSmallOne);

  /**
   * @return the mappings
   */
  protected List<Map<Integer, Integer>> getMappings() {
    return mappings;
  }

}
