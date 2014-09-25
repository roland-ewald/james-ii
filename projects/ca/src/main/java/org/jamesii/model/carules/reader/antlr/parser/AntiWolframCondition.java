/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACondition;

/**
 * Is the reverse rule implementation of {@link WolframCondition} and is used in
 * combination with it to simulate wolfram CAs.
 * 
 * @author Stefan Rybacki
 * 
 */
class AntiWolframCondition implements ICACondition {
  /**
   * The rule.
   */
  private final int rule;

  /**
   * The binary representation of the rule.
   */
  private String binRule;

  /**
   * Instantiates a new anti wolfram condition for the specified wolfram rule.
   * 
   * @param rule
   *          the rule
   */
  public AntiWolframCondition(int rule) {
    if (rule < 0 || rule > 255) {
      throw new IllegalArgumentException(
          "rule must be in the range of 0 and 255");
    }
    this.rule = rule;
    binRule = Integer.toBinaryString(rule);
    // fill binary representation with leading zeros to get a complete
    // length of 8.
    while (this.binRule.length() < 8) {
      binRule = "0" + this.binRule;
    }
  }

  @Override
  public boolean isTrue(int currentState, INeighborStates<Integer> neighbors) {
    if (neighbors == null) {
      return false;
    }

    int index = 7;

    Integer left = neighbors.getState(-1) == null ? 0 : neighbors.getState(-1);
    Integer right = neighbors.getState(1) == null ? 0 : neighbors.getState(1);

    // FIXME specify whether torus or not
    index = left == 1 ? index - 4 : index;
    index = currentState == 1 ? index - 2 : index;
    index = right == 1 ? index - 1 : index;

    return binRule.charAt(index) != '1';
  }

  @Override
  public ICACondition getCondition(int index) {
    return null;
  }

  @Override
  public int getConditionCount() {
    return 0;
  }

  @Override
  public String toString() {
    return "wolfram rule !" + rule + " -> DEAD";
  }

}
