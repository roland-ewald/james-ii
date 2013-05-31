/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.validation;

/**
 * Listener interface that is used by listeners that want to attach to an
 * {@link IValidator}.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IValidatorListener {
  /**
   * notifies the listener that a validation took place and the isValid state of
   * the validator might has changed.
   * 
   * @param src
   *          the validator object that has just performed a validation
   */
  void validated(IValidator src);
}
