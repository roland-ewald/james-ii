/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.features.plugintype;

import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class for all factories providing {@link ModelFeatureExtractor}
 * instances.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class ModelFeatureExtractorFactory<M extends IModel> extends
    FeatureExtractorFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 4763737175234651267L;

  /** The model type. */
  private final Class<M> modelType;

  /**
   * Instantiates a new model feature extractor factory.
   * 
   * @param modelType
   *          the model type
   */
  protected ModelFeatureExtractorFactory(Class<M> modelType) {
    this.modelType = modelType;
  }

  @Override
  public abstract ModelFeatureExtractor<M> create(ParameterBlock params);

  @Override
  public int supportsParameters(ParameterBlock parameters) {
    if (ModelFeatureExtractor.getModel(parameters) == null) {
      return 0;
    }
    return modelType.isAssignableFrom(ModelFeatureExtractor
        .getModel(parameters).getClass()) ? 1 : 0;
  }

  @Override
  public boolean supportsParametersRuntime(ParameterBlock parameters) {
    return supportsParameters(parameters) > 0;
  }
}
