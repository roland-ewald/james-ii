/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.features.plugintype;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.factories.IParameterFilterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Factory for feature extractors. Each feature extractor calculates a feature
 * of any (serializable) type, based on a given simulation problem, and returns
 * it.
 * 
 * @author Roland Ewald
 */
public abstract class FeatureExtractorFactory extends
    Factory<IFeatureExtractor<?>> implements IParameterFilterFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8362877278911499731L;

  /** The sub-block in which the problem representation can be found. */
  public static final String PROBLEM_REPRESENTATION = "problemRepresentation";

  /**
   * Create feature extractor.
   * 
   * @param params
   *          the parameters for creation
   * 
   * @return the created feature extractor
   */
  @Override
  public abstract IFeatureExtractor<?> create(ParameterBlock params);

  /**
   * Get name of the feature to be extracted.
   * 
   * @return feature name
   */
  public abstract String getFeatureName();

  /**
   * Get description of the feature to be extracted.
   * 
   * @return feature description
   */
  public abstract String getFeatureDescription();

  /**
   * Decides if the parameter block to be interpreted at runtime can be analysed
   * by the provided feature extractor. This is in contrast to
   * {@link IParameterFilterFactory#supportsParameters(ParameterBlock)}, which
   * is used here to decide this for an application from the performance
   * database (i.e. offline data analysis).
   * 
   * @see IParameterFilterFactory
   * @param parameters
   *          the parameter block to be interpreted at runtime
   * 
   * @return true, if feature extractor is applicable
   */
  public abstract boolean supportsParametersRuntime(ParameterBlock parameters);

}
