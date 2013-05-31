/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.List;

/**
 * Factory for creating an instance of a {@link FactoryCriterion} for filtering.
 * 
 * @author Jan Himmelspach
 */
public abstract class FactoryCriterionFactory extends Factory {

  /** Serialisation ID. */
  private static final long serialVersionUID = -8597244896736587726L;

  /**
   * Create and return an instance of the criteria for use in an
   * AbstractFactory.
   * 
   * @return instance of the criteria
   */
  public abstract FactoryCriterion<?> getCriteria();

  /**
   * The list of abstract factories this criteria shall be used for filtering.
   * 
   * @return list of supported AbstractFactories
   */
  public abstract List<Class<? extends AbstractFactory<?>>> getFactories();

}
