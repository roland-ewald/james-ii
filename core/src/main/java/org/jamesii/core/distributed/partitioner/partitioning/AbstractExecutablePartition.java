/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning;

import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.distributed.partition.Partitions;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.model.IModel;

/**
 * 
 * 
 * AbstractExecutablePartition -
 * 
 * Defines the constructor for all executable partitions
 * 
 * @author Roland Ewald
 * 
 */
public class AbstractExecutablePartition extends Partition {

  /**
   * Serialisation ID
   */
  private static final long serialVersionUID = 6606639614701514273L;

  /**
   * Default constructor
   * 
   */
  public AbstractExecutablePartition() {
    super();
  }

  /**
   * Standard constructor
   * 
   * @param model
   * @param host
   * @param subPartitions
   */
  public AbstractExecutablePartition(IModel model, ISimulationServer host,
      Partitions subPartitions) {
    super(model, host, subPartitions);
  }

}
