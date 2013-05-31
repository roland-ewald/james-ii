/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.validation;

import java.io.Serializable;

/**
 * This interface is used to provide basic functionality for validators. This
 * interface should not be sub classed instead use {@link AbstractValidator} and
 * extend that one.
 * 
 * 
 * @author Stefan Rybacki
 * 
 */
public interface IValidator extends Serializable {
  /**
   * Indicates whether whatever the validator validates is valide
   * 
   * @return true if content is valid false else
   */
  boolean isValid();

  /**
   * adds a validator listener to the validator, so whenever isValid state
   * changes the listener should be notified
   * 
   * @param listener
   *          the listener to attach
   */
  void addValidatorListener(IValidatorListener listener);

  /**
   * removes a validator listener from teh validator
   * 
   * @param listener
   *          the listener to remove
   */
  void removeValidatorListener(IValidatorListener listener);
}
