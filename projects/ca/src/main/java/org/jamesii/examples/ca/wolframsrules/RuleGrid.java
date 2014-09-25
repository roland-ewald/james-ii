/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA:
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.examples.ca.wolframsrules;

import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.Grid1D;
import org.jamesii.model.cacore.CAState;

public class RuleGrid extends Grid1D {

  static final long serialVersionUID = 9142419966514823882L;

  private int[] rule;

  public RuleGrid() {
    super();
    setWidth(20);
  }

  /**
   * @param name
   */
  public RuleGrid(String name, int width, int[] rule) {
    super(name);
    this.setWidth(width);
    this.rule = rule;
  }

  /**
   * Return the class of the cells of the grid
   * 
   * @return cell class
   */
  @Override
  public Class<? extends Cell<? extends CAState, ?>> getCellClass() {
    return RuleCell.class;
  }

  /**
   * Return the dimension of the grid Here only the width
   */
  @Override
  public int[] getDimensions() {
    return new int[] { getWidth() };
  }

  @Override
  public int[] getNeighbours(int[] cell) {

    return null;
  }

  @Override
  public void initCell(Cell<? extends CAState<?>, ?> cell) {
    ((RuleCell) cell).setRule(rule);
  }

  /**
   * time advance is always 1
   */
  @Override
  public double timeAdvance() {
    return 1;
  }

}
