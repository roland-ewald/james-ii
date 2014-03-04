/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.plugintype;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.preprocess.IDMDataPreProcessor;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Super class for all factories that create performance predictor generators.
 * 
 * @see IPerformancePredictorGenerator
 * 
 * @author Roland Ewald
 * 
 */
public abstract class PerformancePredictorGeneratorFactory extends
    Factory<IPerformancePredictorGenerator> {

  /** Serialization ID. */
  private static final long serialVersionUID = -5777328185968666439L;

  /**
   * Name of parameter determining the number of classes to be distinguished,
   * type {@link Integer}. This is only necessary for classification-based
   * predictors, not for regression-based ones. Default value:
   * {@link PerformancePredictorGeneratorFactory#DEFAULT_VAL_NUM_CLASSES}.
   */
  public static final String NUM_OF_CLASSES = "numOfClasses";

  /**
   * The default parameter value for the number of performance classes to be
   * distinguished.
   */
  public static final int DEFAULT_VAL_NUM_CLASSES = 20;

  /**
   * Name of the parameter that sets the borders of the performance buckets for
   * discretization manually. The expected type of parameter values is
   * <code>java.lang.Double[]</code>. WARNING: if this is set, the parameter
   * {@link #NUM_OF_CLASSES} will be ignored and the number of classes is set to
   * <code>perfBorders.length + 1</code>.
   */
  public static final String PERF_BORDERS = "perfBorders";

  /**
   * Creates a predictor generator.
   * 
   * @param params
   *          creation parameters
   * @param example
   *          an example tuple (while assuming that the tuples all have the same
   *          form, this is ensured by the pre-processing steps)
   * @return predictor generator, or null if not possible
   */
  public abstract IPerformancePredictorGenerator createPredictorGenerator(
      ParameterBlock params, PerformanceTuple example);

  /**
   * Creates a preprocessor for the predictor generator.
   * 
   * @param params
   *          creation parameters
   * @return a preprocessor that generates performance tuples suitable for the
   *         corresponding predictor generator, null if no pre-processing is
   *         necessary
   */
  public IDMDataPreProcessor<PerformanceTuple> createPreprocessor(
      ParameterBlock params) {
    return null;
  }

  @Override
  public IPerformancePredictorGenerator create(ParameterBlock parameters) {
    return createPredictorGenerator(parameters, null);
  }

}
