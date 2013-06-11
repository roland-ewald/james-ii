/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.instrumentation.computation.generic;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.processor.IProcessor;

/**
 * A generic computation instrumenter. This may be helpful for testing or
 * debugging purposes where a simple observer needs to be attached and the
 * overhead of specifying a custom instrumenter etc. is unnecessary.
 * 
 * The observer is attached to the {@link IProcessor} of a computation task. It
 * may be registered at multiple processors. After each new registration, this
 * instrumenter calls {@link IObserver#update(IObservable, Object)} with a
 * {@link ProcessorObservationHint} that contains the reference to the
 * instrumenter. This should allow the observer to deal with parallel
 * simulations etc. on its own, if this is necessary.
 * 
 * @see ProcessorObservationHint
 * @see IObserver
 * 
 * @author Roland Ewald
 */
public class GenericComputationInstrumenter implements IComputationInstrumenter {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8874816134879997288L;

  /** The observer to use. */
  private final IObserver<?> observerToUse;

  /** The target at which the observer shall be registered. */
  private final GenericInstrumentationTarget target;

  /** List containing the observer to use. */
  private final List<IObserver<?>> instantiatedObservers = new ArrayList<>();

  /**
   * Instantiates a new generic computation instrumenter.
   * 
   * @param observerToUse
   *          the observer to use
   */
  public GenericComputationInstrumenter(IObserver<?> observerToUse,
      GenericInstrumentationTarget target) {
    this.observerToUse = observerToUse;
    this.target = target;
    instantiatedObservers.add(observerToUse);
  }

  @Override
  public List<? extends IObserver<? extends IObservable>> getInstantiatedObservers() {
    return instantiatedObservers;
  }

  @Override
  @SuppressWarnings("unchecked")
  // Has to be ensured by observer to use, otherwise exception should be thrown
  public void instrumentComputation(IComputationTask computation) {
    IEntity target = getObservationTarget(computation);
    Mediator.create(target);
    target.registerObserver(observerToUse);
    try {
      ((IObserver<IEntity>) observerToUse).update(target,
          new ProcessorObservationHint(this));
    } catch (ClassCastException ex) {
      throw new IllegalArgumentException(
          "Observer needs to be of type IObserver<IEntity>.", ex);
    }
  }

  /**
   * Gets the observation target.
   * 
   * @param computation
   *          the computation
   * @return the observation target
   */
  private IEntity getObservationTarget(IComputationTask computation) {
    switch (target) {
    case PROCESSOR:
      return computation.getProcessorInfo().getLocal();
    case PROCESSOR_STATE:
      return computation.getProcessorInfo().getLocal().getState();
    case MODEL:
      return computation.getProcessorInfo().getLocal().getModel();
    default:
      throw new IllegalArgumentException("Target '" + target
          + "' is not supported.");
    }
  }
}
