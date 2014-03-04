/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga.abort;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.asf.portfolios.ga.GeneticAlgorithmPortfolioSelector;


/**
 * Implements a list of (@Link IAbortCriterion). Abort when one or more
 * criterion in the list are fulfilled.
 * 
 * @author René Schulz, Roland Ewald
 * 
 */

public class ListOrAbort implements IAbortCriterion {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2493463653021647903L;

  /** The list of criteria. */
  private List<IAbortCriterion> criteria = new ArrayList<>();

  /**
   * Adds an abort criterion.
   * 
   * @param criterion
   *          the criterion
   */
  public void addCriterion(IAbortCriterion criterion) {
    criteria.add(criterion);
  }

  @Override
  public boolean abort(GeneticAlgorithmPortfolioSelector selector) {
    for (IAbortCriterion criterion : criteria) {
      if (criterion.abort(selector)) {
        return true;
      }
    }
    return false;
  }

}
