/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.comparing;

import java.util.Map;
import java.util.WeakHashMap;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.factories.Context;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.comparing.EqualityObserver.InformationExtractor;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterBlocks;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.id.IUniqueID;

/**
 * Stop policy that stops the computation if the relevant
 * {@link EqualityObserver} finds a difference, i.e. if the sequence of calls to
 * {@link IObservable#changed(Object)} by two relevant observables diverged for
 * the first time.
 *
 * Note that there is no stopping here if the two observables produce an equal
 * sequence of states & hints, so the returned stop policy is best be used in a
 * {@link org.jamesii.core.simulationrun.stoppolicy.DisjunctiveSimRunStopPolicy}
 *
 * @author Arne Bittig
 * @date 22.09.2012
 */
public class ComparingObserverStopPolicyFactory extends
    ComputationTaskStopPolicyFactory {

  private static final long serialVersionUID = -7125584538876232803L;

  /** The parameter block setting "check runs for equal behavior" id */
  public static final String COMPARE_EXP_OR_CONFIGS = "CompareRunsOrConfigs";

  /** The parameter block setting "Information extraction method to use" id */
  public static final String INFO_EXTRACTOR = "InformationExtractor";

  private static final Map<IUniqueID, IComputationTaskStopPolicy> STOP_POLICIES =
      new WeakHashMap<>();

  /**
   * Create or get previously created stop policy. Creates associated
   * {@link EqualityObserver} if not yet created.
   * 
   * @param id
   *          Identifier associated with entities/observables to compare
   * @param infoExtractor
   *          Information extraction method for observables with given id
   * @return Stop policy based on the {@link EqualityObserver} for given id
   */
  public static synchronized IComputationTaskStopPolicy getStopPolicyFor(
      final IUniqueID id,
      final InformationExtractor<? extends IObservable, ?> infoExtractor) {
    IComputationTaskStopPolicy stopPol = STOP_POLICIES.get(id);
    if (stopPol != null) {
      return stopPol;
    }
    stopPol = new IComputationTaskStopPolicy() {

      private final EqualityObserver<?> obs = EqualityObserver.getInstanceFor(
          id, infoExtractor);

      @Override
      public boolean hasReachedEnd(IComputationTask t) {
        return obs.getFirstDifferenceIndex() >= 0;
      }
    };
    STOP_POLICIES.put(id, stopPol);
    return stopPol;
  }

  @Override
  public IComputationTaskStopPolicy create(ParameterBlock params, Context context) {
    ISimulationRun run =
        ParameterBlocks.getSubBlockValue(params,
            ComputationTaskStopPolicyFactory.COMPTASK);
    Boolean compare = params.getSubBlockValue(COMPARE_EXP_OR_CONFIGS);
    IUniqueID id =
        compare ? run.getConfig().getExperimentID() : run.getConfig()
            .getConfigurationID();
    InformationExtractor<? extends IObservable, ?> infoEx =
        params.getSubBlockValue(INFO_EXTRACTOR);
    return getStopPolicyFor(id, infoEx);
  }
}