/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.validation;

import java.util.Collection;

import org.jamesii.gui.utils.ListenerSupport;

/**
 * Use this class for extension rather than the {@link IValidator} interface
 * since it already implements basic functionality of that interface.
 * 
 * @author Stefan Rybacki
 * 
 */
public abstract class AbstractValidator implements IValidator {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2326109881931810052L;

  /**
   * list of listeners
   */
  private transient ListenerSupport<IValidatorListener> listeners =
      new ListenerSupport<>();

  @Override
  public final synchronized void addValidatorListener(
      IValidatorListener listener) {
    listeners.addListener(listener);
  }

  @Override
  public final synchronized void removeValidatorListener(
      IValidatorListener listener) {
    listeners.removeListener(listener);
  }

  /**
   * use this to retrieve the attached listeners in case you need to implement a
   * custom notification routine.
   * 
   * @return a list of listeners
   */
  protected final Collection<IValidatorListener> getListeners() {
    return listeners.getListeners();
  }

  /**
   * use this method in sub classes to notify attached listeners
   */
  protected void fireValidityChanged() {
    for (IValidatorListener l : listeners.getListeners()) {
      if (l != null) {
        l.validated(this);
      }
    }
  }
}
