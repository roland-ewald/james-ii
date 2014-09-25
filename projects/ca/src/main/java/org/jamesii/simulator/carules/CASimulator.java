/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * CA "full" simulator. This brute force simulator computes the state of each
 * cell of the grid per simulation step. Thereby the simulator does not take
 * into account whether such a cell might get a new state or not. However, this
 * is usually considered to be the most simple simulation algorithm for cellular
 * automata.
 * 
 * @author Mathias Süß
 * @author Jan Himmelspach
 * 
 */
public class CASimulator extends RunnableProcessor<Double> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5916361620604407646L;

  /** The time. */
  private double time = 0.;

  /**
   * Instantiates a new cA simulator.
   * 
   * @param model
   *          the model
   */
  public CASimulator(IModel model) {
    super(model);
  }

  @Override
  public Double getTime() {
    return time;
  }

  @Override
  protected void nextStep() {
    ICARulesModel m = this.getModel();

    // get the grid from the model
    ICARulesGrid grid = m.getGrid();

    // make a copy of the grid, and thus of each cell's state
    ICARulesGrid oldGrid = grid.cloneGrid();

    // now iterate over all cells in the copy of the grid
    for (ICACell<?> cell : oldGrid.getCellList()) {

      // get the current state
      int current = cell.getState();

      // get the neighbours of the current cell
      INeighborStates<Integer> neighbours =
          oldGrid.getNeighborStates(m.getBaseRules().getNeighborhood(current),
              false, cell.getPosition());

      // compute the new state based on the current one, and the states of the
      // neighbours
      int next = m.getBaseRules().getNextState(current, neighbours);

      // now set the new state (to the ORIGINAL grid, not the original one)
      grid.setState(next, cell.getPosition());

    }
    m.changed();

    // advance the time
    time += 1;
    changed();

  }

}
