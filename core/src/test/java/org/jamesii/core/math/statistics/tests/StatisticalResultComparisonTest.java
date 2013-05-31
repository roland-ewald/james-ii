/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.statistics.tests;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.StochasticChattyTestCase;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.experiments.util.TaskRuntimeInformationListener;
import org.jamesii.core.math.random.distributions.BinomialDistribution;
import org.jamesii.core.math.statistics.tests.plugintype.AbstractPairedTestFactory;
import org.jamesii.core.math.statistics.tests.plugintype.PairedTestFactory;
import org.jamesii.core.math.statistics.tests.wilcoxon.WilcoxonRankSumTestFactory;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;

/**
 * Support for the simple statistical comparison of simulator results.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class StatisticalResultComparisonTest extends
    StochasticChattyTestCase {

  /**
   * The property name that determines whether artificial bias should be
   * introduced.
   */
  private static final String INTRODUCE_BIAS_PROPERTY =
      "StatisticalResultComparisonTest.introducebias";

  /**
   * The bias to be added to each amount of the reference implementation in
   * case. {@link MLResultComparisonTest#statisticalDetectionCheck} is true.
   */
  private static final int STAT_DETECTION_CHECK_BIAS = 1;

  /**
   * The minimal p-value to check in the test. The smaller, the less probable
   * are false positives (and the more probable are false negatives).
   */
  private double pValue = 0.001;

  /** The p-value for the overall outcome. */
  private double outcomePValue = 0.05;

  /**
   * The flag to decide whether the strict mode is turned on. In the strict
   * mode, a single failing of a test is considered harmful and will lead to the
   * termination of the test.
   */
  private boolean strictMode = false;

  /** The number of replications to be executed (per model and setup). */
  private int replications = 1000;

  /** The simulation setups to be tested. */
  private List<ParameterBlock> setups = getProcessorSetups();

  /** The factory used to create a two-sample (paired) statistical test. */
  private Class<? extends PairedTestFactory> statisticalTestFactory =
      WilcoxonRankSumTestFactory.class;

  /** The test models end their simulation stop times. */
  private Map<URI, List<Pair<Double, Map<String, Object>>>> testModelStopTimes =
      getTestModels();

  /** The map simulation (model-uri, sim-setup) => number of failures. */
  private Map<Pair<URI, ParameterBlock>, Integer> failures = new HashMap<>();

  /** The overall number of comparisons (each of which may lead to a failure). */
  private Map<Pair<URI, ParameterBlock>, Integer> comparisons = new HashMap<>();

  /** The failure descriptions. */
  private Map<Pair<URI, ParameterBlock>, List<String>> failureDescriptions =
      new HashMap<>();

  /** The number of simulator failures. */
  private int otherSimErrors = 0;

  /** The number of reference simulator failures. */
  private int refSimErrors = 0;

  /** Set true to check the ability of the statistical test to detect the bias. */
  private final boolean statisticalDetectionCheck = System.getProperty(
      INTRODUCE_BIAS_PROPERTY, "").equals("yes");

  /**
   * Gets all models to be tested, along with their simulation stop times.
   * 
   * @return a map test model => (stop time, model parameters)
   */
  protected abstract Map<URI, List<Pair<Double, Map<String, Object>>>> getTestModels();

  /**
   * Gets the processor setups to be tested. The first one should be the
   * 'reference' simulator.
   * 
   * @return the processor setups
   */
  protected abstract List<ParameterBlock> getProcessorSetups();

  /**
   * Configures instrumentation for the given experiment.
   * 
   * @param exp
   *          the experiment to be configured
   */
  protected abstract void configureInstrumentation(BaseExperiment exp);

  /**
   * Gets the set of state variable names.
   * 
   * @param observer
   *          the observer
   * @return the state variable names
   */
  protected abstract Collection<String> getStateVariableNames(
      IObserver<?> observer);

  /**
   * Extract execution summary, ie a tuple (snapshot_time, map var_name =>
   * value).
   * 
   * @param allVariableNames
   *          the set of all variable names
   * @param observer
   *          the observer
   * @return the tuple (snapshot_time, map var_name => value)
   */
  protected abstract Pair<Number, Map<String, Long>> extractExecutionSummary(
      Set<String> allVariableNames, IObserver<?> observer);

  /**
   * Tests the simulator results.
   */
  public void testSimulatorResults() {
    for (Entry<URI, List<Pair<Double, Map<String, Object>>>> modelEntry : testModelStopTimes
        .entrySet()) {
      for (Pair<Double, Map<String, Object>> modelConfig : modelEntry
          .getValue()) {
        SimSystem.report(Level.INFO, "Checking model '" + modelEntry.getKey()
            + "', stop time: " + modelConfig.getFirstValue());
        checkModel(new SimTaskDescription(modelEntry.getKey(), modelConfig));
      }
    }
    reportAndCheckResults();
  }

  /**
   * Report and check results.
   */
  private void reportAndCheckResults() {
    assertTrue("The reference implementation failed " + refSimErrors
        + " times.", refSimErrors == 0);
    assertTrue("The revised implementation failed " + otherSimErrors
        + " times.", otherSimErrors == 0);
    for (Entry<Pair<URI, ParameterBlock>, Integer> compEntry : comparisons
        .entrySet()) {
      checkResultsForSpecificSituation(
          thisOrDefault(failures.get(compEntry.getKey()), 0),
          compEntry.getValue(),
          thisOrDefault(failureDescriptions.get(compEntry.getKey()),
              new ArrayList<String>()));
    }
  }

  /**
   * Returns first parameter, or second if first one is null.
   * 
   * @param <T>
   *          the generic type
   * @param object
   *          the object
   * @param defaultValue
   *          the default value
   * @return the first parameter (second if first is null)
   */
  private <T> T thisOrDefault(T object, T defaultValue) {
    return object == null ? defaultValue : object;
  }

  /**
   * Check results for specific situation.
   * 
   * @param failures
   *          the failures
   * @param comparisons
   *          the comparisons
   * @param failureDescriptions
   *          the failure descriptions
   */
  private void checkResultsForSpecificSituation(int failures, int comparisons,
      List<String> failureDescriptions) {
    String result =
        "Number of statistical failures: " + failures + " (number of tests: "
            + comparisons
            + ", number of reference simulator errors (exceptions): "
            + refSimErrors
            + ", number of other simulator errors (exceptions): "
            + otherSimErrors + ")";

    addInformation(Strings.dispIterable(failureDescriptions));
    addInformation(result);

    SimSystem.report(Level.INFO, Strings.dispIterable(failureDescriptions));
    SimSystem.report(Level.INFO, result);

    double outcomeProb =
        1 - BinomialDistribution
            .distribution(comparisons, failures - 1, pValue);
    SimSystem.report(Level.INFO,
        "Probability of this outcome (assuming the absence of any bias):"
            + outcomeProb);
    assertTrue(
        "The number of statistical failures should be 'unsurprising' w.r.t. the number of comparisons and the desired signnificance.",
        1 - outcomeProb < outcomePValue);
  }

  /**
   * Check test model with the given index.
   * 
   * @param the
   *          task description
   */
  protected void checkModel(SimTaskDescription taskDescription) {

    List<Map<String, List<Long>>> replicationsOfAllSetups = new ArrayList<>();
    for (ParameterBlock setup : setups) {
      Map<String, List<Long>> results = null;
      try {
        results = executeSetupOnModel(taskDescription, setup);
      } catch (Exception e) {
        SimSystem.report(Level.SEVERE, "Could not simulate model '"
            + taskDescription.uri + "' until sim stop time "
            + taskDescription.stopTime + " with setup " + setup, e);
        // break the loop if the reference failed
        if (setup == setups.get(0)) {
          refSimErrors++;
          break;
        } else {
          otherSimErrors++;
          continue;
        }
      }
      replicationsOfAllSetups.add(results);
    }

    // check if at least two setups produced replication data
    if (replicationsOfAllSetups.size() > 1) {
      checkCardinalities(replicationsOfAllSetups);
      compareSimulatorOutputs(replicationsOfAllSetups, taskDescription);
    }
  }

  /**
   * Compares simulator outputs by statistical tests. It does so by comparing
   * the results of the first setup which each of the following.
   * 
   * @param replicationsOfAllSetups
   *          the replications of all setups
   * @param taskDescription
   *          the task description
   */
  private void compareSimulatorOutputs(
      List<Map<String, List<Long>>> replicationsOfAllSetups,
      SimTaskDescription taskDescription) {
    assertTrue("Comparison requires at least two setups.",
        replicationsOfAllSetups.size() > 1);
    for (int i = 1; i < replicationsOfAllSetups.size(); i++) {
      compareSimulatorOutputs(replicationsOfAllSetups.get(0),
          replicationsOfAllSetups.get(i), setups.get(i), taskDescription);
    }
  }

  /**
   * Compare two simulator outputs.
   * 
   * @param referenceResults
   *          the 'reference' results
   * @param testResults
   *          the results of a setup under test
   * @param testSetup
   *          the test setup
   * @param taskDescription
   *          the task description
   */
  private void compareSimulatorOutputs(
      Map<String, List<Long>> referenceResults,
      Map<String, List<Long>> testResults, ParameterBlock testSetup,
      SimTaskDescription taskDescription) {

    IPairedTest statisticalTest =
        SimSystem
            .getRegistry()
            .getFactory(AbstractPairedTestFactory.class,
                new ParameterBlock(statisticalTestFactory.getName()))
            .create(null);

    for (String varName : referenceResults.keySet()) {

      List<? extends Number> referenceResult =
          addZerosIfNullOrShort(referenceResults.get(varName));
      List<? extends Number> testResult =
          addZerosIfNullOrShort(testResults.get(varName));

      double comparisonPValue =
          statisticalTest.executeTest(referenceResult, testResult);
      incCounter(comparisons, taskDescription.uri, testSetup);

      boolean testFailed = comparisonPValue < pValue;

      if (testFailed) {
        String failureDescription =
            createFailureDescription(testSetup, taskDescription, varName,
                referenceResult, testResult, comparisonPValue);

        if (strictMode) {
          addInformation(failureDescription);
          assertTrue(
              "The null hypothesis that both samples come from the same distribution should not be rejected (careful: this test may fail occasionally, particularly when many comparisons are conducted, current number:"
                  + comparisons + ").", !testFailed);
        } else {
          incCounter(failures, taskDescription.uri, testSetup);
          addFailureDescription(taskDescription.uri, testSetup,
              failureDescription);
        }
      }
    }
  }

  private void addFailureDescription(URI uri, ParameterBlock simSetup,
      String failureDescription) {
    Pair<URI, ParameterBlock> setup = new Pair<>(uri, simSetup);
    List<String> failureList = failureDescriptions.get(setup);
    if (failureList == null) {
      failureList = new ArrayList<>();
      failureDescriptions.put(setup, failureList);
    }
    failureList.add(failureDescription);
  }

  private static void incCounter(
      Map<Pair<URI, ParameterBlock>, Integer> countMap, URI uri,
      ParameterBlock simSetup) {
    Pair<URI, ParameterBlock> setup = new Pair<>(uri, simSetup);
    countMap.put(setup, getCounter(countMap, setup) + 1);
  }

  private static int getCounter(
      Map<Pair<URI, ParameterBlock>, Integer> countMap,
      Pair<URI, ParameterBlock> key) {
    Integer result = countMap.get(key);
    return result == null ? 0 : result;
  }

  /**
   * Creates the failure description.
   * 
   * @param testSetup
   *          the test setup
   * @param taskDescription
   *          the task description
   * @param varName
   *          the variable name
   * @param referenceResult
   *          the reference result
   * @param testResult
   *          the test result
   * @param comparisonPValue
   *          the p-value of the comparison
   * @return the failure description
   */
  private String createFailureDescription(ParameterBlock testSetup,
      SimTaskDescription taskDescription, String varName,
      List<? extends Number> referenceResult,
      List<? extends Number> testResult, double comparisonPValue) {
    StringBuffer failureDescription =
        new StringBuffer(
            "\n\n==FAILURE==\n\nThis setup fails comparison with reference implementation:"
                + ParameterBlocks.printParamTree(testSetup));
    failureDescription.append("\nReference results: "
        + Strings.dispIterable(referenceResult));
    failureDescription.append("\nResults of setup "
        + ParameterBlocks.printParamTree(testSetup) + ": "
        + Strings.dispIterable(testResult));
    failureDescription.append("\nThe results of the setup yield a p-value of:"
        + comparisonPValue);
    failureDescription.append("\nComparison details: variable '" + varName
        + "' in model '" + taskDescription.uri + "', executed until time "
        + taskDescription.stopTime + " with parameters: "
        + Strings.dispMap(taskDescription.modelParameters));
    return failureDescription.toString();
  }

  /**
   * Execute setup on model.
   * 
   * @param taskDescription
   *          the task description
   * @param setup
   *          the setup to be used for execution
   * @return the results for execution a setup of a model; a map variable_name
   *         => (list of amounts, one per replication)
   * @throws Exception
   *           if anything goes wrong
   */
  Map<String, List<Long>> executeSetupOnModel(
      SimTaskDescription taskDescription, ParameterBlock setup)
      throws Exception {
    Pair<BaseExperiment, TaskRuntimeInformationListener> expAndData =
        configureExperiment(taskDescription, setup);
    expAndData.getFirstValue().execute();
    List<IObserver<?>> modelObservers =
        expAndData.getSecondValue().getAllModelObservers();

    assertEquals("The used instrumenter attaches one observer to each model.",
        replications, modelObservers.size());

    List<Pair<Number, Map<String, Long>>> executionSummaries =
        summarizeExecutions(modelObservers);

    if (statisticalDetectionCheck && setup == setups.get(0)) {
      introduceBias(executionSummaries);
    }

    return mergeReplicationData(executionSummaries);
  }

  /**
   * Extract an execution summary from each observer. Such a summary is a tuple
   * (snapshot_time, state_vector).
   * 
   * @param modelObservers
   *          the model observers
   * @return the list of execution summaries, i.e. tuples (sample time, map: var
   *         name => value)
   */
  private List<Pair<Number, Map<String, Long>>> summarizeExecutions(
      List<IObserver<?>> modelObservers) {

    List<Pair<Number, Map<String, Long>>> executionSummaries =
        new ArrayList<>();

    Set<String> allVariableNames = getAllVariableNames(modelObservers);
    for (IObserver<?> obs : modelObservers) {
      executionSummaries.add(extractExecutionSummary(allVariableNames, obs));
    }

    return executionSummaries;
  }

  /**
   * Gets all observed variable names.
   * 
   * @param modelObservers
   *          the model observers
   * @return all variable names
   */
  private Set<String> getAllVariableNames(List<IObserver<?>> modelObservers) {
    Set<String> allVariableNames = new HashSet<>();
    for (IObserver<?> obs : modelObservers) {
      allVariableNames.addAll(getStateVariableNames(obs));
    }
    return allVariableNames;
  }

  /**
   * Check for null and size of the sample. A null sample (i.e., no amount of
   * that species was recorded in *any* replication) or a sample that is too
   * short (i.e., no amount of that species was recorded in *some* replications)
   * is complemented by additional zeroes.
   * 
   * @param sample
   *          the list of sample values
   * @return the checked (and potentially zero-filled) list
   */
  private List<? extends Number> addZerosIfNullOrShort(List<Long> sample) {
    if (sample == null) {
      return Collections.nCopies(replications, 0L);
    } else if (sample.size() < replications) {
      sample.addAll(Collections.nCopies(replications - sample.size(), 0L));
    }
    return sample;
  }

  /**
   * Creates list with a certain amount of zeroes.
   * 
   * @param listSize
   *          the list size
   * @return the list<? extends number>
   */
  public List<Long> createListWithZeroes(int listSize) {
    List<Long> listWithZeroes = new ArrayList<>();
    for (int i = 0; i < listSize; i++) {
      listWithZeroes.add(0L);
    }
    return listWithZeroes;
  }

  /**
   * Checks cardinalities.
   * 
   * @param replicationsOfAllSetups
   *          the replication data for all setups
   */
  private void checkCardinalities(
      List<Map<String, List<Long>>> replicationsOfAllSetups) {
    for (Map<String, List<Long>> repData : replicationsOfAllSetups) {
      for (List<Long> speciesAmountList : repData.values()) {
        assertEquals(
            "There should always be one species amount per replication.",
            replications, speciesAmountList.size());
      }
    }
  }

  /**
   * Introduces some bias to the given set of execution summaries, by adding a
   * fixed amount to each species count and each replication.
   * 
   * @param executionSummaries
   *          the execution summaries of a setup
   */
  protected void introduceBias(
      List<Pair<Number, Map<String, Long>>> executionSummaries) {
    for (Pair<Number, Map<String, Long>> p : executionSummaries) {
      for (Entry<String, Long> entr : p.getSecondValue().entrySet()) {
        entr.setValue(entr.getValue() + STAT_DETECTION_CHECK_BIAS);
      }
    }
  }

  /**
   * Merge replication data.
   * 
   * @param executionSummaries
   *          the execution summaries
   * @return the map containing the merged data: var_name => list of amounts
   *         (per replication)
   */
  protected Map<String, List<Long>> mergeReplicationData(
      List<Pair<Number, Map<String, Long>>> executionSummaries) {

    Map<String, List<Long>> mergedRepicationData = new HashMap<>();

    for (Pair<Number, Map<String, Long>> executionSummary : executionSummaries) {
      for (Entry<String, Long> stateVectorElement : executionSummary
          .getSecondValue().entrySet()) {
        if (!mergedRepicationData.containsKey(stateVectorElement.getKey())) {
          mergedRepicationData.put(stateVectorElement.getKey(),
              new ArrayList<Long>());
        }
        mergedRepicationData.get(stateVectorElement.getKey()).add(
            stateVectorElement.getValue());
      }
    }

    return mergedRepicationData;
  }

  /**
   * Configure a simple experiment.
   * 
   * @param taskDescription
   *          the task description
   * @param processorSetup
   *          the processor setup
   * @return the tuple (base experiment, task runtime info listener)
   */
  protected Pair<BaseExperiment, TaskRuntimeInformationListener> configureExperiment(
      SimTaskDescription taskDescription, ParameterBlock processorSetup) {

    // Set basics
    BaseExperiment exp = new BaseExperiment();
    exp.setModelLocation(taskDescription.uri);
    exp.setDefaultSimStopTime(taskDescription.stopTime);
    exp.setFixedModelParameters(taskDescription.modelParameters);
    exp.setRepeatRuns(replications);
    exp.setProcessorFactoryParameters(processorSetup);

    // Configure instrumenters
    configureInstrumentation(exp);

    // Configure parallel execution
    exp.setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
        new ParallelComputationTaskRunnerFactory(), new ParameterBlock()));

    // Add listener
    TaskRuntimeInformationListener taskRTIListener =
        new TaskRuntimeInformationListener();
    exp.getExecutionController().addExecutionListener(taskRTIListener);
    return new Pair<>(exp, taskRTIListener);
  }

  /**
   * Gets the number of replications.
   * 
   * @return the replication
   */
  public int getReplications() {
    return replications;
  }

  /**
   * Sets the number of replications.
   * 
   * @param replications
   *          the new number of replications
   */
  public void setReplications(int replications) {
    this.replications = replications;
  }

  /**
   * Checks if test runs in strict mode.
   * 
   * @return true, if it is strict mode
   */
  public boolean isStrictMode() {
    return strictMode;
  }

  /**
   * Sets the strict-ness (strict here means to fail as soon asone comparison
   * fails).
   * 
   * @param strictMode
   *          the new strict mode
   */
  public void setStrictMode(boolean strictMode) {
    this.strictMode = strictMode;
  }

}

/**
 * A simple container for the description of a simulation task.
 */
final class SimTaskDescription {

  /** The model uri. */
  final URI uri;

  /** The stop time. */
  final Double stopTime;

  /** The model parameters. */
  final Map<String, Object> modelParameters;

  /**
   * Instantiates a new simulation task description.
   * 
   * @param modelSetup
   *          the model setup
   */
  SimTaskDescription(URI uri, Pair<Double, Map<String, Object>> modelSetup) {
    this.uri = uri;
    stopTime = modelSetup.getFirstValue();
    modelParameters = modelSetup.getSecondValue();
  }
}