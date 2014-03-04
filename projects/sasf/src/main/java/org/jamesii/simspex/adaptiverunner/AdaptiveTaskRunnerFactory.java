/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.adaptiverunner;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.experiments.taskrunner.parallel.ParallelComputationTaskRunnerFactory;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.simspex.adaptiverunner.policies.IntEstimDecFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.AbstractMinBanditPolicyFactory;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy;
import org.jamesii.simspex.adaptiverunner.policies.plugintype.MinBanditPolicyFactory;

/**
 * Factory for the adaptive computation task runner.
 * 
 * @author Roland Ewald
 * 
 */
public class AdaptiveTaskRunnerFactory extends
    ParallelComputationTaskRunnerFactory {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4060394987049060486L;

  /**
   * List of observers for the policy. Type: {@link List}.
   */
  public static final String POLICY_OBSERVERS = "PolicyObservers";

  /**
   * List of factory names that should be ignored. Type: {@link List}.
   */
  public static final String BLACK_LIST = "BlackList";

  /**
   * List of {@link ParameterBlock} that defines the algorithm portfolio that
   * should be considered. Type: {@link List}.
   */
  public static final String PORTFOLIO = "Portfolio";

  /**
   * The
   * {@link org.jamesii.simspex.adaptiverunner.policies.plugintype.IMinBanditPolicy}
   * that shall be used.
   */
  public static final String POLICY = "MinBanditPolicy";

  @Override
  public ITaskRunner create(ParameterBlock params) {
    AdaptiveComputationTaskRunner asr = null;
    try {
      ParameterBlock policyParameters =
          ParameterBlocks.getSBOrDefault(params, POLICY,
              IntEstimDecFactory.class.getName());
      MinBanditPolicyFactory mbFac =
          SimSystem.getRegistry().getFactory(
              AbstractMinBanditPolicyFactory.class, policyParameters);
      SimSystem.report(Level.INFO,
          "Adaptive task runner uses: " + mbFac.getName());
      asr =
          new AdaptiveComputationTaskRunner(params.getSubBlockValue(
              ParallelComputationTaskRunnerFactory.NUM_CORES, -1), mbFac,
              policyParameters);
      asr.setPolicyObservers(params.getSubBlockValue(POLICY_OBSERVERS,
          new ArrayList<IObserver<? extends IMinBanditPolicy>>()));
      asr.setBlackList(params.getSubBlockValue(BLACK_LIST,
          new ArrayList<String>()));
      asr.setPredefinedConfigurations(params.getSubBlockValue(PORTFOLIO,
          (List<ParameterBlock>) null));
    } catch (RemoteException e) {
      SimSystem
          .report(Level.SEVERE, "Could not create adaptive task runner", e);
    }
    return asr;
  }

}
