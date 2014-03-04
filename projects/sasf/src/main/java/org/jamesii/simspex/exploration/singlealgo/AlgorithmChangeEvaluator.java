/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.simspex.adaptiverunner.AdaptiveTaskRunnerFactory;
import org.jamesii.simspex.adaptiverunner.policies.BiasedRandomSelectionFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;
import org.jamesii.simspex.gui.PerfDBRecorder;
import org.jamesii.simspex.util.SimulationProblemDefinition;


/**
 * This exploration algorithm accesses the {@link IPerformanceDatabase} to check
 * a single algorithm against existing performance data.
 * 
 * @author Roland Ewald
 */
public class AlgorithmChangeEvaluator {

  /**
   * The default percentage of applications that have to be registered for a
   * problem's problem instances in order to be considered.
   */
  private static final double DEFAULT_MIN_SHARE_APPS = 0.5;

  /**
   * The default for replication factor, which determines how many replications
   * shall be done per comparison job
   */
  private static final int DEFAULT_REP_FACTOR = 10;

  /** The default maximal number of jobs to be created. */
  private static final int DEFAULT_MAX_JOBS = 50;

  /** The factory of the algorithm to be re-checked. */
  private final Factory<?> algorithmFactory;

  /** The class of the base factory. */
  private final Class<? extends Factory<?>> baseFactClass;

  /** The performance database. */
  private final IPerformanceDatabase perfDB;

  /** The performance type type. */
  private IPerformanceType perfType;

  /**
   * The String that should be contained in all benchmark models to be
   * considered.
   */
  private String bmURIMatch;

  /** The policy to compare both algorithms. */
  private MinBanditPolicyFactory policyFactory =
      new BiasedRandomSelectionFactory();

  /**
   * Percentage of configurations that have to be registered for a problem in
   * order to be considered. Value has to be in [0,1], default is 1, i.e. only
   * problems to which the maximal number of runtime configurations have been
   * applied are considered.
   */
  private double minConfigsShare = 1;

  /**
   * The percentage of applications that have to be registered for a problem's
   * problem instances in order to be considered. Value has to be in [0,1],
   * default is 0.5, i.e. only problems with at least half of the maximal number
   * of applications are considered.
   */
  private double minAppsShare = DEFAULT_MIN_SHARE_APPS;

  /**
   * The flag to include the best-performing non-affected configuration into the
   * comparison, for re-comparing their results.
   */
  private boolean recompare = false;

  /**
   * The flag to determine if *all* configurations using the new version of the
   * factory shall be considered, or if the user is only interested in the
   * configuration with the best performance (speeds up exploration).
   */
  private boolean considerAll = false;

  /**
   * The (exclusive) maximum wall-clock time to consider a configuration for
   * comparison. Has priority over {@link AlgorithmChangeEvaluator#recompare}
   * and {@link AlgorithmChangeEvaluator#considerAll}. Broken configurations
   * (i.e., those with negative performance) will be assigned with MAX_VALUE and
   * will thereby never included into the jobs.
   */
  private double maxWCT = Double.MAX_VALUE;

  /**
   * The replication factor determines how many replications shall be done per
   * comparison job. A factor of k means that the number of replications to be
   * done is k * #configs.
   */
  private int repFactor = DEFAULT_REP_FACTOR;

  /** The maximal number of jobs to be created. */
  private int maxJobs = DEFAULT_MAX_JOBS;

  /** The list of comparison jobs to be executed. */
  private List<ComparisonJob> jobs = new ArrayList<>();

  /**
   * Auxiliary set with all config_id of configuration containing the algorithm
   * that was changed. Is generated by
   * {@link AlgorithmChangeEvaluator#checkConfigs(Map)}.
   */
  private Set<Long> configsWithAlgo = null;

  /** The output handler. */
  private IOutputACEHandler outputHandler;

  /**
   * The number of threads to be used. Adjust carefully, as the number of
   * running threads may affect the execution time.
   */
  private int numOfThreads = 1;

  /**
   * Instantiates a new single algorithm explorer.
   * 
   * @param algoFactory
   *          the factory of the algorithm to be investigated
   * @param perfDatabase
   *          the performance database to be considered
   * @param modelMatch
   *          the model match
   * @param pType
   *          the type of performance to guide comparison
   */
  public AlgorithmChangeEvaluator(Factory<?> algoFactory,
      IPerformanceDatabase perfDatabase, String modelMatch,
      IPerformanceType pType, IOutputACEHandler oHandler) {
    algorithmFactory = algoFactory;
    perfDB = perfDatabase;
    bmURIMatch = modelMatch;
    perfType = pType;
    outputHandler = oHandler;

    @SuppressWarnings("unchecked")
    // getClass() call to wild-card type
    Class<? extends AbstractFactory<?>> af =
        SimSystem.getRegistry().getAbstractFactoryForFactory(
            (Class<? extends Factory<?>>) algorithmFactory.getClass());
    baseFactClass =
        SimSystem.getRegistry().getBaseFactoryForAbstractFactory(af);

  }

  /**
   * Creates the comparison jobs by querying the {@link IPerformanceDatabase}.
   * 
   * @return the list of created comparison jobs
   */
  public void generateJobs() {

    jobs.clear();

    // Access performance database to retrieve suitable benchmark models and
    // -problems
    List<IProblemDefinition> problems = new ArrayList<>();

    try {

      // Check which models (and therefore, simulation problems), match
      // the target string
      List<IProblemScheme> problemSchemes = perfDB.getAllProblemSchemes();
      for (IProblemScheme scheme : problemSchemes) {
        if (scheme.getUri().toString().contains(bmURIMatch)) {
          problems.addAll(perfDB.getAllProblemDefinitions(scheme));
        }
      }

      // Retrieve all runtime configurations for a simulation problem
      Map<IProblemDefinition, List<IRuntimeConfiguration>> problemConfigs =
          new HashMap<>();
      int maxRTConfigs = 0;
      for (IProblemDefinition problem : problems) {
        List<IRuntimeConfiguration> runtimeConfigurations =
            perfDB.getAllCurrentRTConfigs(problem);
        problemConfigs.put(problem, runtimeConfigurations);
        if (maxRTConfigs < runtimeConfigurations.size()) {
          maxRTConfigs = runtimeConfigurations.size();
        }
      }

      // Filter all problems with too few runtime configurations
      Pair<Map<IProblemDefinition, List<IRuntimeConfiguration>>, Map<IProblemDefinition, List<IApplication>>> filteredProblems =
          filterProblems(problemConfigs, maxRTConfigs);

      jobs.addAll(createJobs(filteredProblems.getFirstValue(),
          filteredProblems.getSecondValue()));

    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null,
          "Error while accessing performance database.", null, ex);
    }
  }

  /**
   * Filter out problems, for example if too few runtime configurations have
   * been applied to them.
   * 
   * @param problemConfigs
   *          the map problem definition => applied runtime configurations
   * @param maxRTConfigs
   *          the maximum number of runtime configurations that have been
   *          applied to a single problem definition so far
   * @return a tuple of maps, the first associating problem definitions with
   *         configurations, the second associating problem definitions with
   *         applications
   */
  private Pair<Map<IProblemDefinition, List<IRuntimeConfiguration>>, Map<IProblemDefinition, List<IApplication>>> filterProblems(
      Map<IProblemDefinition, List<IRuntimeConfiguration>> problemConfigs,
      int maxRTConfigs) {

    Map<IProblemDefinition, List<IRuntimeConfiguration>> probConfigs =
        new HashMap<>();
    Map<IProblemDefinition, List<IApplication>> probApps =
        new HashMap<>();

    for (Entry<IProblemDefinition, List<IRuntimeConfiguration>> problemConfig : problemConfigs
        .entrySet()) {
      if (problemConfig.getValue().size() >= minConfigsShare * maxRTConfigs) {
        probConfigs.put(problemConfig.getKey(), problemConfig.getValue());
      }
    }

    // Retrieve all applications of the runtime configurations for the
    // filtered problems
    int maxApps = 0;
    for (IProblemDefinition problem : probConfigs.keySet()) {
      List<IApplication> apps = perfDB.getAllApplications(problem);
      probApps.put(problem, apps);
      if (maxApps < apps.size()) {
        maxApps = apps.size();
      }
    }

    // Filter all problems with too few applications
    List<IProblemDefinition> tooFewApps = new ArrayList<>();
    for (Entry<IProblemDefinition, List<IApplication>> problem : probApps
        .entrySet()) {
      if (problem.getValue().size() < minAppsShare * maxApps) {
        tooFewApps.add(problem.getKey());
      }
    }

    // Clean up both maps
    for (IProblemDefinition problem : tooFewApps) {
      probApps.remove(problem);
      probConfigs.remove(problem);
    }

    return new Pair<>(
        probConfigs, probApps);
  }

  /**
   * Creates comparison jobs.
   * 
   * @param probConfigs
   *          the simulation problems and their configurations
   * @param probApps
   *          the problem applications per simulation problem
   * 
   * @return the collection<? extends comparison job>
   */
  protected List<ComparisonJob> createJobs(
      Map<IProblemDefinition, List<IRuntimeConfiguration>> probConfigs,
      Map<IProblemDefinition, List<IApplication>> probApps) {

    List<ComparisonJob> compJobs = new ArrayList<>();

    // Check which runtime configuration contains the factory under scrutiny
    configsWithAlgo = checkConfigs(probConfigs);

    // Create comparison jobs
    for (Entry<IProblemDefinition, List<IRuntimeConfiguration>> problemEntry : probConfigs
        .entrySet()) {

      // Filter interesting configurations
      Pair<List<IRuntimeConfiguration>, RTCComparator> filterResults =
          filterConfigs(problemEntry.getValue(),
              probApps.get(problemEntry.getKey()));

      List<IRuntimeConfiguration> configsToBeChecked =
          filterResults.getFirstValue();

      // Generate parameter block for each one
      Map<Long, ParameterBlock> configSetups =
          new HashMap<>();
      for (IRuntimeConfiguration rtConfig : configsToBeChecked) {
        configSetups.put(rtConfig.getID(), rtConfig.getSelectionTree()
            .toParamBlock());
      }

      // Add job, propagate problem, filtered configurations, and current
      // performances (for latter results analysis)
      compJobs.add(new ComparisonJob(problemEntry.getKey(), configSetups,
          filterResults.getSecondValue()));
    }

    // If larger than allowed: dismiss jobs randomly
    IRandom random = SimSystem.getRNGGenerator().getNextRNG();
    int deleteJobs = compJobs.size() - maxJobs;
    for (int i = 0; i < deleteJobs; i++) {
      compJobs.remove((int) (random.nextDouble() * compJobs.size()));
    }
    return compJobs;
  }

  /**
   * Filters configurations according to user settings.
   * 
   * @param configs
   *          the list of runtime configurations to be filtered
   * @param apps
   *          the list of available applications (to select the best set-up
   *          etc.)
   * 
   * @return the pair (list of filtered runtime configurations, RTC comparator
   *         having all performance data)
   */
  protected Pair<List<IRuntimeConfiguration>, RTCComparator> filterConfigs(
      List<IRuntimeConfiguration> configs, List<IApplication> apps) {

    List<IRuntimeConfiguration> result = new ArrayList<>();

    // Order configurations by their applications
    RTCComparator rtcComp = new RTCComparator(apps, perfType, perfDB);
    Collections.sort(configs, rtcComp);
    Map<Long, Double> avgRTCPerf = rtcComp.getAvgPerfMap();

    // Filter configs
    boolean selectContainingOK = false;
    boolean selectOthersOK = !recompare;
    for (IRuntimeConfiguration config : configs) {
      // Don't include any config that takes too long to execute
      if (avgRTCPerf.get(config.getID()) >= maxWCT
          || (selectContainingOK && selectOthersOK)) {
        break;
      }
      boolean containsAlgo = configsWithAlgo.contains(config.getID());
      // If all configurations containing the algorithm shall be
      // considered and this is one
      if (!selectContainingOK && containsAlgo) {
        result.add(config);
        if (!considerAll) {
          selectContainingOK = true;
        }
      }
      // Choose best-performing non-affected configuration if still
      // necessary
      if (!selectOthersOK && !containsAlgo) {
        result.add(config);
        selectOthersOK = true;
      }
    }

    return new Pair<>(result, rtcComp);
  }

  /**
   * Checks for all configurations whether they include the parameter block that
   * defines to use the algorithm in question.
   * 
   * @param probConfigs
   *          the map simulation problem => list of configurations
   * 
   * @return the id set of configurations that are affected
   */
  protected Set<Long> checkConfigs(
      Map<IProblemDefinition, List<IRuntimeConfiguration>> probConfigs) {

    Map<Long, Boolean> resultMap = new HashMap<>();
    for (List<IRuntimeConfiguration> rtConfigs : probConfigs.values()) {
      for (IRuntimeConfiguration rtConfig : rtConfigs) {
        if (!resultMap.containsKey(rtConfig.getID())) {
          resultMap.put(rtConfig.getID(), containsFactoryPB(rtConfig));
        }
      }
    }

    Set<Long> result = new HashSet<>();
    for (Entry<Long, Boolean> config : resultMap.entrySet()) {
      if (config.getValue()) {
        result.add(config.getKey());
      }
    }

    return result;
  }

  /**
   * Tests if the algorithm's factory parameter block is a sub-block for the
   * given configuration.
   * 
   * @param RUNTIME_CONFIGURATION
   *          the runtime configuration
   * 
   * @return true if algorithm factory has a sub-block, false if not found
   */
  protected boolean containsFactoryPB(IRuntimeConfiguration rtConfig) {
    // TODO: This only works if each algorithm appears at most *once* per
    // parent
    // parameter block
    return ParameterBlocks.searchSubBlock(rtConfig.getSelectionTree()
        .toParamBlock(), baseFactClass.getName(), algorithmFactory.getClass()
        .getName()) != null;
  }

  /**
   * Executes evaluation for single algorithm.
   * 
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public void execute() throws IOException {

    outputHandler.init(configsWithAlgo);

    // Execute all jobs
    for (ComparisonJob job : jobs) {

      // Configure base experiment
      BaseExperiment exp = new BaseExperiment();
      IProblemDefinition problem = job.getProblem();
      IProblemScheme bm = problem.getProblemScheme();
      exp.setModelLocation(bm.getUri());
      exp.setDefaultSimStopTime(SimulationProblemDefinition
          .getSimStopTime(problem));
      exp.setFixedModelParameters(new HashMap<String, Object>(problem
          .getSchemeParameters()));
      exp.setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
          new AdaptiveTaskRunnerFactory(), (new ParameterBlock())
              .addSubBl(AdaptiveTaskRunnerFactory.PORTFOLIO,
                  new ArrayList<>(job.getConfigs().values()))
              .addSubBl(AdaptiveTaskRunnerFactory.POLICY,
                  policyFactory.getClass().getName())
              .addSubBl(ParallelComputationTaskRunnerFactory.NUM_CORES,
                  numOfThreads)));

      Map<Long, ParameterBlock> configs = job.getConfigs();
      exp.setRepeatRuns(repFactor * configs.size());

      // Set up performance recorder
      // TODO: Make recording optional
      PerfDBRecorder perfRec = new PerfDBRecorder();
      exp.getExecutionController().addExecutionListener(perfRec);
      perfRec.getNewVerRTConfigIDs().addAll(configsWithAlgo);

      // Execute experiment, record performance
      ComparisonJobResultListener cjrl =
          new ComparisonJobResultListener(job, perfType);
      perfRec.addListener(cjrl);
      perfRec.start();
      exp.execute();
      perfRec.stop();

      outputHandler.output(job.getProblem(), job);
    }

    outputHandler.finish();
  }

  /**
   * Gets the jobs.
   * 
   * @return the jobs
   */
  public List<ComparisonJob> getJobs() {
    return jobs;
  }

  /**
   * Gets the performance type.
   * 
   * @return the perfType
   */
  public IPerformanceType getPerfType() {
    return perfType;
  }

  /**
   * Sets the perf type.
   * 
   * @param perfType
   *          the perfType to set
   */
  public void setPerfType(IPerformanceType perfType) {
    this.perfType = perfType;
  }

  /**
   * Gets the bm uri match.
   * 
   * @return the bmURIMatch
   */
  public String getBmURIMatch() {
    return bmURIMatch;
  }

  /**
   * Sets the bm uri match.
   * 
   * @param bmURIMatch
   *          the bmURIMatch to set
   */
  public void setBmURIMatch(String bmURIMatch) {
    this.bmURIMatch = bmURIMatch;
  }

  /**
   * Gets the policy factory.
   * 
   * @return the policyFactory
   */
  public MinBanditPolicyFactory getPolicyFactory() {
    return policyFactory;
  }

  /**
   * Sets the policy factory.
   * 
   * @param policyFactory
   *          the policyFactory to set
   */
  public void setPolicyFactory(MinBanditPolicyFactory policyFactory) {
    this.policyFactory = policyFactory;
  }

  /**
   * Gets the min configs share.
   * 
   * @return the minConfigsShare
   */
  public double getMinConfigsShare() {
    return minConfigsShare;
  }

  /**
   * Sets the min configs share.
   * 
   * @param minConfigsShare
   *          the minConfigsShare to set
   */
  public void setMinConfigsShare(double minConfigsShare) {
    this.minConfigsShare = minConfigsShare;
  }

  /**
   * Gets the min apps share.
   * 
   * @return the minAppsShare
   */
  public double getMinAppsShare() {
    return minAppsShare;
  }

  /**
   * Sets the min apps share.
   * 
   * @param minAppsShare
   *          the minAppsShare to set
   */
  public void setMinAppsShare(double minAppsShare) {
    this.minAppsShare = minAppsShare;
  }

  /**
   * Checks if is recompare.
   * 
   * @return the recompare
   */
  public boolean isRecompare() {
    return recompare;
  }

  /**
   * Sets the recompare.
   * 
   * @param recompare
   *          the recompare to set
   */
  public void setRecompare(boolean recompare) {
    this.recompare = recompare;
  }

  /**
   * Checks if is consider all.
   * 
   * @return the considerAll
   */
  public boolean isConsiderAll() {
    return considerAll;
  }

  /**
   * Sets the consider all.
   * 
   * @param considerAll
   *          the considerAll to set
   */
  public void setConsiderAll(boolean considerAll) {
    this.considerAll = considerAll;
  }

  /**
   * Gets the max wct.
   * 
   * @return the maxWCT
   */
  public double getMaxWCT() {
    return maxWCT;
  }

  /**
   * Sets the max wct.
   * 
   * @param maxWCT
   *          the maxWCT to set
   */
  public void setMaxWCT(double maxWCT) {
    this.maxWCT = maxWCT;
  }

  /**
   * Gets the rep factor.
   * 
   * @return the repFactor
   */
  public int getRepFactor() {
    return repFactor;
  }

  /**
   * Sets the rep factor.
   * 
   * @param repFactor
   *          the repFactor to set
   */
  public void setRepFactor(int repFactor) {
    this.repFactor = repFactor;
  }

  /**
   * Gets the max jobs.
   * 
   * @return the maxJobs
   */
  public int getMaxJobs() {
    return maxJobs;
  }

  /**
   * Sets the max jobs.
   * 
   * @param maxJobs
   *          the maxJobs to set
   */
  public void setMaxJobs(int maxJobs) {
    this.maxJobs = maxJobs;
  }

  /**
   * Gets the algorithm factory.
   * 
   * @return the algorithmFactory
   */
  public Factory<?> getAlgorithmFactory() {
    return algorithmFactory;
  }

  /**
   * Gets the base fact class.
   * 
   * @return the baseFactClass
   */
  public Class<? extends Factory<?>> getBaseFactClass() {
    return baseFactClass;
  }

  /**
   * Gets the set of ID for all configurations that are affected by the
   * algorithmic change.
   * 
   * @return the configsWithAlgo
   */
  public Set<Long> getConfigsWithAlgo() {
    return configsWithAlgo;
  }

  /**
   * Gets the number of threads used for execution.
   * 
   * @return the number of threads
   */
  public int getNumOfThreads() {
    return numOfThreads;
  }

  /**
   * Sets the number of threads to be used for execution. Default is 1.
   * 
   * @param numOfThreads
   *          the number of threads to use
   */
  public void setNumOfThreads(int numOfThreads) {
    this.numOfThreads = numOfThreads;
  }

  /**
   * Gets the output handler.
   * 
   * @return the output handler
   */
  public IOutputACEHandler getOutputHandler() {
    return outputHandler;
  }

  /**
   * Sets the output handler.
   * 
   * @param outputHandler
   *          the new output handler
   */
  public void setOutputHandler(IOutputACEHandler outputHandler) {
    this.outputHandler = outputHandler;
  }

}