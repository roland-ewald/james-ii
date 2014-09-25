/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import java.util.List;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * Rule base implementation for {@link CARulesModel}. It is able to determine
 * the next state of a cell by providing {@link CARule}s a random number
 * generator and the neighbors plus the current cell itself. <br/>
 * Please note: if this class is used by an algorithm making use of multiple
 * threads the rng needs to be wrapped to make sure that
 * <ul>
 * <li>The same random numbers are used for the same cells independent from the
 * algorithm used</li>
 * <li>The rng used is thread safe!</li>
 * </ul>
 * The default implementations don't take care about this!
 * 
 * @author Stefan Rybacki
 */
class CARuleBase implements ICARuleBase {
  /**
   * The rules.
   */
  private final List<CARule> rules;

  /**
   * The random number generator to use for probabilistic rules.
   */
  private IRandom random;

  /**
   * The flag indicating whether the rule base is reactive.
   */
  private boolean reactive = true;

  /** The flag whether to use torus. */
  private boolean isTorus;

  /** The neighborhood. */
  private INeighborhood neighborhood;

  /**
   * Instantiates a new CA rule base.
   * 
   * @param rules
   *          the rules
   * @param hood
   *          the neighborhood to use
   * @param torus
   *          flag whether to use torus grid
   * @param rnd
   *          the random number generator
   */
  public CARuleBase(List<CARule> rules, INeighborhood hood, boolean torus,
      IRandom rnd) {
    this.rules = rules;
    this.random = rnd;
    this.neighborhood = hood;
    this.isTorus = torus;

    // check for non reactive rules
    for (CARule rule : rules) {
      reactive = reactive && Double.compare(rule.getProbability(), 1.0) == 0;
    }
  }

  @Override
  public int getNextState(int current, INeighborStates<Integer> neighbors) {
    // first matching rule is used if probability also agrees
    for (CARule rule : rules) {
      if (rule != null && rule.getCurrentCondition().isTrue(current, neighbors)
          && rule.getPreCondition().isTrue(current, neighbors)) {
        // if pre conditions and current state condition match then check for
        // probability for this rule

        if ((!rule.hasProbability())
            || (Double.compare(random.nextDouble(), rule.getProbability()) <= 0)) {
          return rule.getDestinationState();
        }
      }
    }
    return current;
  }

  @Override
  public boolean isReactiveRules() {
    return reactive;
  }

  @Override
  public INeighborhood getNeighborhood(int current) {
    return neighborhood;
  }

  @Override
  public boolean isTorus(int current) {
    return isTorus;
  }

}
