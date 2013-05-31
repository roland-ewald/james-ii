/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.plugins.metadata;

import java.io.Serializable;

import org.jamesii.core.factories.Factory;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

/**
 * Simple structure to maintain all factory data that can be collected
 * automatically and dynamically at runtime.
 * 
 * @param <F>
 *          the type of the factory for which the data is collected
 * 
 * 
 * @author Roland Ewald
 */
public class FactoryRuntimeData<F extends Factory<?>> implements Serializable {
  static {
    SerialisationUtils.addDelegateForConstructor(FactoryRuntimeData.class,
        new IConstructorParameterProvider<FactoryRuntimeData<?>>() {
          @Override
          public Object[] getParameters(FactoryRuntimeData<?> oldInstance) {
            return new Object[] { oldInstance.getFactory() };
          }
        });
  }

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7138813893745005260L;

  /** The factory for which the data is collected. */
  private final Class<F> factory;

  /** The number of successful executions. */
  private int successfulExecutions = 0;

  /**
   * The state in which the component that is constructed by the factory
   * currently is in.
   */
  private ComponentState state = ComponentState.UNTESTED;

  /**
   * Instantiates a new factory runtime data.
   * 
   * @param factoryClass
   *          the factory class
   */
  public FactoryRuntimeData(Class<F> factoryClass) {
    factory = factoryClass;
    state = ComponentState.considerAnnotations(factoryClass, state);
  }

  /**
   * Change state of a factory.
   * 
   * @param action
   *          the action that affects the state
   */
  public void changeState(ComponentAction action) {
    state = ComponentState.change(state, action);
  }

  /**
   * Gets the factory.
   * 
   * @return the factory
   */
  public Class<F> getFactory() {
    return factory;
  }

  /**
   * Gets the successful executions.
   * 
   * @return the successful executions
   */
  public int getSuccessfulExecutions() {
    return successfulExecutions;
  }

  /**
   * Sets the successful executions.
   * 
   * @param successfulExecutions
   *          the new successful executions
   */
  public void setSuccessfulExecutions(int successfulExecutions) {
    this.successfulExecutions = successfulExecutions;
  }

  /**
   * Gets the state.
   * 
   * @return the state
   */
  public ComponentState getState() {
    return state;
  }

  /**
   * Sets the state.
   * 
   * @param state
   *          the new state
   */
  public void setState(ComponentState state) {
    this.state = state;
    if (state == ComponentState.BROKEN) {
      successfulExecutions = 0;
    }
  }

  /**
   * Signals successful application.
   */
  public void success() {
    successfulExecutions++;
  }

}
