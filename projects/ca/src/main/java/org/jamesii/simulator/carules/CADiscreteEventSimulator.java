/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.ICARulesModel;

/**
 * The cellular automata, discrete event simulator tries to optimize the
 * computation of cellular automata. This simulator only computes the states of
 * the cells which require an update, thus of those where the state could be
 * changed due to changes in the previous step. Thus, if there is only little
 * change in the previous step this simulator has to update fewer cells as the
 * "brute force" simulation algorithm.
 * 
 * @author Mathias Süß
 * @author Jan Himmelspach
 */
public class CADiscreteEventSimulator extends RunnableProcessor implements
    IRunnable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 7115013897581267209L;

  /** event queue for changed cells and neighbour cells. */
  private Map<int[], Object> candidates;

  /** The time. */
  private double time = 0.;

  // /**
  // *
  // */
  // private long last = System.currentTimeMillis();

  /**
   * Create a new instance of a discrete event simulation algorithm for cellular
   * automata.
   * 
   * @param model
   */
  public CADiscreteEventSimulator(ICARulesModel model) {
    super(model);
    this.candidates = new HashMap<>();

    // for the first step we have to add ALL cells to the list of cells to be
    // updated
    for (ICACell<?> c : (model).getGrid().getCellList()) {
      candidates.put(c.getPosition(), null);
    }
  }

  @Override
  protected void nextStep() {
    // list for the cells of which we computed new states
    HashMap<int[], Integer> cells = new HashMap<>();

    // the cellular automaton
    ICARulesModel m = (ICARulesModel) this.getModel();

    // let's update each cell which could get a new state
    for (int[] cell : candidates.keySet()) {
      if (cell == null) {
        continue;
      }

      // get the current state of the cell
      int current = m.getGrid().getState(cell);

      // get the neighbours of the current cell
      INeighborStates<Integer> neighbours =
          m.getGrid().getNeighborStates(
              m.getBaseRules().getNeighborhood(current), false, cell);

      // compute the new state
      int next = m.getBaseRules().getNextState(current, neighbours);

      // if the state has been changed
      if (current != next) {
        cells.put(cell, next);
      }
    }

    // empty the old list of cells to be updated, and let's refill it
    this.candidates = new HashMap<>(); // .clear();

    // for all modified cells do
    for (Map.Entry<int[], Integer> e : cells.entrySet()) {
      int current = m.getGrid().getState(e.getKey());
      // update the cell's state
      m.getGrid().setState(e.getValue(), e.getKey());

      // remember the cell as to be updated in the next turn
      this.candidates.put(e.getKey(), null);

      // add all neighbour cells of the current cell to the list of cells to be
      // computed in the next step
      List<int[]> n =
          m.getGrid().getNeighbors(m.getBaseRules().getNeighborhood(current),
              false, e.getKey());
      for (int i = 0; i < n.size(); i++) {
        this.candidates.put(n.get(i), null);
      }
    }

    m.changed();

    // long curr = System.currentTimeMillis();
    // advance the time
    time += 1;
    // if (time % 1000 == 0) {
    // System.out.println(time + " time needed: " + (curr - last) / 1000d);
    // last = curr;
    // }
    changed();
  }

  @Override
  public Double getTime() {
    return time;
  }

}
