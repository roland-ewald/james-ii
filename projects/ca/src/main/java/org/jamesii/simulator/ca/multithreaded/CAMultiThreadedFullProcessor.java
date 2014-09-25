/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.multithreaded;

import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.RunnableProcessor;
import org.jamesii.core.util.concurrent.ForKernel;
import org.jamesii.core.util.concurrent.ForLoop;
import org.jamesii.model.ca.Cell;
import org.jamesii.model.ca.grid.ICAGrid;
import org.jamesii.model.ca.grid.IGrid;
import org.jamesii.model.cacore.CAState;
import org.jamesii.simulator.carules.CAMultiSimulator;

/**
 * Simulator for cellular models. This simulator performs a full processing of
 * the given cellular model (thus all cells are recomputed). The simulator works
 * for n-dimensional cellular automata. It uses multiple threads and an internal
 * handled grid to achieve better performance.
 * 
 * @author Stefan Rybacki
 */
public class CAMultiThreadedFullProcessor extends RunnableProcessor<Double> {

  private double time = 0;

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 7347100690045626223L;

  private Object[] gridFrom;

  private Object[] gridTo;

  private NeighborStates[] nStates;

  private int[][] coordsCache;

  private final int[] dimensions;

  private int counter = 0;

  private int writeBackInterval = 1;

  private transient final ForKernel kernel = new ForKernel() {

    @Override
    public void run(int i) {
      Cell<? extends CAState<Object>, Object> cell =
          (Cell<? extends CAState<Object>, Object>) ((IGrid) getModel())
              .getCell(indexToModel(i));
      Object nextState = cell.getNextState(gridFrom[i], nStates[i]);

      gridTo[i] = nextState;

      if (writeBackInterval <= counter) {
        cell.setState(nextState);
      }
    }
  };

  private int maxThreads;

  private boolean indexCache;

  public CAMultiThreadedFullProcessor(IModel model, int writeBack,
      int maxThreads, boolean indexCache) {
    super(model);

    writeBackInterval = writeBack;
    this.maxThreads = maxThreads;

    ICAGrid camodel = getModel();

    dimensions = camodel.getDimensions();

    // create internal grid
    int max = 1;
    for (int v : dimensions) {
      max *= v;
    }

    gridFrom = new Object[max];
    gridTo = new Object[max];
    nStates = new NeighborStates[max];

    this.indexCache = indexCache;
    if (indexCache) {
      coordsCache = new int[max][];
    }

    // fill from grid
    for (int i = 0; i < gridFrom.length; i++) {
      Cell<? extends CAState<Object>, Object> cell =
          (Cell<? extends CAState<Object>, Object>) camodel
              .getCell(indexToModel(i));
      gridFrom[i] = cell.getState();
      nStates[i] = new NeighborStates(this, cell, dimensions, cell.isTorus());
    }
  }

  private int[] indexToModel(int index) {
    if (!indexCache) {
      return CAMultiSimulator.convert1DimIndexToNDimIndex(dimensions, index);
    }
    int[] c = coordsCache[index];
    if (c == null) {
      c = CAMultiSimulator.convert1DimIndexToNDimIndex(dimensions, index);
      coordsCache[index] = c;
    }

    return c;
  }

  @Override
  protected void nextStep() {
    final ICAGrid model = getModel();

    counter++;

    ForLoop.FOR(0, gridFrom.length, kernel, maxThreads);

    // flip grids
    Object[] temp = gridFrom;
    gridFrom = gridTo;
    gridTo = temp;

    if (counter >= writeBackInterval) {
      counter = 0;
      model.changed();
      changed();

      model.getState().changed();
      model.getState().isChangedRR();
    }

    time += model.timeAdvance();
  }

  @Override
  public Double getTime() {
    return time;
  }

  protected Object[] getGrid() {
    return gridFrom;
  }

}
