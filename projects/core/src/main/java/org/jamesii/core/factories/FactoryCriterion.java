/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.factories;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * A factory criterion is used by an {@link AbstractFactory} for reducing /
 * rearranging the list of available factories.
 * 
 * This abstract class contains only one abstract method (
 * {@link #filter(List, ParameterBlock)}) which has to be implemented for this
 * purpose in descendant classes.
 * 
 * @param <F>
 *          The type of factories to be filtered
 * 
 *          TODO: Remove redundancy in criteria that only check single factories
 *          (no // lists passing is needed there)
 * 
 * @author Jan Himmelspach
 */
public abstract class FactoryCriterion<F extends Factory> {

  /**
   * Instantiates a new factory criterion.
   */
  public FactoryCriterion() {
    super();
  }

  /**
   * Filters the list of factories according to some internal mechanisms. The
   * filtered/rearranged/sorted list of factories is returned. The parameter
   * maybe null. If the parameter is not null the filter criterion can check
   * whether there is any information of interest in there or not. These
   * information can be anything from plain canonical class names to any object. <br>
   * This method should only return a sub set of the factories passed for
   * filtering, i.e., it should not add any additional factory. This is due to
   * the circumstance that most often several criteria have to be applied until
   * a selection is made, and some of the criteria might filter away problematic
   * factories. Adding another factory later on thus might lead to the situation
   * that such a problematic factory is in the list, and thus could get
   * selected, and in consequence may crash the experiment.
   * 
   * @param factories
   *          the list of factories to be filtered
   * @param parameter
   *          the parameter block for filtering, maybe null!
   * 
   * @return the list< f> of factories, the factories are of the same type as
   *         those passed for filtering (thus no different factories can be
   *         added in here)
   */
  public abstract List<F> filter(List<F> factories, ParameterBlock parameter);

}
