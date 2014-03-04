/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import junit.framework.TestCase;

import org.jamesii.asf.integrationtest.bogus.application.model.BogusModel;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.experiments.taskrunner.plugintype.TaskRunnerFactory;
import org.jamesii.core.experiments.variables.ExperimentVariable;
import org.jamesii.core.experiments.variables.modifier.IncrementModifierInteger;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;
import org.jamesii.simspex.util.SelTreeSetCreation;

/**
 * Class to test the performance of an {@link IMinBanditPolicy}
 * 
 * @author Roland Ewald
 * 
 */
public abstract class TestPolicyPerformance extends TestCase {

  /**
   * The options (parameter blocks) to be used. If non-existent, all parameter
   * blocks will be used.
   */
  final static String OPTION_FILE = "./options.xml";

  /** The overall number of model setups. */
  final static int MODEL_SETUPS = 25;

  /**
   * Gets the policy setup.
   * 
   * @return a pair (factory, parameters)
   */
  protected abstract Pair<MinBanditPolicyFactory, ParameterBlock> getPolicySetup();

  /**
   * Tests the policy.
   */
  public void testPolicy() throws Exception {
    Pair<MinBanditPolicyFactory, ParameterBlock> setup = getPolicySetup();
    MinBanditPolicyFactory polFactory = setup.getFirstValue();
    SimplePolicyObserver polObserver = new SimplePolicyObserver();

    testSampleModel(polFactory, setup.getSecondValue(), getModelLocation(), 30,
        0.00001, polObserver);
  }

  /**
   * Gets the model location.
   * 
   * @return the model location
   */
  protected String getModelLocation() {
    return "java://" + BogusModel.class.getName();
  }

  /**
   * Use the policy factory and the parameters with which it shall be
   * initialised to create a replication experiment and execute it.
   * 
   * @param factory
   * @param policyParams
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testSampleModel(MinBanditPolicyFactory factory,
      ParameterBlock policyParams, String modelLocation, int replications,
      double stopTime, SimplePolicyObserver policyObserver) throws Exception {

    // Initialising black list, observers
    List<IObserver<? extends IMinBanditPolicy>> observers = new ArrayList<>();
    observers.add(policyObserver);
    List<String> blackList = new ArrayList<>();
    blackList.add("SomeFactory");

    // Configure experiment with given model, policy, etc.
    BaseExperiment experiment = new BaseExperiment();
    experiment.setModelLocation(new URI(modelLocation));
    experiment.setBackupEnabled(false);
    experiment.setRepeatRuns(replications);
    experiment.setDefaultSimStopTime(stopTime);

    // Define experiment
    List<ExperimentVariable<?>> expModelVars =
        new ArrayList<>();
    expModelVars.add(new ExperimentVariable<>("someVariable",
        new IncrementModifierInteger(3, 1, 5)));
    expModelVars.add(new ExperimentVariable<>("anotherVariable",
        new IncrementModifierInteger(1, 1, 2)));
    experiment.setupVariables(expModelVars);

    // Get all possible options
    File optionFile = new File(OPTION_FILE);
    List<ParameterBlock> options = null;
    if (optionFile.exists()) {
      options = (List<ParameterBlock>) Files.load(OPTION_FILE);
    } else {
      options = createParameterBlocks(modelLocation, blackList);
      Files.save(options, OPTION_FILE);
    }

    // Set parameters of runner
    policyParams.setValue(factory.getClass().getName());
    ParameterBlock runnerParams = new ParameterBlock();
    runnerParams
        .addSubBl(AdaptiveTaskRunnerFactory.POLICY_OBSERVERS, observers)
        .addSubBl(AdaptiveTaskRunnerFactory.POLICY, policyParams)
        .addSubBl(AdaptiveTaskRunnerFactory.BLACK_LIST, blackList)
        .addSubBl(AdaptiveTaskRunnerFactory.PORTFOLIO, options)
        .addSubBl(ParallelComputationTaskRunnerFactory.NUM_CORES, 4);

    experiment
        .setTaskRunnerFactory(new ParameterizedFactory<TaskRunnerFactory>(
            new AdaptiveTaskRunnerFactory(), runnerParams));

    // Execute experiment
    experiment.execute();

    // Analyse and reset observer
    policyObserver.reset();
  }

  /**
   * Creates all eligible options.
   * 
   * @param modelLocation
   *          location of the model to be simulated
   * @param blackList
   *          ignore list of factories names (or parts thereof)
   * @return list of eligible options
   */
  protected List<ParameterBlock> createParameterBlocks(String modelLocation,
      List<String> blackList) {
    SelectionTreeSet treeSet = null;
    try {
      treeSet =
          SelTreeSetCreation.createSelectionTreeSet(new ParameterBlock(),
              new URI(modelLocation), new HashMap<String, Object>());
    } catch (Exception ex) {
      ex.printStackTrace();
    }
    treeSet.generateFactoryCombinations(blackList);
    List<ParameterBlock> options = treeSet.getFactoryCombinations();
    return options;
  }

  /**
   * Checks whether the policy observer could observe the correct number of
   * selections, reward returns and quarantine actions.
   * 
   * @param policyObserver
   *          the policy observer
   * @param replications
   *          the number of intended replications
   */
  protected void checkSelectionResults(SimplePolicyObserver policyObserver,
      int replications) {
    List<Integer> selHistory = policyObserver.getSelectionHistory();
    assertEquals(replications * MODEL_SETUPS, selHistory.size());

    // Tricky: stochastic policies might not always identify all options that
    // need to be quarantined - so a heuristic bound is used instead...
    IMinBanditPolicy policy = policyObserver.getPolicy();
    List<Pair<Integer, Double>> rewardHistory =
        policyObserver.getRewardHistory();
    assertTrue(replications * MODEL_SETUPS >= rewardHistory.size()
        + policy.getQuarantinedOptions().size() * MODEL_SETUPS);

    assertTrue(replications * MODEL_SETUPS - 5 <= rewardHistory.size()
        + policy.getQuarantinedOptions().size() * MODEL_SETUPS);
  }

}
