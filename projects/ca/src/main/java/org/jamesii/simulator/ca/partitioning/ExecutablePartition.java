/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.partitioning;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partitions;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.model.IModel;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 *
 * ExecutablePartition - 
 *
 * @author Roland Ewald
 *
 */

/**
 * org.jamesii.core.simulation.partitionizer.ExecutablePartition
 * 
 * Created on Dec 1, 2004
 * 
 * @author Roland Ewald
 */
public class ExecutablePartition extends AbstractExecutablePartition {

  /**
   * ID for serialization
   */
  static final long serialVersionUID = 1428214783905992475L;

  /**
   * Advanced constructor (to use with a partition generated by the
   * partitionizer)
   * 
   * @param model
   *          model
   * @param modelGraph
   *          the graph of the model
   * @param hardwareGraph
   *          the graph of the infrastructure
   * @param partitionMapping
   *          the partition generated by the partitioning framework
   */
  public ExecutablePartition(IModel model, ISimpleGraph modelGraph,
      ISimpleGraph hardwareGraph,
      org.jamesii.core.distributed.partitioner.PartitionMapping partitionMapping) {

    // Initialize partition properly
    super();

    SimSystem
        .report(
            Level.WARNING,
            "this is not implemented so far thus the following parameters are not used at all: "
                + modelGraph + " - " + hardwareGraph + " - " + partitionMapping);
    init(model, null, new Partitions());
  }

  /**
   * Standard constructor
   * 
   * @param model
   * @param host
   * @param subPartitions
   */
  public ExecutablePartition(IModel model, ISimulationServer host,
      Partitions subPartitions) {
    super(model, host, subPartitions);
  }

}
