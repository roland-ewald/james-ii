/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning;

import org.jamesii.core.distributed.partitioner.PartitionMapping;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * org.jamesii.core.simulation.partitionizer.AbstractPartitioningAlgorithm
 * 
 * Mother class of all partitioning algorithms. The real algorithms should
 * inherit from classes derived from this here.
 * 
 * Created on Nov 15, 2004
 * 
 * @author Roland Ewald
 * 
 */
public abstract class AbstractPartitioningAlgorithm {

  /**
   * Calculates initial partition
   * 
   * @return partition
   */
  public abstract PartitionMapping calculatePartition();

  /**
   * Initializes algorithm for initial partitionizing
   * 
   * @param hardware
   *          graph representing the hardware (processors connected over a
   *          network, costs for communication(edge labels) and calculation
   *          ability(vertex labels))
   * @param model
   *          graph representing the dependencies between the atomic parts of
   *          the model - edge labels indicate the probability of communication,
   *          vertex labels the approximate calculation costs
   * @return true, if initialization was successful, otherwise false
   */
  public abstract boolean initializePartitioning(ISimpleGraph hardware,
      ISimpleGraph model);

  /**
   * Initializes algorithm for continued partitionizing (=> load balancing)
   * 
   * @param hardware
   *          graph
   * @param model
   *          graph
   * @param oldPartition
   *          formerly used partition
   * @return true, if initialization was successful, otherwise false
   */
  public abstract boolean initializePartitioning(ISimpleGraph hardware,
      ISimpleGraph model, PartitionMapping oldPartition);
}
