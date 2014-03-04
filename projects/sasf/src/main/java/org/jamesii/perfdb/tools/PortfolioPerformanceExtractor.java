/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.core.math.statistics.univariate.ArithmeticMean;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * A specific performance extractor that generates
 * {@link PortfolioPerformanceData} instances for portfolio selection.
 * 
 * @see AbstractPerformanceExtractor
 * @see PortfolioPerformanceData
 * @see org.jamesii.asf.portfolios.plugintype.IPortfolioSelector
 * 
 * @author Roland Ewald
 * 
 */
public class PortfolioPerformanceExtractor extends AbstractPerformanceExtractor {

  /**
   * Specifies the default minimum number of configurations a simulation problem
   * should have been tried with before it is considered for portfolio
   * construction.
   */
  private static final int DEFAULT_MIN_NUM_OF_CONFIGURATIONS = 5;

  /** Specifies the type of the performance measure to be collected. */
  private final Class<? extends PerformanceMeasurerFactory> perfMeasurerFactoryClass;

  /**
   * The performance type corresponding to the performance measurer factory
   * (will be loaded after connecting to performance database).
   */
  private IPerformanceType perfType;

  /** Performance vectors of the problems. */
  private List<Double[]> performanceVectors = new ArrayList<>();

  /** Map from the runtime configurations to their index. */
  private Map<IRuntimeConfiguration, Integer> configIndices =
      new HashMap<>();

  /**
   * Specifies the minimum number of configurations a simulation problem should
   * have been tried with before it is considered for portfolio construction.
   */
  private int minNumberOfConfigurations = DEFAULT_MIN_NUM_OF_CONFIGURATIONS;

  /**
   * Instantiates a new portfolio performance extractor.
   * 
   * @param modelMatchURI
   *          the model URI to be matched
   * @param performanceDatabase
   *          the performance database
   * @param performanceMeasurerFactoryClass
   *          the class of the performance measurer factory that corresponds to
   *          the type of performance data to be collected
   */
  public PortfolioPerformanceExtractor(
      String modelMatchURI,
      IPerformanceDatabase performanceDatabase,
      Class<? extends PerformanceMeasurerFactory> performanceMeasurerFactoryClass) {
    super(modelMatchURI, performanceDatabase);
    perfMeasurerFactoryClass = performanceMeasurerFactoryClass;
  }

  /**
   * Extracts performance data for portfolio construction from the performance
   * database.
   * 
   * @return the portfolio performance data
   */
  public PortfolioPerformanceData extract() {
    init();

    List<IProblemDefinition> problems = fetchSimulationProblemsFromDB();
    for (IProblemDefinition problem : problems) {
      syncRuntimeConfigs(problem);
      performanceVectors.add(getAvgPerfVector(problem));
    }

    return new PortfolioPerformanceData(createDoubleMatrix(performanceVectors),
        createRTCArray(), problems.toArray(new IProblemDefinition[problems
            .size()]));
  }

  /**
   * Creates an array of runtime configurations, corresponding to the recorded
   * indices.
   * 
   * @return the array of runtime configurations
   */
  SelectionTree[] createRTCArray() {
    SelectionTree[] configs = new SelectionTree[configIndices.size()];
    for (IRuntimeConfiguration config : configIndices.keySet()) {
      configs[configIndices.get(config)] = config.getSelectionTree();
    }
    return configs;
  }

  @Override
  protected void init() {
    super.init();
    perfType = getPerfDB().getPerformanceType(perfMeasurerFactoryClass);
  }

  /**
   * Calculates the average performance vector for the given problem and type.
   * 
   * @param problem
   *          the problem
   * @param perfType
   *          the performance type to be considered
   * 
   * @return the average performance vector
   */
  Double[] getAvgPerfVector(IProblemDefinition problem) {
    Double[] performanceVector = new Double[configIndices.size()];
    for (IRuntimeConfiguration runtimeConfig : configIndices.keySet()) {
      performanceVector[configIndices.get(runtimeConfig)] =
          getAveragePerformance(problem, runtimeConfig);
    }
    return performanceVector;
  }

  /**
   * Gets the average performance of a given configuration on a given simulation
   * problem.
   * 
   * @param problem
   *          the problem
   * @param runtimeConfig
   *          the runtime configuration
   * @return the average performance
   */
  Double getAveragePerformance(IProblemDefinition problem,
      IRuntimeConfiguration runtimeConfig) {
    List<IApplication> applications =
        getPerfDB().getAllApplications(problem, runtimeConfig);
    List<Double> performances = getPerformances(applications, perfType);
    return ArithmeticMean.arithmeticMean(performances);
  }

  /**
   * Checks whether all runtime configurations applied to this problem are
   * already known.
   * 
   * @param problem
   *          the problem
   */
  void syncRuntimeConfigs(IProblemDefinition problem) {
    List<IRuntimeConfiguration> runtimeConfigs =
        getPerfDB().getAllRuntimeConfigs(problem);
    for (IRuntimeConfiguration runtimeConfig : runtimeConfigs) {
      if (!configIndices.containsKey(runtimeConfig)) {
        configIndices.put(runtimeConfig, configIndices.size());
        incrementPerformanceVectorLength();
      }
    }
  }

  /**
   * Converts list of (equally long!) Double arrays to Double matrix.
   * 
   * @param vectorList
   *          the list of Double arrays
   * 
   * @return the 2d Double matrix
   */
  protected Double[][] createDoubleMatrix(List<Double[]> vectorList) {
    if (vectorList.size() == 0) {
      return new Double[0][0];
    }
    int numOfAlgos = vectorList.get(0).length;
    Double[][] matrix = new Double[numOfAlgos][vectorList.size()];

    for (int i = 0; i < vectorList.size(); i++) {
      for (int j = 0; j < numOfAlgos; j++) {
        matrix[j][i] = vectorList.get(i)[j];
      }
    }

    return matrix;
  }

  /**
   * Fetches problem definitions from database that have been tried with at
   * least the minimum amount of runtime configurations.
   */
  private List<IProblemDefinition> fetchSimulationProblemsFromDB() {
    List<IProblemDefinition> problemDefinitions =
        new ArrayList<>();
    for (IProblemScheme scheme : fetchMatchingSchemesFromDB()) {
      List<IProblemDefinition> schemeProblemDefinitions =
          getPerfDB().getAllProblemDefinitions(scheme);
      for (IProblemDefinition modelSimProblem : schemeProblemDefinitions) {
        if (getPerfDB().getAllRuntimeConfigs(modelSimProblem).size() >= minNumberOfConfigurations) {
          problemDefinitions.add(modelSimProblem);
        }
      }
    }
    return problemDefinitions;
  }

  /**
   * Increments all performance vectors by one, fills in null (as no additional
   * configuration's performance has been recorded).
   */
  protected void incrementPerformanceVectorLength() {
    List<Double[]> incrementedVectors = new ArrayList<>();
    for (Double[] performanceVector : performanceVectors) {
      Double[] incrementedVector = new Double[performanceVector.length + 1];
      System.arraycopy(performanceVector, 0, incrementedVector, 0,
          performanceVector.length);
      incrementedVectors.add(incrementedVector);
    }
    performanceVectors = incrementedVectors;
  }

  public List<Double[]> getPerformanceVectors() {
    return performanceVectors;
  }
}
