/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

import org.jamesii.core.algoselect.Broken;
import org.jamesii.core.algoselect.UnderDevelopment;
import org.jamesii.core.factories.Factory;

/**
 * This enumeration should list the different states a plug-in may be in, in
 * other words: its life-cycle. It basically defines a state automaton with
 * different states. These states can be changed either manually or
 * automatically.
 * 
 * @see IPlugInData
 * 
 * 
 * @author Roland Ewald
 * 
 */
public enum ComponentState {

  /**
   * The plug-in is currently not even supposed to work (because coding is not
   * finished). This is the initial state of any plug-in. It should not be used
   * for any automatic set-up procedure, as this would be guaranteed to fail.
   */
  UNDER_DEVELOPMENT,

  /**
   * The plug-in has caused runtime error or has shown invalid behaviour.
   */
  BROKEN,

  /**
   * The plug-in is untested but coding is complete. Automatic selection should
   * avoid using any untested plug-ins, but might resort to them as a last
   * option.
   */
  UNTESTED,

  /**
   * The plug-in has been tested by standard tests (benchmark model, unit test),
   * but has not been successfully applied in different 'real-world' situations.
   */
  TESTED,

  /**
   * The plug-in has been successfully used in different real-world situations.
   * It is regarded as stable and valid.
   */
  STABLE;

  /**
   * State-transition function for the state-automaton that defines the
   * life-cycle of a plug-in.
   * 
   * @param currentState
   *          current state of the plug-in
   * @param action
   *          the action that changes the state of the plug-in
   * @return the new state of the plug-in
   */
  public static ComponentState change(ComponentState currentState,
      ComponentAction action) {

    switch (action) {
    case SUBMIT:
      if (currentState != UNDER_DEVELOPMENT) {
        throw new IllegalArgumentException(getErrMsg(currentState, action));
      }
      return UNTESTED;
    case FINISH_TEST:
      if (currentState != UNTESTED) {
        throw new IllegalArgumentException(getErrMsg(currentState, action));
      }
      return TESTED;
    case IS_STABLE:
      if (currentState != TESTED && currentState != UNTESTED) {
        throw new IllegalStateException(getErrMsg(currentState, action));
      }
      return STABLE;
    case DECLARE_BROKEN:
      return BROKEN;
    case FIX:
      return UNTESTED;
    default:
    case WITHDRAW:
      return UNDER_DEVELOPMENT;
    }
  }

  /**
   * Gets the error message.
   * 
   * @param currentState
   *          the current state
   * @param action
   *          the action
   * 
   * @return the error message
   */
  static String getErrMsg(ComponentState currentState, ComponentAction action) {
    return "Action '" + action + "' is not defined for plug-in state "
        + currentState;
  }

  /**
   * Considers factory annotations to update the state of a component.
   * 
   * @param factoryClass
   *          the factory class
   * @param originalState
   *          the original state
   * 
   * @return the component state
   */
  public static ComponentState considerAnnotations(
      Class<? extends Factory> factoryClass, ComponentState originalState) {
    if (factoryClass.getAnnotation(UnderDevelopment.class) != null) {
      return ComponentState.UNDER_DEVELOPMENT;
    }
    if (factoryClass.getAnnotation(Broken.class) != null) {
      return ComponentState.BROKEN;
    }
    return originalState;
  }

}
