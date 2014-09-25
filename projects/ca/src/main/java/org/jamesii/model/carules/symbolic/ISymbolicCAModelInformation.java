/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.symbolic;

import java.util.List;

import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CARule;

/**
 * The Interface ISymbolicCAModelInformation.
 * 
 * @author Jan Himmelspach
 */
public interface ISymbolicCAModelInformation {

  /**
   * Gets the dimensions.
   * 
   * @return the dimensions
   */
  int getDimensions();

  /**
   * Checks if is wolfram.
   * 
   * @return true, if is wolfram
   */
  boolean isWolfram();

  /**
   * Gets the neighborhood.
   * 
   * @return the neighborhood
   */
  INeighborhood getNeighborhood();

  /**
   * Gets the rules.
   * 
   * @return the rules
   */
  List<CARule> getRules();

  /**
   * Gets the wolfram rule.
   * 
   * @return the wolfram rule
   */
  int getWolframRule();

  /**
   * Gets the states.
   * 
   * @return the states
   */
  List<String> getStates();

  /**
   * Returns the comment for the model
   * 
   * @return the model comment for the current version
   */
  String getModelComment();

  /**
   * Gets the model source. That this {@link ISymbolicCAModelInformation}
   * originated from.
   * 
   * @return the model source
   */
  String getModelSource();
}
