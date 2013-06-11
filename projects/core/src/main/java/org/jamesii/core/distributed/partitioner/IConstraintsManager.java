/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner;

import org.jamesii.core.distributed.simulationserver.ISimulationHost;
import org.jamesii.core.model.IModel;

/**
 * org.jamesii.core.simulation.partitionizer.ConstraintsManager
 * 
 * Used for adding constraints to the partitioning algorithm. If a partitioning
 * algorithm supports that feature, it has to implement this interface.
 * 
 * Created on Dec 13, 2004
 * 
 * @author Roland Ewald
 * 
 */
public interface IConstraintsManager {

  /**
   * Declares that all nodes in this array have to be assigned to the same
   * partition
   * 
   * @param models
   * @throws ConstraintConflictException
   */
  void assignNodesToSamePartition(IModel[] models)
      throws ConstraintConflictException;

  /**
   * Declares that all nodes in this array and also their subnodes have to be
   * assignes to the same partition ATTENTION: Not defined if the model
   * structure is not a tree.
   * 
   * @param models
   * @throws ConstraintConflictException
   */
  void assignNodesToSamePartitionRecursively(IModel[] models)
      throws ConstraintConflictException;

  /**
   * Assigns a node to a specified host
   * 
   * @param model
   * @param host
   * @throws ConstraintConflictException
   */
  void assignNodeToPartition(IModel model, ISimulationHost host)
      throws ConstraintConflictException;

  /**
   * Assigns a node and all of its subnodes to a specified partition. ATTENTION:
   * Not defined if the model structure is not a tree.
   * 
   * @param model
   * @param host
   * @throws ConstraintConflictException
   */
  void assignNodeToPartitionRecursively(IModel model, ISimulationHost host)
      throws ConstraintConflictException;

}
