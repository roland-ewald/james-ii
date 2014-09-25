/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

import org.jamesii.SimSystem;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * CA "full" simulator. This brute force simulator computes the state of each
 * cell of the grid per simulation step. Thereby the simulator does not take
 * into account whether such a cell might get a new state or not. However, this
 * is usually considered to be the most simple simulation algorithm for cellular
 * automata. Here we've got a multi threaded version of this algorithm. The
 * cells to be computed are split to as many threads as processors / cores exist
 * on this machine.
 * 
 * @author Jan Himmelspach
 */
public class CAMultiSimulator extends RunnableProcessor<Double> implements IRunnable {

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
  public CAMultiSimulator(IModel model) {
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

    Runtime r = Runtime.getRuntime();

    int procs = r.availableProcessors();

    // System.out.println("Going to use " + procs + " processors");
    int[] size = oldGrid.getSize();
    int cellCount = 1;
    for (int element : size) {
      cellCount *= element;
    }

    int partition = cellCount / procs;

    ComputeThread[] threads = new ComputeThread[procs + 1];

    // create a thread per partition
    int p = 0;
    for (int i = 0; i < threads.length; i++) {
      threads[i] =
          new ComputeThread(m, grid, oldGrid, p, Math.min(cellCount - 1, p
              + partition));
      p += partition;
      threads[i].start();
    }
    // create an extra thread for the rest of the division ("uncomplete"
    // partition), if there is such a rest
    if (p < cellCount - 1) {
      threads[partition] =
          new ComputeThread(m, grid, oldGrid, p, cellCount - 1);
      threads[partition].start();
    }

    // now wait until all threads have finished their job
    try {
      for (ComputeThread thread : threads) {
        if (thread != null) {
          thread.join();
        }
      }
    } catch (InterruptedException e) {
      SimSystem.report(e);
    }

    m.changed();

    // advance the time
    time += 1;
    changed();

  }

  /**
   * The Class ComputeThread.
   */
  private static class ComputeThread extends Thread {

    /** The grid. */
    private ICARulesGrid grid;

    /** The old grid. */
    private ICARulesGrid oldGrid;

    /** The from (start). */
    private int from;

    /** The to (upper limit). */
    private int to;

    /** The m. */
    private ICARulesModel model;

    /**
     * Instantiates a new compute thread.
     * 
     * @param m
     *          the m
     * @param grid
     *          the grid
     * @param oldGrid
     *          the old grid
     * @param from
     *          the from
     * @param to
     *          the to
     */
    public ComputeThread(ICARulesModel m, ICARulesGrid grid,
        ICARulesGrid oldGrid, int from, int to) {
      this.grid = grid;
      this.oldGrid = oldGrid;
      this.from = from;
      this.to = to;
      this.model = m;
    }

    @Override
    public void run() {
      // now iterate over all cells in the copy of the grid
      int[] size = oldGrid.getSize();
      int[] coord = new int[size.length];

      for (int i = from; i < to; i++) {

        CAMultiSimulator.convert1DimIndexToNDimIndex(size, i, coord);

        // get the current state
        int current = oldGrid.getState(coord);

        // get the neighbours of the current cell
        INeighborStates<Integer> neighbours =
            oldGrid.getNeighborStates(
                model.getBaseRules().getNeighborhood(current), false, coord);

        // compute the new state based on the current one, and the states of the
        // neighbours
        int next = model.getBaseRules().getNextState(current, neighbours);

        // now set the new state (to the ORIGINAL grid, not the original one)
        grid.setState(next, coord);

      }
    }
  }

  /**
   * Converts an N dimensional index to a one dimensional index.
   * 
   * @param size
   *          the size of the N dimensional body
   * @param index
   *          the index
   * 
   * @return the one dimensional index
   */
  public static int convertNDimIndexTo1DimIndex(int[] size, int[] index) {
    int res = 0;
    int m = 1;
    for (int i = 0; i < size.length; i++) {
      res += index[i] * m;
      m *= size[i];
    }

    return res;
  }

  /**
   * Converts one dimensional index to an N dimensional index.
   * 
   * @param size
   *          the size of the used N dimensional body
   * @param index
   *          the index
   * @param coord
   *          the array to store the N dimensional index to
   * 
   */
  public static void convert1DimIndexToNDimIndex(int[] size, int index,
      int[] coord) {
    // transform i into coordinate system of grid
    int m = 1;
    int result = index;
    int[] f = new int[size.length - 1];
    for (int j = 0; j < size.length - 1; j++) {
      m *= size[j];
      f[j] = m;
    }

    for (int j = size.length - 1; j > 0; j--) {
      int b = result % f[j - 1];
      coord[j] = (result - b) / f[j - 1];
      result = b;
    }

    coord[0] = result;
  }

  /**
   * Converts one dimensional index to an N dimensional index.
   * 
   * @param size
   *          the size of the used N dimensional body
   * @param index
   *          the index
   * 
   * @return the N dimensional index
   */
  public static int[] convert1DimIndexToNDimIndex(int[] size, int index) {
    int[] coord = new int[size.length];

    convert1DimIndexToNDimIndex(size, index, coord);

    return coord;
  }

}
