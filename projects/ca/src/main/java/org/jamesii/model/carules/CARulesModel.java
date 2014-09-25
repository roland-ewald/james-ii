/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.grid.plugintype.BaseGridFactory;

/**
 * {@link ICARulesModel} Formalism implementation following the rule paradigm.
 * This implementation is able to model rule based cellular automata in 1D, 2D
 * and 3D. Plus it uses {@link CARule} as rule representation and
 * {@link CARuleBase} as rule base.
 * 
 * @author Stefan Rybacki
 */
public class CARulesModel extends AbstractCAModel implements ICARulesModel {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 4085248414668550817L;

  /**
   * The rule base.
   */
  private transient CARuleBase ruleBase;

  /**
   * Instantiates a new rule based CA model.
   * 
   * @param name
   *          the name
   * @param dimensions
   *          the dimensions
   * @param rules
   *          the rules
   * @param states
   *          the states
   * @param neighborhood
   *          the neighborhood
   * @param initialGrid
   *          the initial grid
   * @param gridSize
   *          the grid size
   * @param torus
   *          flag indicating whether to use torus
   * @param factory
   *          the factory used to create the grid
   */
  public CARulesModel(String name, int dimensions, List<CARule> rules,
      List<String> states, INeighborhood neighborhood,
      List<ICACell> initialGrid, int[] gridSize, boolean torus,
      BaseGridFactory factory) {
    super(dimensions, states, initialGrid, gridSize, factory);
    if (dimensions < 1 || dimensions > 3) {
      throw new IllegalArgumentException("Dimensions must be between 1 and 3");
    }
    if (rules == null || rules.isEmpty()) {
      throw new NullPointerException("Rules can't be null or empty.");
    }
    if (states == null || states.isEmpty()) {
      throw new NullPointerException("States can't be null or empty.");
    }
    if (neighborhood == null) {
      throw new NullPointerException("Neighborhood can't be null.");
    }
    if (gridSize == null) {
      throw new NullPointerException("gridSize can't be null.");
    }
    if (initialGrid == null) {
      throw new NullPointerException("initialGrid can't be null.");
    }
    if (gridSize.length != dimensions) {
      throw new IllegalArgumentException("Grid size doesn't match dimensions.");
    }

    this.ruleBase =
        new CARuleBase(rules, neighborhood, torus, SimSystem.getRNGGenerator()
            .getNextRNG());
  }

  /**
   * Instantiates a new rule based CA model.
   * 
   * @param name
   *          the name
   * @param dimensions
   *          the dimensions
   * @param rules
   *          the rules
   * @param states
   *          the states
   * @param neighborhood
   *          the neighborhood
   * @param initialGrid
   *          the initial grid
   * @param gridSize
   *          the grid size
   * @param torus
   *          flag indicating whether to use torus
   */
  public CARulesModel(String name, int dimensions, List<CARule> rules,
      List<String> states, INeighborhood neighborhood,
      List<ICACell> initialGrid, int[] gridSize, boolean torus) {
    super(dimensions, states, initialGrid, gridSize);

    this.ruleBase =
        new CARuleBase(rules, neighborhood, torus, SimSystem.getRNGGenerator()
            .getNextRNG());
  }

  @Override
  public ICARuleBase getBaseRules() {
    return ruleBase;
  }

  @Override
  public List<String> getStates() {
    return (List<String>) super.getStates();
  }

  @Override
  public void cleanUp() {
    super.cleanUp();

    this.ruleBase = null;
  }

}
