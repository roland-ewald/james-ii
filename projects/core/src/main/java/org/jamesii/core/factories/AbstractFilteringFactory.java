/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

/**
 * Super class for all abstract factories that employ the generic parameter
 * filtering mechanism.
 * 
 * @author Roland Ewald
 * @param <F>
 */
public abstract class AbstractFilteringFactory<F extends Factory<?> & IParameterFilterFactory>
    extends AbstractFactory<F> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 5098039358731476307L;

  /**
   * Instantiates a new abstract filtering factory.
   */
  public AbstractFilteringFactory() {
    super();
    addCriteria(new ParameterFilterCriteria<F>());
  }

}
