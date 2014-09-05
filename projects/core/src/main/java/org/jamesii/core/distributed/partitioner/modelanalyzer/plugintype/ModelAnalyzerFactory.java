/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partitioner.modelanalyzer.plugintype;

import org.jamesii.core.distributed.partitioner.PartitionMapping;
import org.jamesii.core.distributed.partitioner.modelanalyzer.AbstractModelAnalyzer;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * 
 * Super class for all factories that create model analyser algorithms.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ModelAnalyzerFactory extends
    Factory<AbstractModelAnalyzer> implements IParameterFilterFactory {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 5882493650579952130L;

  /**
   * Default constructor.
   */
  public ModelAnalyzerFactory() {
    super();
  }

  /**
   * Create executable partition (depending on model type).
   * 
   * @param model
   *          the model
   * @param modelGraph
   *          the graph of the model's structure
   * @param hardwareGraph
   *          the infrastructure graph
   * @param partitionMapping
   *          the partition mapping, defined on the graphs
   * @return executable partition
   */
  public abstract AbstractExecutablePartition getExecutablePartitionForModel(
      IModel model, ISimpleGraph modelGraph, ISimpleGraph hardwareGraph,
      PartitionMapping partitionMapping);

  /**
   * Returns the class of which the edge labels will be instances.
   * 
   * @return class of which the edge labels will be instances
   */
  public abstract Class<?> getGeneratedEdgeLabel();

  /**
   * Returns the class of which the vertex labels will be instances.
   * 
   * @return class of which the vertex labels will be instances
   */
  public abstract Class<?> getGeneratedVertexLabel();

  /**
   * Creates an adequate model analysis algorithm due to model type.
   * 
   * @param model
   *          the model to be analysed
   * @return instance of model analysis algorithm
   */
  public abstract AbstractModelAnalyzer createFromModel(IModel model);

  @Override
  public AbstractModelAnalyzer create(ParameterBlock parameters, Context context) {
    return createFromModel((IModel) parameters.getSubBlockValue("MODEL"));
  }

}
