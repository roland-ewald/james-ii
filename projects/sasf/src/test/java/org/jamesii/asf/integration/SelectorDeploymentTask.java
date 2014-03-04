/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.asf.integrationtest.bogus.application.model.BogusModelFactory;
import org.jamesii.asf.registry.AlgoSelectionRegistry;
import org.jamesii.asf.registry.selection.ProcessorSelectorManager;
import org.jamesii.asf.registry.selection.SelectorEnsemble;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.generators.ConfigurationEntry;
import org.jamesii.asf.spdm.generators.ISelector;
import org.jamesii.asf.spdm.generators.Selector;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.model.variables.BaseVariable;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.util.StopWatch;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.PredictorPerformance;

/**
 * Evaluates the effectiveness of the generated selectors under several
 * scenarios.
 * 
 * @author Roland Ewald
 * 
 */
public class SelectorDeploymentTask {

  /**
   * The difference factor. Defines how to scale randomly generated model
   * parameters, i.e. 1.0 means that sampling for testing is done from the same
   * space, values in (0.0,1.0) only use a sub-space, and values > 1.0 yield
   * model setups that have definitely not been included in the training data.
   */
  double differenceFactor = 2.0;

  /**
   * The number of sample setups to create. Will be used to create two different
   * sets, the sample set from the similar space and the sample set from a space
   * scaled by the differenceFactor.
   */
  int sampleSize = 5;

  /**
   * Stores the observed performance data.
   */
  List<RealWorldSelectorPerformanceEntry> performanceData =
      new ArrayList<>();

  /** The default simulation stop time. Fixed for all samples. */
  double defaultSimStopTime = 1.0;

  /**
   * Removes all old selector managers for the processor factory class.
   */
  public static void cleanUpOldSelectorManagers() {
    SelectorEnsemble procSelectionEnsemble =
        ((AlgoSelectionRegistry) SimSystem.getRegistry())
            .getSelectorEnsemble(AbstractProcessorFactory.class);
    if (procSelectionEnsemble != null) {
      procSelectionEnsemble.clearSelectorManagers();
    }
  }

  /**
   * Carries out deployment and evaluation.
   * 
   * @param perfDB
   *          the performance database
   * @param exploration
   *          the previous exploration task
   * @param selectorGeneration
   *          the selector generation
   * @throws Exception
   *           if creation of sample setups goes wrong
   */
  public void evaluateASEffectiveness(IPerformanceDatabase perfDB,
      PerformanceExplorationTask exploration,
      SelectorGenerationTask selectorGeneration) throws Exception {

    List<ISelector> selectors = new ArrayList<>();
    selectors.addAll(selectorGeneration.getSelectors());
    addOptimalSelector(selectors);
    List<Pair<String, List<Map<String, Object>>>> setups =
        generateBenchmarkSetups(perfDB, exploration);

    for (int i = 0; i < selectors.size(); i++) {
      ISelector selector = selectors.get(i);
      Pair<PerformancePredictorGeneratorFactory, List<PredictorPerformance>> perf =
          i == 0 ? null : selectorGeneration.selPerformances.get(i - 1);
      try {
        System.err.println("Deploying:" + selector + " / predictor:"
            + perf.getFirstValue().getClass());
        deployNewSelector(selector);
        for (Pair<String, List<Map<String, Object>>> setup : setups) {
          try {
            RealWorldSelectorPerformanceEntry perfEntry =
                testSelector(
                    selector,
                    setup,
                    exploration,
                    perf == null ? "OptimumSelector" : perf.getFirstValue()
                        .getClass().getName(),
                    perf == null ? 0.0 : selectorGeneration
                        .calculateAverageError(perf.getSecondValue()));
            performanceData.add(perfEntry);
          } catch (Throwable t) {
            t.printStackTrace();
          }

        }
      } catch (Throwable t) {
        t.printStackTrace();
      }
    }
  }

  /**
   * Deploys new selector. Removes all old ones.
   * 
   * @param selector
   *          the selector to be deployed
   * 
   * @throws Exception
   *           the exception
   */
  private void deployNewSelector(ISelector selector) throws Exception {
    cleanUpOldSelectorManagers();
    ((AlgoSelectionRegistry) SimSystem.getRegistry()).deploySelector(
        new ProcessorSelectorManager(selector, (new BogusModelFactory())
            .getSupportedInterfaces()), AbstractProcessorFactory.class);
  }

  /**
   * Tests selector on the given sample setup.
   * 
   * @param selector
   *          the selector
   * @param setup
   *          the setup
   * @param exploration
   *          the previous exploration task
   * @param selGenFactoryName
   *          the name of the selector generator factory
   * @param predictedPerformance
   *          the performance predicted by SPDM
   * 
   * @return the real world selector performance entry
   */
  private RealWorldSelectorPerformanceEntry testSelector(ISelector selector,
      Pair<String, List<Map<String, Object>>> setup,
      PerformanceExplorationTask exploration, String selGenFactoryName,
      double predictedPerformance) {

    List<Double> performances = new ArrayList<>();
    for (Map<String, Object> parameterSetup : setup.getSecondValue()) {
      System.err.println("Executing: " + Strings.dispMap(parameterSetup));
      StopWatch sw = new StopWatch();
      sw.start();
      BaseExperiment exp = new BaseExperiment();
      exp.getFixedModelParameters().putAll(parameterSetup);
      exp.setModelLocation(exploration.getExperiment().getModelLocation());
      exp.setDefaultSimStopTime(defaultSimStopTime);
      exp.execute();
      sw.stop();
      performances.add(sw.elapsedSeconds());
    }

    return new RealWorldSelectorPerformanceEntry(setup.getFirstValue(),
        performances, selGenFactoryName, predictedPerformance);
  }

  /**
   * Generates all benchmark setups.
   * 
   * @param perfDB
   *          the performance database
   * @param exploration
   *          the exploration task
   * 
   * @throws Exception
   *           the exception
   */
  protected List<Pair<String, List<Map<String, Object>>>> generateBenchmarkSetups(
      IPerformanceDatabase perfDB, PerformanceExplorationTask exploration)
      throws Exception {

    List<Pair<String, List<Map<String, Object>>>> setups =
        new ArrayList<>();

    // Real experiments on same sample set of variable size, to be
    // replicated with different sample sets as well
    // 1. Resubstitution error
    setups.add(new Pair<>("Resubstitution",
        getSameSetups(perfDB, exploration)));
    // 2. Measure selection error by checking choice on whole range
    setups.add(new Pair<>("Similar Models",
        getSimilarSetups(exploration, 1.0)));
    // 3. Measure selection error by checking choice on *extended* range
    setups.add(new Pair<>("Different Models",
        getSimilarSetups(exploration, differenceFactor)));

    return setups;
  }

  /**
   * Adds the optimal selector (at first position).
   * 
   * @see OptimalBogusSimulatorPerformancePredictor
   * @param selectors
   *          the list of all selectors
   */
  private void addOptimalSelector(List<ISelector> selectors) {
    Set<Configuration> configurations = new HashSet<>();
    for (ConfigurationEntry configEntry : ((Selector) selectors.get(0))
        .getConfigs()) {
      configurations.add(configEntry.getConfig());
    }
    selectors.add(0, new Selector(configurations,
        new OptimalBogusSimulatorPerformancePredictor(), configurations.size(),
        false));
  }

  /**
   * Gets model setups that are only similar to those used for training the
   * selectors. How similar they are can be configure by a scale factor.
   * 
   * @param exploration
   *          the exploration task
   * @param scaleFactor
   *          the scale factor; 1.0 means use the same range for every variable
   * 
   * @return similar random setups
   */
  protected List<Map<String, Object>> getSimilarSetups(
      PerformanceExplorationTask exploration, double scaleFactor) {
    List<Map<String, Object>> result = new ArrayList<>();
    Set<BaseVariable<?>> explorationVars =
        exploration.getSimSpaceExplorer().getModelVariables();
    for (int i = 0; i < sampleSize; i++) {
      Map<String, Object> newSetup = new HashMap<>();
      for (BaseVariable<?> baseVar : explorationVars) {
        baseVar.setRandomValue();
        newSetup.put(baseVar.getName(),
            (int) (((Number) baseVar.getValue()).doubleValue() * scaleFactor));
      }
      result.add(newSetup);
    }
    return result;
  }

  /**
   * Retrieves the same setups from the performance database that have been used
   * to generate/evaluate the selectors.
   * 
   * @param perfDB
   *          the performance database
   * @param exploration
   *          the exploration task
   * 
   * @return the same setups that have been used before
   * 
   * @throws Exception
   *           the exception
   */
  protected List<Map<String, Object>> getSameSetups(
      IPerformanceDatabase perfDB, PerformanceExplorationTask exploration)
      throws Exception {
    List<Map<String, Object>> result = new ArrayList<>();
    List<IProblemDefinition> problemDefinitions =
        perfDB.getAllProblemDefinitions(perfDB.getProblemScheme(exploration
            .getExperiment().getModelLocation()));
    for (IProblemDefinition problemDefinition : problemDefinitions) {
      Map<String, Object> schemeSetup = new HashMap<>();
      schemeSetup.putAll(problemDefinition.getSchemeParameters());
      result.add(schemeSetup);
    }
    return result;
  }

  public double getDifferenceFactor() {
    return differenceFactor;
  }

  public int getSampleSize() {
    return sampleSize;
  }

  public List<RealWorldSelectorPerformanceEntry> getPerformanceData() {
    return performanceData;
  }
}
