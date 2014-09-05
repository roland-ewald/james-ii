/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.misc.Pair;

/**
 * Super class for all factories for {@link CompositeCompTaskStopPolicy}
 * sub-classes. These rely on additional stop policies.
 * 
 * @author Roland Ewald
 * @author Arne Bittig
 */
public abstract class CompositeCompTaskStopPolicyFactory extends
    ComputationTaskStopPolicyFactory<IComputationTask> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -4893322013816570490L;

  /**
   * Parameter name for passing over a list of pairs (policy factory,
   * parameters) to a sub-class.
   */
  public static final String POLICY_FACTORY_LIST = "policyFactoryList";

  /**
   * Creates a list of sub-policies by considering the factories and parameter
   * blocks in {@link CompositeCompTaskStopPolicyFactory#POLICY_FACTORY_LIST}.
   * 
   * @param parameters
   *          the parameters
   * 
   * @return the list of simulation run stop policies
   */
  protected static List<IComputationTaskStopPolicy<IComputationTask>> createSubPolicies(
      ParameterBlock parameters) {

    ISimulationRun simRun =
        ParameterBlocks.getSubBlockValue(parameters, COMPTASK);
    List<IComputationTaskStopPolicy<IComputationTask>> policies =
        new ArrayList<>();
    List<Pair<ComputationTaskStopPolicyFactory<IComputationTask>, ParameterBlock>> policyFactories =
        ParameterBlocks.getSubBlockValue(parameters, POLICY_FACTORY_LIST);

    if (policyFactories == null) {
      return policies;
    }

    for (Pair<ComputationTaskStopPolicyFactory<IComputationTask>, ParameterBlock> policyFactorySetup : policyFactories) {
      createAndAddPolicy(policies, policyFactorySetup, simRun);
    }

    return policies;
  }

  /**
   * Creates a new policy and adds it to the list.
   * 
   * @param policies
   *          the policies
   * @param policyFactorySetup
   *          the policy factory setup
   * @param simRun
   *          the simulation run
   */
  protected static void createAndAddPolicy(
      List<IComputationTaskStopPolicy<IComputationTask>> policies,
      Pair<ComputationTaskStopPolicyFactory<IComputationTask>, ParameterBlock> policyFactorySetup,
      ISimulationRun simRun) {
    ParameterBlock policyParams = policyFactorySetup.getSecondValue().getCopy();
    policyParams.addSubBlock(COMPTASK, simRun);
    policies.add(policyFactorySetup.getFirstValue().create(policyParams, SimSystem.getRegistry().createContext()));
  }

  /**
   * Convenience method for iterated conversion from a
   * {@link ParameterizedFactory} to the ugly (and legacy?) {@link Pair}
   * accepted by {@link CompositeCompTaskStopPolicyFactory} and subclasses (in
   * the {@link #POLICY_FACTORY_LIST} parameter block)
   * 
   * @param listParFac
   *          Collection of {@link ParameterizedFactory parameterized factories}
   * @return List of {@link Pair}s of factory and parameter
   */
  public static <F extends org.jamesii.core.factories.Factory<?>> List<Pair<F, ParameterBlock>> listOfParFacsToListOfPairs(
      Collection<ParameterizedFactory<F>> listParFac) {
    List<Pair<F, ParameterBlock>> rv = new ArrayList<>(listParFac.size());
    for (ParameterizedFactory<F> parFac : listParFac) {
      rv.add(new Pair<>(parFac.getFactory(), parFac.getParameter()));
    }
    return rv;
  }

}
