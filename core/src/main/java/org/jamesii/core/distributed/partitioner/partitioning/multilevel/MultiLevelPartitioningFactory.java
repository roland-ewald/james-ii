/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.partitioning.multilevel;

import org.jamesii.core.distributed.partitioner.partitioning.AbstractPartitioningAlgorithm;
import org.jamesii.core.distributed.partitioner.partitioning.plugintype.PartitioningFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * Factory for the generic multi-level partitioning algorithm. Has to be the
 * super class of all factories that provide multi-level partitioning
 * algorithms.
 * 
 * @author Roland Ewald
 * 
 */
public class MultiLevelPartitioningFactory extends PartitioningFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 7322676074762390614L;

  @Override
  public AbstractPartitioningAlgorithm create(IModel model,
      ISimpleGraph modelGraph, ISimpleGraph hardwareGraph) {
    return new MultiLevelPartitioningAlgorithm(model);
  }

  @Override
  public boolean supportsHardwareGraphLabels(Class<?> edgeLabel,
      Class<?> vertexLabel) {
    return true;
  }

  @Override
  public int supportsParameters(ParameterBlock parameter) {
    return 1;
  }

  @Override
  public boolean supportsModelGraphLabels(Class<?> edgeLabel,
      Class<?> vertexLabel) {
    return true;
  }

}
