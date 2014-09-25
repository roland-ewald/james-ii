/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules;

import java.util.List;

import org.jamesii.core.model.IModel;
import org.jamesii.model.ca.ICAModel;

/**
 * The Interface ICAModel.
 * 
 * @author Stefan Rybacki
 */
public interface ICARulesModel extends ICAModel, IModel {

  /**
   * Gets the states.
   * 
   * @return the states
   */
  @Override
  List<String> getStates();

  /**
   * Gets the base rules.
   * 
   * @return the base rules
   */
  ICARuleBase getBaseRules();

  /**
   * Gets the grid.
   * 
   * @return the grid
   */
  @Override
  org.jamesii.model.carules.grid.ICARulesGrid getGrid();

}
