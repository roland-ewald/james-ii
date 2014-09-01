/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

/**
 * Different actions to be applied to the {@link ComponentState} of a plug-in.
 * See {@link ComponentState#change(ComponentState, ComponentAction)} for
 * details.
 * 
 * @author Roland Ewald
 * 
 */
public enum ComponentAction {

  /**
   * Submitting a plug-in that is under development.
   */
  SUBMIT,

  /**
   * Finishing testing successfully, indicating that the plug-in has now been
   * tested.
   */
  FINISH_TEST,

  /**
   * Indicates that the plug-in has met the defined stability criteria and is
   * now considered to be stable.
   */
  IS_STABLE,

  /**
   * Declare a plug-in to be broken.
   */
  DECLARE_BROKEN,

  /**
   * Declare a plug-in to be fixed.
   */
  FIX,

  /**
   * Withdraw a plug-in so that it is regarded as being 'under development',
   * e.g. for optimisation or refactoring.
   */
  WITHDRAW

}
