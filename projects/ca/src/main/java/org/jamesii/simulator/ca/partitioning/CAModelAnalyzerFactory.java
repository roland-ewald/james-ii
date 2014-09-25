/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.partitioning;

import org.jamesii.core.distributed.partitioner.PartitionMapping;
import org.jamesii.core.distributed.partitioner.modelanalyzer.AbstractModelAnalyzer;
import org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype.AbstractModelAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype.ModelAnalyzerFactory;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.annotations.Plugin;
import org.jamesii.core.util.graph.ISimpleGraph;
import org.jamesii.model.ca.grid.ICAGrid;

/**
 * 
 * CAModelAnalyzerFactory -
 * 
 * Factory to create model analyser objects for cellular automata
 * 
 * @author Roland Ewald
 * 
 */
@Plugin
public class CAModelAnalyzerFactory extends ModelAnalyzerFactory {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4381122173867593237L;

  @Override
  public AbstractExecutablePartition getExecutablePartitionForModel(
      IModel model, ISimpleGraph modelGraph, ISimpleGraph hardwareGraph,
      PartitionMapping partitionMapping) {
    return new ExecutablePartition(model, modelGraph, hardwareGraph,
        partitionMapping);
  }

  @Override
  public Class<?> getGeneratedEdgeLabel() {
    return Double.class;
  }

  @Override
  public Class<?> getGeneratedVertexLabel() {
    return Double.class;
  }

  @Override
  public AbstractModelAnalyzer createFromModel(IModel model) {
    return new SimpleCAAnalyzer();
  }

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (parameters.getSubBlockValue(AbstractModelAnalyzerFactory.MODEL) instanceof ICAGrid) {
      return 1;
    }

    return 0;
  }

}
