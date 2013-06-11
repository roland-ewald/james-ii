/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.math.random.generators.IRandom;

/**
 * The Class UniformDistributionSelector.
 * 
 * This is the selector for selecting one element of an uniform distributed set.
 * 
 * @author Christian Ober
 * @param <M>
 *          the type of the elements
 */
public class UniformDistributionSelector<M> implements ISelector<M> {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6319857582968240558L;

  /** The random. */
  private IRandom random = SimSystem.getRNGGenerator().getNextRNG();

  /**
   * Creates a new UniformDistributionSelector.
   */
  public UniformDistributionSelector() {
    super();
  }

  /**
   * Creates a new UniformDistributionSelector. Uses a specific seed for the
   * random number generator used in this class.
   * 
   * @param seed
   *          the seed
   */
  public UniformDistributionSelector(long seed) {
    this();
    random.setSeed(seed);
  }

  /**
   * Selects randomly one element from the given ArrayList.<br>
   * <b>Note: Due to performance-factors and other reasons of
   * implementation-issues it is needed to return a list of models by executing
   * the selection, but due to the formalism it is not allowed to use the
   * information included in the models of the list. It is not allowed to change
   * any of the information.</b>
   * 
   * @param elements
   *          The Vector, containing all "interesting" elements.
   * 
   * @return The Vector containing all results (1 result).
   */
  @Override
  public synchronized List<M> executeSelection(List<M> elements) {
    ArrayList<M> result = new ArrayList<>();

    if (elements.size() > 0) {
      if (elements.size() != 1) {
        result.add(elements.get(random.nextInt(elements.size())));
      } else {
        result.add(elements.get(0));
      }
    }
    return result;
  }
}
