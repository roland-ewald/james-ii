package org.jamesii.gui.application.action;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;

/**
 * The Class WeakPropertyChangeListenerProxy. Wraps a given
 * {@link PropertyChangeListener} using {@link WeakReference} for later use in
 * listener lists where the listener can't be removed after usage.
 */
class WeakPropertyChangeListenerProxy implements PropertyChangeListener {

  /**
   * The weak reference to the actual {@link PropertyChangeListener}.
   */
  private WeakReference<PropertyChangeListener> ref;

  /**
   * Instantiates a new weak property change listener proxy.
   * 
   * @param reference
   *          the reference to wrap
   */
  public WeakPropertyChangeListenerProxy(PropertyChangeListener reference) {
    ref = new WeakReference<>(reference);
  }

  @Override
  public void propertyChange(PropertyChangeEvent evt) {
    if (ref != null) {
      PropertyChangeListener a = ref.get();
      if (a != null) {
        a.propertyChange(evt);
      } else {
        ref = null;
      }
    }
  }

}
