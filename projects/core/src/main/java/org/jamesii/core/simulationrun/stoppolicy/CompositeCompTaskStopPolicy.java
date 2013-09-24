/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.AbstractComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;

/**
 * Super class for simulation run stop policies that rely on other policies.
 * 
 * @see IComputationTaskStopPolicy
 * 
 * @author Roland Ewald
 * @author Arne Bittig
 * 
 */
public abstract class CompositeCompTaskStopPolicy
    extends
    AbstractComputationTaskStopPolicy implements
    ISimulationRunStopPolicySimTime {

  /** The computation task stop policies. */
  private final List<IComputationTaskStopPolicy> compTaskStopPolicies;

  /**
   * Instantiates a new composite stop policy.
   * 
   * @param run
   *          the simulation run
   */
  public CompositeCompTaskStopPolicy(IComputationTask run) {
    this(run, new ArrayList<IComputationTaskStopPolicy>());
  }

  /**
   * Instantiates a new composite stop policy.
   * 
   * @param run
   *          the computation task
   * @param policies
   *          the policies to be queried
   */
  public CompositeCompTaskStopPolicy(IComputationTask run,
      List<IComputationTaskStopPolicy> policies) {
    super();
    this.compTaskStopPolicies = policies;
  }

  /**
   * Adds the passed computation task stop policy.
   * 
   * @param policy
   *          the policy
   */
  public void addSimRunStopPolicy(IComputationTaskStopPolicy policy) {
    getStopPolicies().add(policy);
  }

  /**
   * Removes the computation task stop policy.
   * 
   * @param policy
   *          the policy
   */
  public void removeSimRunStopPolicy(IComputationTaskStopPolicy policy) {
    getStopPolicies().remove(policy);
  }

  @Override
  public Comparable getEstimatedEndTime() {
    for (IComputationTaskStopPolicy runStopPolicy : getStopPolicies()) {
      if (runStopPolicy instanceof ISimulationRunStopPolicySimTime) {
        return ((ISimulationRunStopPolicySimTime) runStopPolicy)
            .getEstimatedEndTime();
      }
    }
    return Double.POSITIVE_INFINITY;
  }

  /**
   * Get the other policy whose {@link #hasReachedEnd()} result was most
   * relevant for the last return value of {@link #hasReachedEnd()} of the
   * composite policy (<code>null</code> if none; an arbitrarily selected one if
   * more than one relevant). For example, for several policies linked by OR
   * this shall be the one that returned <code>true</code> and thus made the
   * composite policy return <code>true</code>, too (if several returned
   * <code>true</code> at the same time, any of them). For policies linked by
   * AND, the most relevant policy could be the last whose
   * {@link #hasReachedEnd()} result switched from <code>false</code> to
   * <code>true</code>.
   * 
   * @return after {@link #hasReachedEnd()} returned true, stop policy whose
   *         changing {@link #hasReachedEnd()} result triggered the former
   */
  public abstract IComputationTaskStopPolicy getLastRelevantStopPolicy();

  /**
   * @return Copy of the list of stop policies used
   */
  public List<IComputationTaskStopPolicy> getSimRunStopPolicies() {
    return new ArrayList<>(getStopPolicies());
  }

  /**
   * @param compTaskStopPolicies
   *          Stop policies to use (replaces already defined ones)
   */
  protected void setSimRunStopPolicies(
      List<IComputationTaskStopPolicy> compTaskStopPolicies) {
    this.getStopPolicies().clear();
    this.getStopPolicies().addAll(compTaskStopPolicies);
  }

  /**
   * @return List of stop policies used (not copied, unlike with
   *         {@link #getSimRunStopPolicies()})
   */
  protected List<IComputationTaskStopPolicy> getStopPolicies() {
    return compTaskStopPolicies;
  }

  @Override
  public String toString() {
    return this.getClass().getSimpleName() + compTaskStopPolicies;
  }
}
