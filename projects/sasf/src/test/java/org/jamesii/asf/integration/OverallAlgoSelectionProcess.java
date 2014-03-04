/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.StochasticChattyTestCase;
import org.jamesii.asf.registry.AlgoSelectionRegistry;
import org.jamesii.asf.spdm.generators.plugintype.PerformancePredictorGeneratorFactory;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.metadata.FailureTolerance;
import org.jamesii.core.util.logging.ApplicationLogger;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.simspex.gui.SimSpExPerspective;
import org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy;
import org.jamesii.simspex.spdm.evaluation.bootstrapping.BootStrapping;
import org.jamesii.simspex.spdm.evaluation.perfmeasures.IPredictorPerformanceMeasure;
import org.jamesii.simspex.util.DBConfiguration;

/**
 * Generalised process structure for (inductive, data-driven) algorithm
 * selection as currently implemented by SASF / SPDM / ASR.
 * 
 * Can be easily extended (Strategy pattern) and parameterised in many different
 * ways. All essential parameters have to be passed to the constructor.
 * 
 * @author Roland Ewald
 */
public abstract class OverallAlgoSelectionProcess extends
    StochasticChattyTestCase {

  /** The algorithm-selection registry. */
  final protected AlgoSelectionRegistry asr = new AlgoSelectionRegistry();

  /** The database connection. */
  final protected DBConnectionData dbConn;

  /** The selector generator factories. */
  final protected PerformancePredictorGeneratorFactory[] selectorGeneratorFactories;

  /** The number of times selector evaluation shall be replicated. */
  final protected int selectorEvaluationReplications;

  /** The evaluation strategy to be used. */
  final IPredictorGeneratorEvaluationStrategy evaluationStrategy;

  /** The selector performance measure to be used for comparison. */
  final Class<? extends IPredictorPerformanceMeasure> selectorPerformanceMeasure;

  /** The number of available runtime configurations. Will be set at runtime. */
  Integer numberOfRTConfigs;

  /**
   * The flag that determines whether exploration shall be omitted or not. If
   * this is true no performance experiments will be executed, the data will
   * just be imported from the database (i.e. it is assumed to already be
   * there).
   */
  protected boolean switchOffExploration = false;

  /** Task to explore algorithm performance. */
  final PerformanceExplorationTask exploration;

  /** Task to generate selectors. */
  final SelectorGenerationTask selectorGeneration;

  /** Task to deploy (and evaluate) selectors. */
  final SelectorDeploymentTask selectorDeployment;

  /**
   * Instantiates a new overall algorithm selection process.
   * 
   * @param dbName
   *          the name of the database
   * @param selGenFactories
   *          the selector generator factories
   * @param selGenFactoryParamMap
   *          additional parameters for selector generation factories
   * @param selPerfMeasure
   *          the performance measure by which the selectors shall be evaluated
   * @param selEvalReplications
   *          the number of times the selector evaluation should be repeated on
   *          (re-)sampled data
   * @param explorationPhase
   *          the task to carry out the exploration
   */
  public OverallAlgoSelectionProcess(
      String dbName,
      PerformancePredictorGeneratorFactory[] selGenFactories,
      Map<PerformancePredictorGeneratorFactory, ParameterBlock> selGenFactoryParamMap,
      Class<? extends IPredictorPerformanceMeasure> selPerfMeasure,
      int selEvalReplications, PerformanceExplorationTask explorationPhase) {
    SimSystem.setRegistry(asr);
    dbConn = DBConfiguration.getConnectionData(dbName);
    selectorGeneratorFactories = selGenFactories;
    selectorEvaluationReplications = selEvalReplications;
    selectorPerformanceMeasure = selPerfMeasure;
    evaluationStrategy = createEvaluationStrategy();
    exploration = explorationPhase;
    selectorGeneration =
        new SelectorGenerationTask(explorationPhase.model, selEvalReplications,
            selPerfMeasure, selGenFactories, selGenFactoryParamMap,
            evaluationStrategy);
    selectorDeployment = new SelectorDeploymentTask();
  }

  @Override
  public void setUp() {
    SimSpExPerspective.setDbConnectionData(dbConn);
    SimSpExPerspective.resetPerformanceDatabase();
    IPerformanceDatabase perfDatabase =
        SimSpExPerspective.getPerformanceDataBase();
    perfDatabase.clear();
    ApplicationLogger.enableConsoleLog();
    numberOfRTConfigs = null;
  }

  /**
   * Creates the evaluation strategy to be used.
   * 
   * @return the selector generator evaluation strategy
   */
  protected IPredictorGeneratorEvaluationStrategy createEvaluationStrategy() {
    return new BootStrapping(selectorEvaluationReplications);
  }

  /**
   * Executes the overall algorithm selection process.
   * 
   * @param perfDB
   *          the performance database
   * @param seed
   *          the seed (null if random seed shall be used)
   * @param skipExploration
   *          if true, exploration is skipped
   * 
   * @throws Exception
   *           the exception
   */
  public void execute(IPerformanceDatabase perfDB, Long seed,
      boolean skipExploration) throws Exception {
    asr.setAndStoreFailureTolerance(FailureTolerance.ACCEPT_UNTESTED);
    exploration.executePerformanceExploration(perfDB, seed, skipExploration);
    selectorGeneration.extractFeatures(perfDB);
    selectorGeneration.executeSelectorGenerationAndEvaluation(perfDB,
        exploration);
    selectorDeployment.evaluateASEffectiveness(perfDB, exploration,
        selectorGeneration);
  }

}
