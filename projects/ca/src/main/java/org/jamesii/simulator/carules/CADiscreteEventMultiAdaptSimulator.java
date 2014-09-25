/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.carules;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.ICARulesModel;
import org.jamesii.model.carules.grid.ICARulesGrid;

/**
 * CA "discrete event" simulator. Here we've got a multi threaded version of
 * this algorithm. The cells to be computed are split to as many threads as
 * processors / cores exist on this machine. But we have a minimal number of
 * cells per thread to avoid the generation of too many threads for too few
 * cells. <br/>
 * This algorithm only parallelizes the computation of the new states - not the
 * collection of the next events (candidates) by gathering the neighbours nor
 * the setting of the new states to the grid of the model.
 * 
 * @author Jan Himmelspach
 */
public class CADiscreteEventMultiAdaptSimulator extends RunnableProcessor<Double>
    implements IRunnable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -5916361620604407646L;

  /** event queue for changed cells and neighbour cells. */
  private Map<int[], Object> candidates;

  /** The time. */
  private double time = 0.;

  /**
   * The thread count factor. Sometimes it might not be a good idea to use all
   * cores/CPUs, in other cases it might be useful to be able to have more
   * threads than physical cores/CPUs. By using this factor the number of
   * threads can be adapted relative to the physical capabilities.
   */
  private double threadCountFactor = 1.0;

  /** The min number of cells per thread. */
  private int minNumberOfCellsPerThread = 10;

  /** The proc count refresh counter. */
  private int procCountRefresh = 0;

  /** The number of processors. */
  private int procCount = 1;

  /**
   * Instantiates a new cA simulator.
   * 
   * @param model
   *          the model
   */
  public CADiscreteEventMultiAdaptSimulator(IModel model) {
    super(model);

    this.candidates = new HashMap<>();

    // for the first step we have to add ALL cells to the list of cells to be
    // updated
    for (ICACell<?> c : ((ICARulesModel) model).getGrid().getCellList()) {
      candidates.put(c.getPosition(), null);
    }
  }

  /**
   * Instantiates a new cA simulator.
   * 
   * @param model
   *          the model
   * @param minNumberOfCellsPerThread
   *          the min number of cells per thread
   * @param threadCountFactor
   *          the thread count factor
   */
  public CADiscreteEventMultiAdaptSimulator(IModel model,
      int minNumberOfCellsPerThread, double threadCountFactor) {
    this(model);
    this.minNumberOfCellsPerThread = minNumberOfCellsPerThread;
    this.threadCountFactor = threadCountFactor;
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

    // update number of processors occasionally
    procCountRefresh++;
    if (procCountRefresh == 1000) {
      Runtime r = Runtime.getRuntime();
      procCount = r.availableProcessors();
      // recompute the number of threads - if threadMult = 1.0 we'll have as
      // many threads as physical resources, > 1 means that's there is the
      // Probability to use more threads
      procCount = (int) (procCount * threadCountFactor);
      procCountRefresh = 0;
    }

    // list for the cells of which we computed new states
    HashMap<int[], Integer> ccells = new HashMap<>();

    // System.out.println("Going to use " + procs + " processors");

    List<int[]> cells = new ArrayList<>(candidates.keySet());

    // get the number of threads to be used right now, procsUsed = 0 is fine
    // here, because we'll have automatically an "additional one" for the rest
    int procsUsed = procCount;
    while ((procsUsed * minNumberOfCellsPerThread > cells.size())
        && (procsUsed > 1)) {
      procsUsed--;
    }

    int partition = cells.size() / procsUsed;

    ComputeThread[] threads = new ComputeThread[procsUsed + 1];

    // create a thread per partition
    int p = 0;
    for (int i = 0; i < procsUsed; i++) {
      threads[i] = new ComputeThread(m, grid, cells, p, p + partition);
      p += partition;
      threads[i].start();
    }
    // create an extra thread for the rest of the division ("uncomplete"
    // partition), if there is such a rest
    if (p != cells.size()) {
      threads[procsUsed] = new ComputeThread(m, grid, cells, p, cells.size());
      threads[procsUsed].start();
    }

    // now wait until all threads have finished their job
    try {
      for (ComputeThread thread : threads) {
        if (thread != null) {
          thread.join();
          ccells.putAll(thread.ccells);
        }

      }
    } catch (InterruptedException e) {
      SimSystem.report(e);
    }

    // empty the old list of cells to be updated, and let's refill it
    this.candidates.clear();

    // for all modified cells do
    for (Map.Entry<int[], Integer> e : ccells.entrySet()) {
      int current = m.getGrid().getState(e.getKey());
      // update the cell's state
      m.getGrid().setState(e.getValue(), e.getKey());

      // remember the cell as to be updated in the next turn
      this.candidates.put(e.getKey(), null);

      // add all neighbour cells of the current cell to the list of cells to be
      // computed in the next step
      List<int[]> n =
          m.getGrid().getNeighbors(m.getBaseRules().getNeighborhood(current),
              m.getBaseRules().isTorus(current), e.getKey());
      for (int i = 0; i < n.size(); i++) {
        this.candidates.put(n.get(i), null);
      }
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

    /** The cells. */
    private List<int[]> cells;

    /** The from (lower level). */
    private int from;

    /** The to (upper border). */
    private int to;

    /** The m. */
    private ICARulesModel m;

    // list for the cells of which we computed new states
    /** The ccells. */
    private Map<int[], Integer> ccells = new HashMap<>();

    /**
     * Instantiates a new compute thread.
     * 
     * @param m
     *          the m
     * @param grid
     *          the grid
     * @param cells
     *          the cells
     * @param from
     *          the from
     * @param to
     *          the to
     */
    public ComputeThread(ICARulesModel m, ICARulesGrid grid, List<int[]> cells,
        int from, int to) {
      this.grid = grid;
      this.cells = cells;
      this.from = from;
      this.to = to;
      this.m = m;
    }

    @Override
    public void run() {
      // now iterate over all cells in the copy of the grid
      for (int i = from; i < to; i++) {

        int[] cell = cells.get(i);

        if (cell == null) {
          continue;
        }

        // get the current state
        int current = grid.getState(cell);

        // get the neighbours of the current cell
        INeighborStates<Integer> neighbours =
            grid.getNeighborStates(m.getBaseRules().getNeighborhood(current), m
                .getBaseRules().isTorus(current), cell);

        // compute the new state based on the current one, and the states of the
        // neighbours
        int next = m.getBaseRules().getNextState(current, neighbours);

        // if the state has been changed
        if (current != next) {
          ccells.put(cell, next);
        }

      }
    }
  }

}
