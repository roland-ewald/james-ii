/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: Rule60
 * Description:  Cellular automata behaving according to rule60 of Wolfram (1986)
 * 									X - dead, O - alive
 * 									actual cell
 * 											 |
 * 										    XOX				=> alive
 * 											XOO				=> alive
 * 											OXX				=> alive
 * 											OXO				=> alive
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.examples.ca.wolframsrules;

import java.util.Arrays;

import org.jamesii.examples.ca.DeadAliveState;
import org.jamesii.model.ca.Cell;
import org.jamesii.model.cacore.INeighborStates;

public class RuleCell extends Cell<DeadAliveState, Boolean> {

  static final long serialVersionUID = -1030377398621680956L;

  private int[] help = new int[1];

  private int rule[];

  private int rule102[] = { 0, 1, 1, 0, 0, 1, 1, 0 };

  private int rule110[] = { 0, 1, 1, 0, 1, 1, 1, 0 };

  private int rule150[] = { 1, 0, 0, 1, 0, 1, 1, 0 };

  private int rule250[] = { 1, 1, 1, 1, 1, 0, 1, 0 };

  // binary representation of several rules
  private int rule30[] = { 0, 0, 0, 1, 1, 1, 1, 0 };

  private int rule60[] = { 0, 0, 1, 1, 1, 1, 0, 0 };

  private int rule90[] = { 0, 1, 0, 1, 1, 0, 1, 0 };

  public RuleCell() {
    super();
  }

  /**
   * @param name
   */
  public RuleCell(String name) {
    super(name);
  }

  @Override
  protected DeadAliveState createState() {
    return new DeadAliveState();
  }

  /**
   * Initialize the cell's alive status
   */
  @Override
  public void init() {
    // deterministic 1
    // ((CAState) getState()).setAlive(coordinates[0] % 2 == 0);
    // deterministic 2
    // ((CAState) getState()).setAlive(coordinates[0] % 2 == 1);
    // random
    // ((CAState) getState()).setAlive((Math.round(Math.random())) == 0);
    setState(getCoordinates()[0] == getGridDimension()[0] / 2);
  }

  @Override
  public Boolean getNextState(Boolean cellState,
      INeighborStates<Boolean> neighborStates) {

    int index = 7;

    index =
        Boolean.TRUE.equals(neighborStates.getState(-1)) ? index - 4 : index;
    index = cellState ? index - 2 : index;
    index = Boolean.TRUE.equals(neighborStates.getState(1)) ? index - 1 : index;

    // System.out.print(index+ rule[index]);

    return getRule()[index] == 1;
  }

  /**
   * @return the rule
   */
  public int[] getRule() {
    return Arrays.copyOf(rule, rule.length);
  }

  /**
   * @param rule
   *          the rule to set
   */
  public void setRule(int rule[]) {
    this.rule = Arrays.copyOf(rule, rule.length);
  }

}
