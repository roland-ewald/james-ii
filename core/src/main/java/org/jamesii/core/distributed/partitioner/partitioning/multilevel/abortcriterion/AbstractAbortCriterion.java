/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel.abortcriterion;

import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * Sub-classes of this class control the behaviour of the
 * {@link org.jamesii.core.distributed.partitioner.partitioning.multilevel.MultiLevelPartitioningAlgorithm}
 * by defining when a coarsening phase should stop.
 * 
 * @author Ragnar Nevries
 */
public abstract class AbstractAbortCriterion {

  /**
   * This method is called by coarsening algorithm to set information about the
   * generated levels.
   * 
   * @param level
   *          the current level index
   * @param coarseGraph
   *          the coarse graph
   */
  public abstract void putLevelInformation(int level, ISimpleGraph coarseGraph);

  /**
   * This method is called by coarsening algo to ask if the iteration should
   * continue.
   * 
   * @return true, if should continue
   */
  public abstract boolean shouldContinue();
}
