/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe;

import org.jamesii.core.observe.listener.IObserverListener;
import org.jamesii.gui.utils.ListenerSupport;

/**
 * This is basically an extended version of the observer class that also
 * implements a notification mechanism for listeners and implements all other
 * methods defined in {@link INotifyingObserver}.
 * 
 * @author Roland Ewald
 * @param <E>
 *          Type of entity to observe
 */
public abstract class NotifyingObserver<E extends IObservable> extends
    Observer<E> implements INotifyingObserver<E> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -7139804202293012826L;

  /**
   * List of listeners to this GUI observer.
   */
  private ListenerSupport<IObserverListener> observerListeners =
      new ListenerSupport<>();

  @Override
  public void addListener(IObserverListener listener) {
    synchronized (observerListeners) {
      observerListeners.add(listener);
    }
  }

  /**
   * Function to be implemented by all subclasses. Replaces
   * {@link IObserver#update(IObservable)}.
   * 
   * @param entity
   *          entity that was updated
   */
  public abstract void handleUpdate(E entity);

  /**
   * Method called by {@link #update(IObservable)}, telling it whether listeners
   * need to be notified
   * 
   * @return true by default (to be overridden in subclasses...)
   */
  @SuppressWarnings("static-method")
  protected boolean executeNotification() {
    return true;
  }

  /**
   * This method is called when information has to be passed to the observer
   * listeners.
   */
  protected void notifyListeners() {
    for (IObserverListener listener : observerListeners) {
      listener.updateOccurred(this);
    }
  }

  @Override
  public void removeListener(IObserverListener listener) {
    observerListeners.remove(listener);
  }

  @Override
  public final void update(E entity) {
    handleUpdate(entity);
    if (executeNotification()) {
      notifyListeners();
    }
  }
}
