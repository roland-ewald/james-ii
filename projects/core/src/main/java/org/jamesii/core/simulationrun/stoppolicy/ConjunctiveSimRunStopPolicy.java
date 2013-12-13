/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.util.List;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * This stopping policy stops if *all* of the added policies decide to stop
 * (logical AND).
 * 
 * @see IComputationTaskStopPolicy
 * @see CompositeCompTaskStopPolicy
 * 
 * @author Roland Ewald
 * @author Arne Bittig
 */
public class ConjunctiveSimRunStopPolicy<T extends IComputationTask> extends
    CompositeCompTaskStopPolicy<T> {

  /**
   * Instantiates a new conjunctive sim run stop policy.
   * 
   * @param run
   *          the run
   */
  public ConjunctiveSimRunStopPolicy(ISimulationRun run) {
    super(run);
  }

  /**
   * Instantiates a new conjunctive sim run stop policy.
   * 
   * @param run
   *          the simulation run
   * @param policies
   *          the stopping policies
   */
  public ConjunctiveSimRunStopPolicy(ISimulationRun run,
      List<IComputationTaskStopPolicy<T>> policies) {
    super(run, policies);
  }

  private IComputationTaskStopPolicy<T> lastFalse = null;

  @Override
  public boolean hasReachedEnd(T t) {
    for (IComputationTaskStopPolicy<T> compTaskStopPolicy : getStopPolicies()) {
      if (!compTaskStopPolicy.hasReachedEnd(t)) {
        lastFalse = compTaskStopPolicy;
        return false;
      }
    }
    return true;
  }

  @Override
  public IComputationTaskStopPolicy<T> getLastRelevantStopPolicy() {
    return lastFalse;
  }

}
