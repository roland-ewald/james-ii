/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.simulator.ca.multithreaded;

import java.util.Arrays;

import org.jamesii.model.ca.Cell;
import org.jamesii.model.cacore.CAState;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.simulator.carules.CAMultiSimulator;

/**
 * 
 * LICENCE: JAMESLIC
 * 
 * @author Stefan Rybacki
 * 
 */
class NeighborStates<T> implements INeighborStates<T> {

  private INeighborhood neighborhood;

  private int[] dimensions;

  private int[] pos;

  private boolean torus;

  private CAMultiThreadedFullProcessor processor;

  public NeighborStates(CAMultiThreadedFullProcessor processor,
      Cell<CAState<T>, T> cell, int[] dimensions, boolean torus) {
    neighborhood = cell.getNeighborhood();
    this.dimensions = Arrays.copyOf(dimensions, dimensions.length);
    this.pos = cell.getCoordinates();
    this.torus = torus;
    this.processor = processor;
  }

  @Override
  public T getState(int... c) {
    int[] coord = c.clone();
    for (int i = 0; i < coord.length; i++) {
      coord[i] += pos[i];
      // special treatmeant for torus
      if (torus && coord[i] < 0) {
        coord[i] += dimensions[i];
      }
      if (torus && coord[i] >= dimensions[i]) {
        coord[i] -= dimensions[i];
      }

      // return null if outside of grid
      if (coord[i] < 0 || coord[i] >= dimensions[i]) {
        return null;
      }
    }

    T[] grid = (T[]) processor.getGrid();
    return grid[CAMultiSimulator.convertNDimIndexTo1DimIndex(dimensions, coord)];
  }

  @Override
  public int getCountOf(T state) {
    return getCountOf(state, neighborhood);
  }

  @Override
  public int getCountOf(T state, INeighborhood in) {
    int result = 0;
    for (int i = 0; i < in.getCellCount(); i++) {
      T nState = getState(in.getCell(i));
      if (state == nState || state.equals(nState)) {
        result++;
      }
    }

    return result;
  }

}
