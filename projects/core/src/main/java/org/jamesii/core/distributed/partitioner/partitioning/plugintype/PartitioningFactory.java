/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.plugintype;

import org.jamesii.core.distributed.partitioner.partitioning.AbstractPartitioningAlgorithm;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * Super class for all factories that generate partitioning algorithms.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PartitioningFactory extends
    Factory<AbstractPartitioningAlgorithm> implements IParameterFilterFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 9029238144239093125L;

  /**
   * Creates a partitioning algorithm.
   * 
   * @param model
   *          the model
   * @param modelGraph
   *          the graph describing the model structure
   * @param hardwareGraph
   *          the graph describing the infrastructure
   * @return partitioning algorithm
   */
  public abstract AbstractPartitioningAlgorithm create(IModel model,
      ISimpleGraph modelGraph, ISimpleGraph hardwareGraph);

  @Override
  public AbstractPartitioningAlgorithm create(ParameterBlock pb) {
    return create((IModel) pb.getSubBlockValue("MODEL"),
        (ISimpleGraph) pb.getSubBlockValue("MODEL_GRAPH"),
        (ISimpleGraph) pb.getSubBlockValue("HARDWARE_GRAPH"));
  }

  /**
   * Returns true if given label classes are supported for the hardware graph.
   * 
   * @param edgeLabel
   *          class of edge labels
   * @param vertexLabel
   *          class of vertex labels
   * @return true if given label classes are supported for the hardware graph
   */
  public abstract boolean supportsHardwareGraphLabels(Class<?> edgeLabel,
      Class<?> vertexLabel);

  /**
   * Returns true if given label classes are supported for the model graph.
   * 
   * @param edgeLabel
   *          class of edge labels
   * @param vertexLabel
   *          class of vertex labels
   * @return true if given label classes are supported for the model graph
   */
  public abstract boolean supportsModelGraphLabels(Class<?> edgeLabel,
      Class<?> vertexLabel);
}
