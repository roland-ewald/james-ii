/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;
import org.jamesii.asf.portfolios.ga.ListIndividualFactory;
import org.jamesii.asf.portfolios.ga.abort.GenerationCountAbort;
import org.jamesii.asf.portfolios.ga.abort.IAbortCriterion;
import org.jamesii.asf.portfolios.ga.fitness.ASRFitness;
import org.jamesii.asf.portfolios.ga.fitness.IPortfolioFitness;
import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.asf.portfolios.plugintype.PortfolioProblemDescription;
import org.jamesii.asf.portfolios.stochsearch.StochSearchPortfolioSelector;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.core.math.statistics.univariate.StandardDeviation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.simspex.adaptiverunner.policies.EpsilonGreedyFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;

import junit.framework.TestCase;

/**
 * Complex test case with artificial data {@link PerformanceDataGenerator} to
 * test the effect of the {@link GeneticAlgorithmPortfolioSelector} on
 * {@link org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy}.
 * 
 * @author Roland Ewald
 * @author Rene' Schulz
 */

public class TestGeneticAlgorithmPortfolioSelectorWithPolicies extends TestCase {

  /** The Constant delimiter. */
  public static final String delimiter = "\t";

  /** Problem description. */
  static final int MIN_SIZE = 3;

  /** The Constant MAX_SIZE. */
  static final int MAX_SIZE = 6;

  /** The Constant NUM_OF_IND. */
  static final int NUM_OF_IND = 250;

  /** The Constant FILE_NAME. */
  static final String FILE_NAME = "./srs_portfolio.dat";

  /** The Constant MUTATION_RATE. */
  static final double MUTATION_RATE = 0.001;

  /** The problem matrix. */
  Double[][] problemMatrix;

  /** The policies that shall be tested. */
  private MinBanditPolicyFactory[] policyFactories =
      new MinBanditPolicyFactory[] { new EpsilonGreedyFactory() };

  /** The pop sizes. */
  private int[] popSizes = new int[] { NUM_OF_IND };

  /** The acceptable risk. */
  private double acceptableRisk = 1;

  /** The flag that activates fine-granular output. */
  private boolean fineGranularOutput = false;

  /** Used FitnessFunction. */
  static final IPortfolioFitness FITNESS = new ASRFitness();

  /** The number of generations. */
  static final int NUM_OF_GENERATIONS = 40;

  /** Used abortion criterion. */
  static final IAbortCriterion ABORT = new GenerationCountAbort(
      NUM_OF_GENERATIONS);

  /** The overall number of rounds a policy has to play per problem. */
  private static final int NUM_OF_ROUNDS = 20;

  /** The number of replications per policy execution on a given problem. */
  private static final int NUM_OF_POLICY_EXEC_REP = 2;

  /**
   * The number of replicated portfolio generations (= new test data + new
   * portfolio).
   */
  private static final int NUM_OF_PORTFOLIO_GEN_REP = 10;

  /** The Constant OUTPUT_DIRECTORY. */
  private static final String OUTPUT_DIRECTORY = "./results_gaportfolios/";

  /**
   * The flag that determines if results shall be compared to stochastic search.
   */
  private static final boolean COMPARE_STOCHASTIC = false;

  /** The number of algorithm clusters. */
  private static int[] numOfClusters = new int[] { 50 };

  /** The number of algorithms per cluster. */
  private static int[] numOfAlgoPerCluster = new int[] { 5 };

  /** The number of problems. */
  private static int[] numOfProblems = new int[] { 50 };

  /** The number of problem classes. */
  private static int[] numOfProblemClasses = new int[] { 5 };

  /** The distance between clusters. */
  private static double[] distanceBetweenClusters = new double[] { 10 };

  /** Names of the elements describing the current scenario. */
  private static final String[] descriptionElements = new String[] {
      "#algo_clusters", "algos_per_cluster", "cluster_distance", "#problems",
      "population_size", "problem_classes" };

  /**
   * Test policy.
   */
  public void testPolicy() {

    initialiseOutputDirectory();

    ThreadPoolExecutor executor =
        new ThreadPoolExecutor(8, 100, 10, TimeUnit.HOURS,
            new ArrayBlockingQueue<Runnable>(100));

    // was: 1, 3, 10
    for (int dataReps : new int[] { 1 }) {
      // was: 0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1
      for (double accRisk : new double[] { .5 }) {
        final double risk = accRisk;
        final int datReps = dataReps;
        Runnable r = new Runnable() {
          @Override
          public void run() {
            acceptableRisk = risk;
            AggregatedData aggregatedData = new AggregatedData();
            try {
              for (int clusterNumber : numOfClusters) {
                for (int algosPerCluster : numOfAlgoPerCluster) {
                  for (int problemNumber : numOfProblems) {
                    for (double clusterDistance : distanceBetweenClusters) {
                      for (int popSize : popSizes) {
                        for (int problemClasses : numOfProblemClasses) {
                          PerformanceDataGenerator generator =
                              new PerformanceDataGenerator(clusterNumber,
                                  algosPerCluster, problemNumber,
                                  clusterDistance, problemClasses, datReps);
                          GeneticAlgorithmPortfolioSelector gaps =
                              new GeneticAlgorithmPortfolioSelector();
                          gaps.setAbortCriterion(ABORT);
                          gaps.setFitness(FITNESS);
                          gaps.setNumIndividuals(popSize);
                          gaps.setMutationRate(MUTATION_RATE);
                          String[] desc =
                              new String[] { "" + clusterNumber,
                                  "" + algosPerCluster, "" + clusterDistance,
                                  "" + problemNumber, "" + popSize,
                                  "" + problemClasses };
                          testGASetupForProblem(desc, "clusters_"
                              + clusterNumber + "_algo_per_cluster"
                              + algosPerCluster + "_problems" + problemNumber
                              + "_cdistance" + clusterDistance + "_popsize"
                              + popSize + "_problemclasses" + problemClasses,
                              generator, gaps, aggregatedData);
                        }
                      }
                    }
                  }
                }
              }
            } catch (Throwable t) {
              t.printStackTrace();
              fail("There should be no errors.");
            } finally {
              toCSV(aggregatedData, OUTPUT_DIRECTORY + NUM_OF_GENERATIONS
                  + "gens" + NUM_OF_IND + "ind_risk_" + risk + "_reps_"
                  + datReps + "_aggregated_diffs.csv");
            }
          }
        };
        executor.execute(r);
      }
    }

    try {
      executor.shutdown();
      executor.awaitTermination(100, TimeUnit.SECONDS);
    } catch (InterruptedException e) {
      e.printStackTrace();
      fail("There should be no interruption.");
    }

    checkOutputDirectory();
  }

  /**
   * Initialise output directory.
   */
  private void initialiseOutputDirectory() {
    File f = new File(OUTPUT_DIRECTORY);
    if (!f.exists()) {
      assertTrue("Should be able to create output directory...", f.mkdir());
    }
  }

  /**
   * Check output directory.
   */
  private void checkOutputDirectory() {
    File outDir = new File(OUTPUT_DIRECTORY);
    assertTrue("Output directory should exist", outDir.exists());
    assertTrue("There should be some output files", outDir.list().length > 0);
    for (String file : outDir.list()) {
      assertFalse("All output files should have some content.", file.isEmpty());
    }
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments (none required)
   */
  public static void main(String[] args) {
    TestGeneticAlgorithmPortfolioSelectorWithPolicies test =
        new TestGeneticAlgorithmPortfolioSelectorWithPolicies();
    test.testPolicy();
  }

  /**
   * Test GA setup for problem.
   * 
   * @param generator
   *          the generator
   * @param gaps
   *          the gaps
   * @param configDesc
   *          the description of the configuration
   * @param description
   *          the description
   * @param aggregatedData
   *          the aggregated data
   */
  private void testGASetupForProblem(String[] description, String configDesc,
      PerformanceDataGenerator generator,
      GeneticAlgorithmPortfolioSelector gaps, AggregatedData aggregatedData) {

    for (int i = 0; i < NUM_OF_PORTFOLIO_GEN_REP; i++) {

      // Generate test data
      SimSystem.report(Level.INFO, "Generating portfolios for replication #"
          + i + " for " + configDesc);
      Pair<double[], double[]> portfolios = createPortfolios(generator, gaps);

      aggregatedData.setPortfolioQuality(generator.evaluatePortfolio(portfolios
          .getFirstValue()));

      for (MinBanditPolicyFactory policyFactory : policyFactories) {
        IMinBanditPolicy policy = policyFactory.create(new ParameterBlock());
        SimSystem.report(Level.INFO, "\tExecuting policy:" + policy.getClass());
        aggregatedData.registerGAPerformance(
            description,
            Strings.dispClassName(policy.getClass()),
            runPolicyOnPortfolio(configDesc + "_r" + i + "_", policy,
                generator, portfolios.getFirstValue()));
        // If there is a stochastic portfolio, generate this as well
        if (portfolios.getSecondValue() != null) {
          aggregatedData.registerStochPerformance(
              description,
              Strings.dispClassName(policy.getClass()),
              runPolicyOnPortfolio("stoch__" + configDesc + "_r" + i + "_",
                  policy, generator, portfolios.getSecondValue()));
        }
        // TODO: Output policy data
      }
    }
  }

  /**
   * Creates the portfolios.
   * 
   * @param generator
   *          the generator
   * @param gaps
   *          the gaps
   * 
   * @return the pair<double[],double[]>
   */
  private Pair<double[], double[]> createPortfolios(
      PerformanceDataGenerator generator, GeneticAlgorithmPortfolioSelector gaps) {

    PortfolioPerformanceData perfData = generator.generateTestMatrix();
    PortfolioProblemDescription pdd =
        new PortfolioProblemDescription(perfData, acceptableRisk, false,
            MIN_SIZE, MAX_SIZE);
    gaps.setIndividualFactory(new ListIndividualFactory());
    double[] portfolio = gaps.portfolio(pdd);
    double[] stochasticPortfolio = null;

    if (COMPARE_STOCHASTIC) {
      StochSearchPortfolioSelector ssps =
          new StochSearchPortfolioSelector(gaps.getNumIndividuals()
              * NUM_OF_GENERATIONS, SimSystem.getRNGGenerator().getNextRNG());
      stochasticPortfolio = ssps.portfolio(pdd);
    }
    return new Pair<>(portfolio, stochasticPortfolio);
  }

  /**
   * Writes data to CSV.
   * 
   * @param aggregatedDifferences
   *          the aggregated differences
   * @param fileName
   *          the file name
   */
  private void toCSV(AggregatedData aggregatedDifferences, String fileName) {
    try {

      List<String> scenarios =
          new ArrayList<>(aggregatedDifferences.avgOverheads.keySet());

      Collections.sort(scenarios);
      try (FileWriter fileWriter = new FileWriter(fileName)) {
        List<String> policyNames = new ArrayList<>(aggregatedDifferences.avgOverheads.get(
                                 scenarios.get(0)).keySet());
        Collections.sort(policyNames);
        int portfolioSelectionReps = NUM_OF_PORTFOLIO_GEN_REP;
        fileWriter.append(writeHeader(policyNames, portfolioSelectionReps));
        for (String scenario : scenarios) {
          StringBuffer strBuf = new StringBuffer();
          String[] list = scenario.split(AggregatedData.descriptionDelimiter);
          for (String listElem : list) {
            strBuf.append(listElem + delimiter);
          }

          strBuf.append(aggregatedDifferences.portfolioQuality + delimiter);

          for (String policyName : policyNames) {
            double overheadMeanGA =
                ArithmeticMean.arithmeticMean(aggregatedDifferences.avgOverheads
                    .get(scenario).get(policyName).gaPortfolio
                    .toArray(new Double[0]));
            double overheadStdDevGA =
                StandardDeviation
                    .standardDeviation(aggregatedDifferences.avgOverheads.get(
                        scenario).get(policyName).gaPortfolio);
            double overheadMeanSt =
                ArithmeticMean.arithmeticMean(aggregatedDifferences.avgOverheads
                    .get(scenario).get(policyName).stochPortfolio
                    .toArray(new Double[0]));
            double overheadStdDevSt =
                StandardDeviation
                    .standardDeviation(aggregatedDifferences.avgOverheads.get(
                        scenario).get(policyName).stochPortfolio);
            double overheadMeanNone =
                ArithmeticMean.arithmeticMean(aggregatedDifferences.avgOverheads
                    .get(scenario).get(policyName).noPortfolio
                    .toArray(new Double[0]));
            double overheadStdDevNone =
                StandardDeviation
                    .standardDeviation(aggregatedDifferences.avgOverheads.get(
                        scenario).get(policyName).noPortfolio);

            strBuf.append(" " + delimiter);

            strBuf.append(Strings.formatNumberForDisplay(overheadMeanGA, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadStdDevGA, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadMeanNone, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadStdDevNone, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadMeanSt, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadStdDevSt, 4)
                + delimiter);

            strBuf.append(Strings.formatNumberForDisplay(overheadMeanNone
                - overheadMeanGA, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadStdDevNone
                - overheadStdDevGA, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadMeanSt
                - overheadMeanGA, 4)
                + delimiter);
            strBuf.append(Strings.formatNumberForDisplay(overheadStdDevSt
                - overheadStdDevGA, 4)
                + delimiter);

          }
          strBuf.append("\n");
          fileWriter.append(strBuf);

        }
      }
    } catch (IOException ex) {
      ex.printStackTrace();
    }
  }

  /**
   * Write header.
   * 
   * @param policyNames
   *          the policy names
   * @param portfolioSelectionReps
   *          the number of portfolio selection replications
   * 
   * @return the string
   */
  private String writeHeader(List<String> policyNames,
      int portfolioSelectionReps) {
    StringBuffer strBuf = new StringBuffer();
    for (String headerElement : descriptionElements) {
      strBuf.append(headerElement + delimiter);
    }
    strBuf.append("Overall Portfolio Quality" + delimiter);
    for (String policyName : policyNames) {
      strBuf.append(policyName + delimiter);
      for (String prefix : new String[] { "GA", "None", "Stoch", "Real-GA",
          "Stoch-GA" }) {
        strBuf.append(prefix + " / Mean" + delimiter);
        strBuf.append(prefix + " / StdDev" + delimiter);
      }
    }
    strBuf.append("\n");
    return strBuf.toString();
  }

  /**
   * Run policy on problems with and without given portfolio, using the
   * generator to generate test data.
   * 
   * @param policy
   *          the policy
   * @param generator
   *          the generator
   * @param portfolio
   *          the portfolio
   * @param configDesc
   *          the configuration description
   * 
   * @return the empirical overhead, averaged over round, replication, and
   *         problem for (complete_portfolio, portfolio)
   */
  private Pair<Double, Double> runPolicyOnPortfolio(String configDesc,
      IMinBanditPolicy policy, PerformanceDataGenerator generator,
      double[] portfolio) {

    double[] completePortfolio = createCompletePortfolio(portfolio.length);
    List<RawPerformanceData> performanceDataWithPortfolio =
        new ArrayList<>();
    List<RawPerformanceData> performanceDataWithoutPortfolio =
        new ArrayList<>();
    List<Double[]> overheadDifferences = new ArrayList<>();

    double avgOverheadPerRunSumWithPortfolio = 0.0;
    double avgOverheadPerRunSumWithoutPortfolio = 0.0;

    for (int problem = 0; problem < generator.getNumOfProblems(); problem++) {
      for (int i = 0; i < NUM_OF_POLICY_EXEC_REP; i++) {
        RawPerformanceData perfWithPortfolio =
            generatePerformanceData(policy, generator, portfolio, problem, i,
                true);
        performanceDataWithPortfolio.add(perfWithPortfolio);
        avgOverheadPerRunSumWithPortfolio +=
            ArithmeticMean.arithmeticMean(perfWithPortfolio.policyOverhead);

        RawPerformanceData perfWithoutPortfolio =
            generatePerformanceData(policy, generator, completePortfolio,
                problem, i, false);
        performanceDataWithoutPortfolio.add(perfWithoutPortfolio);
        avgOverheadPerRunSumWithoutPortfolio +=
            ArithmeticMean.arithmeticMean(perfWithoutPortfolio.policyOverhead);

        if (fineGranularOutput) {
          Double[] overheadDifference =
              calculateOverheadDifference(perfWithoutPortfolio.policyOverhead,
                  perfWithPortfolio.policyOverhead);
          overheadDifferences.add(overheadDifference);
        }
      }
    }

    if (fineGranularOutput) {
      toCSV(
          overheadDifferences,
          OUTPUT_DIRECTORY + configDesc
              + Strings.dispClassName(policy.getClass()) + "_oh_diff.dat");
    }

    int measurements = generator.getNumOfProblems() * NUM_OF_POLICY_EXEC_REP;
    return new Pair<>(avgOverheadPerRunSumWithoutPortfolio
        / measurements, avgOverheadPerRunSumWithPortfolio / measurements);
  }

  /**
   * Writes data to CSV.
   * 
   * @param overheadDifferences
   *          the overhead differences
   * @param fileName
   *          the file name
   */
  private void toCSV(List<Double[]> overheadDifferences, String fileName) {
    try {
      try (FileWriter fileWriter = new FileWriter(fileName)) {
        for (Double[] ohDifferences : overheadDifferences) {
          StringBuffer strBuf = new StringBuffer();
          for (Double ohDiff : ohDifferences) {
            strBuf.append(ohDiff + delimiter);
          }
          fileWriter.append(strBuf.toString());
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /**
   * Calculate overhead difference per run.
   * 
   * @param overhead1
   *          the overhead to be subtracted from
   * @param overhead2
   *          the overhead that is subtracted
   * 
   * @return the resulting array: overhead1 - overhead2
   */
  private Double[] calculateOverheadDifference(Double[] overhead1,
      Double[] overhead2) {
    Double[] difference = new Double[overhead1.length];
    for (int i = 0; i < overhead1.length; i++) {
      difference[i] = overhead1[i] - overhead2[i];
    }
    return difference;
  }

  /**
   * Generate performance data.
   * 
   * @param policy
   *          the policy
   * @param generator
   *          the generator
   * @param portfolio
   *          the portfolio
   * @param problem
   *          the problem
   * @param replication
   *          the replication
   * @param portfolioUsed
   *          the flag to tell if portfolio was used
   * 
   * @return a structure containing the raw performance data
   */
  private RawPerformanceData generatePerformanceData(IMinBanditPolicy policy,
      PerformanceDataGenerator generator, double[] portfolio, int problem,
      int replication, boolean portfolioUsed) {
    PortfolioPolicyRunner runner =
        new PortfolioPolicyRunner(policy, portfolio, generator);
    runner.runPolicy(NUM_OF_ROUNDS, problem);
    Double[] overheadPolicy = runner.getReward();
    return new RawPerformanceData(portfolioUsed, problem, replication,
        overheadPolicy);
  }

  /**
   * Creates a well-balanced portfolio (all assets get the same share).
   * 
   * @param size
   *          the size
   * 
   * @return a portfolio containing all sets
   */
  private double[] createCompletePortfolio(int size) {
    double[] completePortfolio = new double[size];
    for (int i = 0; i < size; i++) {
      completePortfolio[i] = 1.0 / size;
    }
    return completePortfolio;
  }
}

/**
 * Simple container for the raw performance data to be stored.
 * 
 * @author Roland Ewald
 */
class RawPerformanceData {

  /** Flag to signal that a portfolio was used. */
  final boolean portfolioUsed;

  /** The problem number the policy was applied to. */
  final int problemNumber;

  /** The replication of this execution. */
  final int replicationNumber;

  /** The overheads of the policy (w.r.t. the optimum), per round. */
  final Double[] policyOverhead;

  /**
   * Instantiates a new raw performance data.
   * 
   * @param portUsed
   *          tells if portfolio was used or not
   * @param probNumber
   *          the problem number
   * @param replication
   *          the replication number
   * @param overhead
   *          the overhead
   */
  RawPerformanceData(boolean portUsed, int probNumber, int replication,
      Double[] overhead) {
    portfolioUsed = portUsed;
    problemNumber = probNumber;
    replicationNumber = replication;
    policyOverhead = overhead;
  }

  /**
   * Sums up the overhead.
   * 
   * @return the sum of all overheads
   */
  public double sumOverhead() {
    double sum = 0;
    for (double overhead : policyOverhead) {
      sum += overhead;
    }
    return sum;
  }
}

/**
 * Simple container to store the average overheads.
 */
class AverageOverheads {

  /** The overheads GA-based portfolio. */
  List<Double> gaPortfolio = new ArrayList<>();

  /** The overheads when no portfolio is used. */
  List<Double> noPortfolio = new ArrayList<>();

  /** The overheads for the portfolio selected by random search. */
  List<Double> stochPortfolio = new ArrayList<>();
}

/**
 * Simple structure to hold the aggregated high-level data.
 * 
 * @author Roland Ewald
 * 
 */
class AggregatedData {

  /**
   * A map policy name => (list of average overhead differences, replicated by
   * different constructed portfolios).
   */
  final Map<String, Map<String, AverageOverheads>> avgOverheads =
      new HashMap<>();

  /** The delimiter for the description. */
  final static String descriptionDelimiter = "=";

  /**
   * The quality of the portfolio, as determined by the
   * {@link PerformanceDataGenerator}.
   */
  double portfolioQuality;

  /**
   * Register performance of the GA-based portfolio.
   * 
   * @param description
   *          the description
   * @param policyName
   *          the policy name
   * @param avgOverhead
   *          the average overhead
   */
  void registerGAPerformance(String[] description, String policyName,
      Pair<Double, Double> avgOverhead) {
    AverageOverheads storedAvgOverheads =
        retrieveStoredOverheads(description, policyName);
    storedAvgOverheads.noPortfolio.add(avgOverhead.getFirstValue());
    storedAvgOverheads.gaPortfolio.add(avgOverhead.getSecondValue());
  }

  /**
   * Retrieves stored overheads, creates data structures on the go.
   * 
   * @param description
   *          the description of the scenario
   * @param policyName
   *          the policy name
   * 
   * @return the average overheads
   */
  private AverageOverheads retrieveStoredOverheads(String[] description,
      String policyName) {
    String desc = Strings.getSeparatedList(description, descriptionDelimiter);
    if (!avgOverheads.containsKey(desc)) {
      avgOverheads.put(desc, new HashMap<String, AverageOverheads>());
    }
    if (!avgOverheads.get(desc).containsKey(policyName)) {
      avgOverheads.get(desc).put(policyName, new AverageOverheads());
    }
    AverageOverheads storedAvgOverheads =
        avgOverheads.get(desc).get(policyName);
    return storedAvgOverheads;
  }

  /**
   * Register performance of stochastically selected portfolio.
   * 
   * @param description
   *          the description
   * @param policyName
   *          the policy name
   * @param avgOverhead
   *          the avg overhead
   */
  public void registerStochPerformance(String[] description, String policyName,
      Pair<Double, Double> avgOverhead) {
    AverageOverheads storedAvgOverheads =
        retrieveStoredOverheads(description, policyName);
    storedAvgOverheads.noPortfolio.add(avgOverhead.getFirstValue());
    storedAvgOverheads.stochPortfolio.add(avgOverhead.getSecondValue());
  }

  public void setPortfolioQuality(double pQuality) {
    portfolioQuality = pQuality;
  }
}
