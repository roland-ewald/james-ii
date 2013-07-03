/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.base.Entity;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Filters all factories that implement {@link IParameterFilterFactory}
 * according to whether they support the given parameters or not.
 * 
 * @param <F>
 *          the type of the factory, has to extend the base Factory and to
 *          implement the {@link IParameterFilterFactory} interface.
 * @author Roland Ewald
 */
public class ParameterFilterCriteria<F extends Factory & IParameterFilterFactory>
    extends FactoryCriterion<F> {

  /** Flag that indicates whether the factories ought to be sorted. */
  private final boolean applySort;

  /** Minimal index that is required for this particular task. */
  private final int minSupportIndex;

  /**
   * Lazy constructor. All supporting factories are allowed, list will be
   * sorted.
   */
  public ParameterFilterCriteria() {
    this(true, 0);
  }

  /**
   * Default constructor.
   * 
   * @param sort
   *          the flag to switch sorting on and off
   * @param minSuppIndex
   *          the minimal required support index
   */
  public ParameterFilterCriteria(boolean sort, int minSuppIndex) {
    applySort = sort;
    minSupportIndex = minSuppIndex;
  }

  @Override
  public List<F> filter(List<F> factories, final ParameterBlock parameter) {

    List<F> filteredFactories = new ArrayList<>();
    for (F factory : factories) {
      try {
        if (factory.supportsParameters(parameter) > minSupportIndex) {
          filteredFactories.add(factory);
        }
      } catch (Exception e) {
        SimSystem.report(Level.WARNING, "A factory caused a problem on determining whether it supports a set of parameters or not: "
        + factory.getClass()
        + ". The problem was of type "
        + e.getClass().getName()
        + " with message "
        + e.getMessage());
      }
    }

    if (applySort) {
      Collections.sort(filteredFactories, new ParameterFilterFactoryComparator(
          parameter));
    }

    return filteredFactories;
  }
}

/**
 * Comparator for factories regarding their support-index.
 * 
 * @author Roland Ewald
 */
class ParameterFilterFactoryComparator implements
    Comparator<IParameterFilterFactory> {

  /** Reference to parameters that should be used for sorting. */
  private final ParameterBlock parameters;

  /**
   * Default constructor.
   * 
   * @param params
   *          parameters to be used for sorting
   */
  public ParameterFilterFactoryComparator(ParameterBlock params) {
    parameters = params;
  }

  @Override
  public int compare(IParameterFilterFactory factory1,
      IParameterFilterFactory factory2) {
    Integer support1 = factory1.supportsParameters(parameters);
    Integer support2 = factory2.supportsParameters(parameters);

    // Applies reverse order (factories with best (highest) support first)
    return support2.compareTo(support1);
  }

}
