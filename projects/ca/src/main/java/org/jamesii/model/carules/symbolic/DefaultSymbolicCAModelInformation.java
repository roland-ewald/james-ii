/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.symbolic;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CARule;

/**
 * @author Stefan Rybacki
 */
public class DefaultSymbolicCAModelInformation implements
    ISymbolicCAModelInformation {
  /**
   * The models comment.
   */
  private String modelComment;

  /**
   * The wolfram rule if {@link #isWolfram()} returns true.
   */
  private int wolframRule;

  /**
   * The is wolfram model flag.
   */
  private boolean isWolfram;

  /**
   * The states.
   */
  private List<String> states;

  /**
   * The rules.
   */
  private List<CARule> rules;

  /**
   * The neighborhood.
   */
  private INeighborhood neighborhood;

  /**
   * The used dimensions.
   */
  private int dimensions;

  /**
   * The model source.
   */
  private String source;

  /**
   * Instantiates a new {@link ISymbolicCAModelInformation} information
   * implementation.
   * 
   * @param dimensions
   *          the dimensions to use
   * @param neighborhood
   *          the neighborhood used
   * @param rules
   *          the rules
   * @param states
   *          the states
   * @param isWolfram
   *          the is wolfram flag indicating whether the supplied model
   *          information belong to a wolfram model
   * @param wolframRule
   *          the wolfram rule the wolfram rule if isWolfram was true
   * @param modelComment
   *          the model comment
   * @param source
   *          the models source that this information was created from
   */
  public DefaultSymbolicCAModelInformation(int dimensions,
      INeighborhood neighborhood, List<CARule> rules, List<String> states,
      boolean isWolfram, int wolframRule, String modelComment, String source) {
    this.dimensions = dimensions;
    this.neighborhood = neighborhood;
    this.rules = new ArrayList<>(rules);
    this.states = new ArrayList<>(states);
    this.isWolfram = isWolfram;
    this.wolframRule = wolframRule;
    this.modelComment = modelComment;
    this.source = source;
  }

  @Override
  public int getDimensions() {
    return dimensions;
  }

  @Override
  public boolean isWolfram() {
    return isWolfram;
  }

  @Override
  public INeighborhood getNeighborhood() {
    return neighborhood;
  }

  @Override
  public List<CARule> getRules() {
    // defensive copy
    return new ArrayList<>(rules);
  }

  @Override
  public int getWolframRule() {
    return wolframRule;
  }

  @Override
  public List<String> getStates() {
    return new ArrayList<>(states);
  }

  @Override
  public String getModelComment() {
    return modelComment;
  }

  @Override
  public String getModelSource() {
    return source;
  }

}
