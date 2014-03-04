/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.ensemble;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.dataimport.PerfTupleMetaData;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.generators.AbstractPredictorGenerator;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.asf.spdm.generators.preprocess.IDMDataPreProcessor;
import org.jamesii.asf.spdm.util.PerformanceTuples;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Creates a predictor that uses another {@link IPerformancePredictorGenerator}
 * to create an ensemble of {@link IPerformancePredictor} instances, one per
 * configuration.
 * 
 * 
 * @author Roland Ewald
 * 
 */
public class EnsemblePredictorGenerator extends AbstractPredictorGenerator {

  /** The parameters of the predictor generator to be used. */
  private ParameterBlock predGenParameters;

  /** The factory to be used for predictor generation. */
  private PerformancePredictorGeneratorFactory predGenFactory;

  /**
   * Default constructor.
   * 
   * @param pgFac
   *          factory for predictor generation
   * @param pgParams
   *          parameters for predictor generation
   */
  public EnsemblePredictorGenerator(PerformancePredictorGeneratorFactory pgFac,
      ParameterBlock pgParams) {
    predGenParameters = pgParams;
    predGenFactory = pgFac;
  }

  @Override
  public <T extends PerformanceTuple> IPerformancePredictor generatePredictor(
      List<T> data, PerfTupleMetaData metaData) {

    Map<Configuration, List<T>> configMap =
        PerformanceTuples.sortToConfigMap(data);

    // Create new metaData that leaves out all attributes belonging to the
    // configuration
    PerfTupleMetaData featureMData = new PerfTupleMetaData();
    featureMData.setNominalAttribs(new HashMap<>(metaData
        .getNominalAttribs()));
    featureMData.setNumericAttribs(new HashSet<>(metaData
        .getNumericAttribs()));
    featureMData.setMaximizePerformance(metaData.isMaximizePerformance());
    Set<String> configAttributes = PerformanceTuples.getConfigAttributes(data);
    for (String configAttrib : configAttributes) {
      featureMData.getNominalAttribs().remove(configAttrib);
      featureMData.getNumericAttribs().remove(configAttrib);
    }

    // TODO(re027): Think of narrowing down the population of configurations to
    // those belonging to the efficient border (->portfolio design), take into
    // account the result size

    // Create predictors for each configuration
    Map<Configuration, IPerformancePredictor> predictors =
        new HashMap<>();
    for (Entry<Configuration, List<T>> configTuples : configMap.entrySet()) {
      List<T> inputData = configTuples.getValue();

      // Pre-process data
      @SuppressWarnings("rawtypes")
      IDMDataPreProcessor preProcessor =
          predGenFactory.createPreprocessor(predGenParameters);
      if (preProcessor != null) {
        inputData = preProcessor.preprocess(inputData);
      }
      if (inputData.isEmpty()) {
        continue;
      }

      // Create predictor generator, predictor
      IPerformancePredictorGenerator predictorGenerator =
          predGenFactory.createPredictorGenerator(predGenParameters,
              inputData.get(0));
      IPerformancePredictor predictor =
          predictorGenerator.generatePredictor(inputData, featureMData);
      predictors.put(configTuples.getKey(), predictor);
    }

    return new EnsemblePerfPredictor(predictors);
  }
}
