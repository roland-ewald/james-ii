/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.features.plugintype;


import java.io.Serializable;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Features;
import org.jamesii.core.data.IURIHandling;
import org.jamesii.core.data.model.read.plugintype.AbstractModelReaderFactory;
import org.jamesii.core.data.model.read.plugintype.ModelReaderFactory;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IProblemDefinition;


/**
 * Common super class for all feature extraction mechanisms that work on models.
 * In this case, the provided {@link ParameterBlock} contains all information
 * available for algorithm selection when called on a real-world problem, *OR*
 * an instance of {@link IProblemDefinition} when called for benchmarking. This
 * class hides both variants from its sub-class by delegating the invocation to
 * {@link ModelFeatureExtractor#extractModelFeatures(IModel)}.
 * 
 * @author Roland Ewald
 */
public abstract class ModelFeatureExtractor<M extends IModel> implements
    IFeatureExtractor<ParameterBlock> {

  @Override
  @SuppressWarnings("unchecked")
  // Should be ensured by factory
  public Features extractFeatures(ParameterBlock params) {
    IModel model = getModel(params);
    return extractModelFeatures((M) model);
  }

  /**
   * Extract model features.
   * 
   * @param model
   *          the model
   * 
   * @return the map containing the model features
   */
  protected abstract Features extractModelFeatures(M model);

  @SuppressWarnings("unchecked")
  public Features extractModelFeaturesRuntime(IModel model) {
    return extractModelFeatures((M) model);
  }

  /**
   * Gets the model.
   * 
   * @param params
   *          the params
   * 
   * @return the model
   */
  protected static IModel getModel(ParameterBlock params) {
    IModel model;
    Object representation =
        params.getSubBlockValue(FeatureExtractorFactory.PROBLEM_REPRESENTATION);
    if (representation instanceof IApplication) {
      model =
          instantiateModel(((IApplication) representation).getProblemInstance()
              .getProblemDefinition());
    } else {
      model = getModelFromRuntimeParameterBlock(params);
    }
    return model;
  }

  /**
   * Gets the model runtime from a parameter block given at runtime.
   * 
   * @param parameters
   *          the parameters handed over to the simulator
   * 
   * @return the model, extracted from the partition
   */
  public static IModel getModelRuntime(ParameterBlock parameters) {
    Partition partition =
        parameters.getSubBlockValue(AbstractProcessorFactory.PARTITION);
    return partition.getModel();
  }

  /**
   * Instantiate model.
   * 
   * @param simProblem
   *          the simulation problem
   * 
   * @return the instantiated model
   */
  protected static IModel instantiateModel(IProblemDefinition simProblem) {
    ParameterBlock amrwfp =
        new ParameterBlock(simProblem.getProblemScheme().getUri(),
            IURIHandling.URI);
    ModelReaderFactory mrwf =
        SimSystem.getRegistry().getFactory(AbstractModelReaderFactory.class,
            amrwfp);
    Map<String, Serializable> parameters = new HashMap<>();
    parameters.putAll(simProblem.getSchemeParameters());
    return mrwf.create(null).read(
        (URI) amrwfp.getSubBlockValue(IURIHandling.URI), parameters);
  }

  /**
   * Gets the model from runtime parameter block.
   * 
   * @param params
   *          the parameters
   * 
   * @return the model from runtime parameter block
   */
  protected static IModel getModelFromRuntimeParameterBlock(
      ParameterBlock params) {
    return params.hasSubBlock(AbstractProcessorFactory.PARTITION) ? ((Partition) params
        .getSubBlockValue(AbstractProcessorFactory.PARTITION)).getModel()
        : null;
  }
}
