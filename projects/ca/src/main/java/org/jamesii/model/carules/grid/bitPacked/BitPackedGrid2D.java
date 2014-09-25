/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.grid.bitPacked;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jamesii.model.cacore.neighborhood.INeighborhood;
import org.jamesii.model.carules.CACell;
import org.jamesii.model.carules.ICACell;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.IGrid2D;

/**
 * The Class Grid. Adaptive Grid for all dimensions
 * 
 * @author Jonathan Wienss
 */
public final class BitPackedGrid2D extends BitPackedGrid implements IGrid2D {

  /**
   * Constructor for Grid
   * 
   * @param getSize
   *          ()
   * @param defaultState
   * @param numberOfStates
   */
  public BitPackedGrid2D(int[] size, int defaultState, int numberOfStates) {
    super(size, defaultState, numberOfStates);
  }

  public BitPackedGrid2D(int[] size, int defaultState, long[] data,
      int numberOfStates) {
    super(size, defaultState, data, numberOfStates);
  }

  @Override
  public List<ICACell> getCellList() {
    // NEED REWORK FOR 2 DIM
    // But should be not used anyway
    List<ICACell> result = new ArrayList<>();

    int[] currentCellCoords = new int[2];
    for (int i = 0; i < 2; i++) {
      currentCellCoords[i] = 0;
    }
    CACell currentCell = new CACell(currentCellCoords, 0);
    int cellNumber = 1; // Max number of cells
    for (int i = 0; i < 2; i++) {
      cellNumber = cellNumber * getSize()[i];
    }

    for (int i = 0; i < cellNumber; i++) {
      result.add(currentCell.clone());
    }

    int[] temp;
    currentCell = null;
    int currentCellPosition = 0;
    int last = 1;
    for (int i = 0; i < 2; i++) {
      while (currentCellPosition < cellNumber) {
        for (int j = 0; j < getSize()[i]; j++) {
          for (int k = 0; k < last; k++) {
            currentCell = (CACell) result.get(currentCellPosition);
            temp = currentCell.getPosition();
            temp[i] = j;
            currentCell.setPosition(temp);
            if (i == 1) {
              currentCell.setState(getState(temp));
            }
            result.set(currentCellPosition, currentCell);
            currentCellPosition++;
          }
        }
      }
      currentCellPosition = 0;
      last = last * getSize()[i];
    }

    return result;
  }

  @Override
  public List<int[]> getNeighbors(INeighborhood neighborhood, boolean torus,
      int... coord) {
    List<int[]> result = new ArrayList<>(neighborhood.getCellCount());

    if (torus) {
      // Now add every state to the list:
      for (int i = 0; i < neighborhood.getCellCount(); i++) {
        int[] pos = new int[2];
        // Get the absolute position of the neighbour-Cell in our Grid:
        pos[0] = normalize(coord[0] + neighborhood.getCell(i)[0], getSize()[0]);
        pos[1] = normalize(coord[1] + neighborhood.getCell(i)[1], getSize()[1]);
        result.add(getCoordID()[pos[0] + pos[1] * getSize()[0]]);
      }

    } else { // = If not Torus
      // Now add every state to the list:
      for (int i = 0; i < neighborhood.getCellCount(); i++) {
        // Test every diension for being out of bounds:
        if (coord[0] + neighborhood.getCell(i)[0] < 0
            || coord[0] + neighborhood.getCell(i)[0] >= getSize()[0]
            || coord[1] + neighborhood.getCell(i)[1] < 0
            || coord[1] + neighborhood.getCell(i)[1] >= getSize()[1]) {
          // result.add(defaultState);
          continue;
        }
        // If not out of bounds:
        int[] pos = new int[2];
        // Get the absolute position of the Cell in our Grid:
        pos[0] = coord[0] + neighborhood.getCell(i)[0];
        pos[1] = coord[1] + neighborhood.getCell(i)[1];

        result.add(getCoordID()[pos[0] + pos[1] * getSize()[0]]);
      }

    }
    return result;
  }

  @Override
  public void setState(int state, int... coords) {
    if (state >= getNumberOfStates()) {
      setNumberOfStates(state + 1);
      // Get the coordinates of the corresponding field:
      // Also get the field internal coords for later
    }

    setState(coords[0] / getExpansion()[0] + (coords[1] / getExpansion()[1])
        * getDataSize()[0], new int[] {
        (coords[0] - getExpansion()[0] * (coords[0] / getExpansion()[0])),
        (coords[1] - getExpansion()[1] * (coords[1] / getExpansion()[1])) },
        state);
  }

  @Override
  public int getState(int... coords) {
    // Get the coordinates of the corresponding field:
    // Also get the field internal coords for later
    return getState(coords[0] / getExpansion()[0]
        + (coords[1] / getExpansion()[1]) * getDataSize()[0], new int[] {
        (coords[0] - getExpansion()[0] * (coords[0] / getExpansion()[0])),
        (coords[1] - getExpansion()[1] * (coords[1] / getExpansion()[1])) });
  }

  /**
   * Sets the state in the field using coords (field internal coords!)
   * 
   * @param field
   * @param coords
   * @param state
   */
  private void setState(int dataPos, int[] coords, int state) {
    // if(state<0 || state>integerPow(2,bits))
    // System.out.println("### State not normalized!");
    // Setting new state
    getData()[dataPos] =
        (getData()[dataPos] & ~(getMask() << (coords[0] + coords[1]
            * getExpansion()[0])
            * getBits()))
            | (((long) state) << (coords[0] + coords[1] * getExpansion()[0])
                * getBits());
  }

  /**
   * Gets the state in the field using coords (field internal coords!)
   * 
   * @param field
   * @param coords1
   * @param state
   */
  private int getState(int dataPos, int[] coords) {
    // Retrieving information, shifting, returning
    return (int) ((getData()[dataPos] & (getMask() << (coords[0] + coords[1]
        * getExpansion()[0])
        * getBits())) >>> (coords[0] + coords[1] * getExpansion()[0])
        * getBits());
  }

  @Override
  public ICARulesGrid cloneGrid() {
    BitPackedGrid result =
        new BitPackedGrid2D(getSize().clone(), getDefaultState(),
            getNumberOfStates());
    result.setData(Arrays.copyOf(getData(), getData().length));
    return result;
  }
}
