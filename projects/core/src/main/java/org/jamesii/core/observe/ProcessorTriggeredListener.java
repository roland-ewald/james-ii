/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.observe.listener.IObserverListener;

/**
 * A view that always depends on the progress of simulation. It is triggered by
 * the advancement of simulation time and can manage multiple other observers.
 * 
 * $Date: 2006-12-31 11:17:03 +0100 (So, 31 Dez 2006) $.
 * 
 * $Rev: 3867 $.
 * 
 */
public abstract class ProcessorTriggeredListener extends Observer<IObservable>
    implements IObserverListener {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7340406823130045627L;

  /** Reference to main observer, which triggers the view. */
  private ProcessorStateObserver<?> mainObserver;

  /** List of observers to be managed. */
  private volatile List<IObserver<IObservable>> observerList =
      new ArrayList<>();

  /**
   * Default constructor.
   */
  public ProcessorTriggeredListener() {
    super();
  }

  /**
   * Default constructor.
   * 
   * @param mainObserver
   *          the observer which triggers
   */
  public ProcessorTriggeredListener(ProcessorStateObserver<?> mainObserver) {
    registerMainObserver(mainObserver);
  }

  /**
   * Adds observer.
   * 
   * @param observer
   *          the observer
   */
  public void addObserver(IObserver observer) {
    observerList.add(observer);
  }

  /**
   * Re-draws the view.
   */
  public abstract void draw();

  /**
   * Get main observer.
   * 
   * @return main observer
   */
  protected ProcessorStateObserver<?> getMainObserver() {
    return mainObserver;
  }

  /**
   * Get list of observers this view listens to.
   * 
   * @return list of notifying observers
   */
  public List<IObserver<IObservable>> getObserverList() {
    return observerList;
  }

  /**
   * Register main observer.
   * 
   * @param observer
   *          the observer
   */
  final void registerMainObserver(ProcessorStateObserver<?> observer) {
    this.mainObserver = observer;
    mainObserver.addListener(this);
  }

  /**
   * Removes observer.
   * 
   * @param observer
   *          the observer
   */
  public void removeObserver(IObserver<IEntity> observer) {
    observerList.remove(observer);
  }

  @Override
  public void init(ComputationTaskRuntimeInformation srti) {
    mainObserver = null;
  }

  /**
   * Re-draws the view if main observer sends notification.
   * 
   * @param observer
   *          main observer
   */
  @Override
  public void updateOccurred(INotifyingObserver observer) {

    if (mainObserver == null && observer instanceof ProcessorStateObserver) {
      registerMainObserver((ProcessorStateObserver<?>) observer);
    }

    if (mainObserver != null) {
      draw();
    }
  }

}
