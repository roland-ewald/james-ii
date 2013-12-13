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
 * This stopping policy stops if *any* of the added policies decides to stop
 * (logical OR).
 * 
 * @see IComputationTaskStopPolicy
 * @see CompositeCompTaskStopPolicy
 * 
 * @author Roland Ewald
 * @author Arne Bittig
 */
public class DisjunctiveSimRunStopPolicy<T extends IComputationTask> extends
    CompositeCompTaskStopPolicy<T> {

  /**
   * Instantiates a new disjunctive sim run stop policy.
   * 
   * @param run
   *          the simulation run
   */
  public DisjunctiveSimRunStopPolicy(ISimulationRun run) {
    super(run);
  }

  /**
   * Instantiates a new disjunctive sim run stop policy.
   * 
   * @param run
   *          the run
   * @param policies
   *          the policies
   */
  public DisjunctiveSimRunStopPolicy(ISimulationRun run,
      List<IComputationTaskStopPolicy<T>> policies) {
    super(run, policies);
  }

  private IComputationTaskStopPolicy<T> firstTrue = null;

  @Override
  public boolean hasReachedEnd(T t) {

    if (getStopPolicies().size() == 0) {
      return true;
    }

    for (IComputationTaskStopPolicy<T> compTaskStopPolicy : getStopPolicies()) {
      if (compTaskStopPolicy.hasReachedEnd(t)) {
        firstTrue = compTaskStopPolicy;
        return true;
      }
    }
    return false;
  }

  @Override
  public IComputationTaskStopPolicy<T> getLastRelevantStopPolicy() {
    return firstTrue;
  }

}
