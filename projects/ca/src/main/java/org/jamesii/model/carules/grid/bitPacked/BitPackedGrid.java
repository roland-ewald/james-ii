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
import org.jamesii.model.carules.grid.AbstractGrid;
import org.jamesii.model.carules.grid.ICARulesGrid;
import org.jamesii.model.carules.grid.IGrid2D;

/**
 * The Class Grid. Adaptive Grid for all dimensions
 * 
 * @author Jonathan Wienss
 */
public class BitPackedGrid extends AbstractGrid implements IGrid2D {

  /** The Amount of Bits in the current system. See above */
  private static final int SYSTEM_BITS = 64;

  /** The data. */
  private long[] data;

  // int[] data;

  /** The Coordinates, has-ID of a cell */
  private int[][] coordID;

  /** Size of the data-Array in each dimension */
  private int[] dataSize;

  /** Size of the fields in each dimension */
  private int[] expansion;

  /** The size. */
  private int[] size;

  /** The amount of states */
  private int numberOfStates;

  /** The number of bits for each cell. Results from numberOfStates */
  private int bits;

  /**
   * Mask for the stateinformation inside data[i] example: numberOfStates=5 so
   * mask=...000000111
   */
  private long mask;

  /** The cached hash code. */
  private int hashCode;

  /**
   * Constructor for Grid
   * 
   * @param size
   * @param defaultState
   * @param numberOfStates
   */
  public BitPackedGrid(int[] size, int defaultState, int numberOfStates) {
    super(defaultState);

    this.size = size;
    this.setNumberOfStates(numberOfStates);
    int dimensions = size.length;
    setDataSize(new int[dimensions]);

    // the number of bits per cell used in each field
    setBits(0);
    // the expansion of each field
    expansion = new int[dimensions];

    // calculate number of bits per cell:
    for (int i = 0; i < numberOfStates; i++) {
      // equal of double to int???? hm...
      // should work now...
      if (integerPow(2, i) >= numberOfStates) {
        setBits(i);
      }
    }

    // estimate the semantics of each data-field
    // each field has an expansion in each dimension
    // trying to math a cube etc.
    int end = 0;
    while (end < dimensions) {
      end = 0;
      for (int i = 0; i < dimensions; i++) {
        expansion[i]++;
        int prod = 1;
        // get the volume of the expansion
        for (int j = 0; j < dimensions; j++) {
          prod = prod * expansion[j];
        }
        // if the expansion is to big
        if (prod > java.lang.Math.floor((double)getSystemBits() / getBits())) {
          expansion[i]--;
          end++;
        }
      }
    }

    // Create the (bit) mask:
    setMask(0x0000000000000001);
    for (int i = 1; i < getBits(); i++) {
      setMask(getMask() << 1);
      setMask(getMask() | 0x0000000000000001);
    }

    setSize(size);

    int temp = 1;
    for (int element : size) {
      temp = temp * element;
    }
    setCoordID(new int[temp][]);

  }

  public BitPackedGrid(int[] size, int defaultState, long[] data,
      int numberOfStates) {
    super(defaultState);

    this.size = size;
    this.setNumberOfStates(numberOfStates);
    int dimensions = size.length;
    setDataSize(new int[dimensions]);

    // the number of bits per cell used in each field
    setBits(0);
    // the expansion of each field
    expansion = new int[dimensions];

    // calculate number of bits per cell:
    for (int i = 0; i < numberOfStates; i++) {
      // equal of double to int???? hm...
      // should work now...
      if (integerPow(2, i) >= numberOfStates) {
        setBits(i);
      }
    }

    // estimate the semantics of each data-field
    // each field has an expansion in each dimension
    // trying to math a cube etc.
    int end = 0;
    while (end < dimensions) {
      end = 0;
      for (int i = 0; i < dimensions; i++) {
        expansion[i]++;
        int prod = 1;
        // get the volume of the expansion
        for (int j = 0; j < dimensions; j++) {
          prod = prod * expansion[j];
        }
        // if the expansion is to big
        if (prod > getSystemBits() / getBits()) {
          expansion[i]--;
          end++;
        }
      }
    }

    // Create the (bit) mask:
    setMask(0x0000000000000001);
    for (int i = 1; i < getBits(); i++) {
      setMask(getMask() << 1);
      setMask(getMask() | 0x0000000000000001);
    }

    setSize(size);

    this.setData(data.clone());

    int temp = 1;
    for (int element : size) {
      temp = temp * element;
    }
    setCoordID(new int[temp][]);
  }

  @Override
  public ICARulesGrid cloneGrid() {
    BitPackedGrid result =
        new BitPackedGrid(size.clone(), getDefaultState(), getNumberOfStates());
    result.setData(Arrays.copyOf(getData(), getData().length));
    return result;
  }

  @Override
  public ICACell getCell(int... coord) {
    return new CACell(coord, getState(coord));
  }

  @Override
  public List<ICACell> getCellList() {
    // NEED REWORK FOR coordID
    // BUT should not be used anyway
    List<ICACell> result = new ArrayList<>();

    int[] currentCellCoords = new int[size.length];
    for (int i = 0; i < size.length; i++) {
      currentCellCoords[i] = 0;
    }
    CACell currentCell = new CACell(currentCellCoords, 0);
    int cellNumber = 1; // Max number of cells
    for (int element : size) {
      cellNumber = cellNumber * element;
    }

    for (int i = 0; i < cellNumber; i++) {
      result.add(currentCell.clone());
    }

    int[] temp;
    currentCell = null;
    int currentCellPosition = 0;
    int last = 1;
    for (int i = 0; i < size.length; i++) {
      while (currentCellPosition < cellNumber) {
        for (int j = 0; j < size[i]; j++) {
          for (int k = 0; k < last; k++) {
            currentCell = (CACell) result.get(currentCellPosition);
            temp = currentCell.getPosition();
            temp[i] = j;
            currentCell.setPosition(temp);
            if (i == size.length - 1) {
              currentCell.setState(getState(temp));
            }
            result.set(currentCellPosition, currentCell);
            currentCellPosition++;
          }
        }
      }
      currentCellPosition = 0;
      last = last * size[i];
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
        int[] pos = new int[size.length];
        // Get the absolute position of the neighbour-Cell in our Grid:
        for (int j = 0; j < size.length; j++) {
          pos[j] = normalize(coord[j] + neighborhood.getCell(i)[j], size[j]);
        }
        result.add(pos);
      }

    } else { // = If not Torus
      // Now add every state to the list:
      for (int i = 0; i < neighborhood.getCellCount(); i++) {
        boolean cont = false;
        // Test every dimension for being out of bounds:
        for (int j = 0; j < size.length; j++) {
          if (coord[j] + neighborhood.getCell(i)[j] < 0
              || coord[j] + neighborhood.getCell(i)[j] >= size[j]) {
            // result.add(defaultState); // Neighbour does not exist
            cont = true;
          }
        }
        // Break bcs current cell is not on the grid
        if (cont) {
          continue;
        }
        // If not out of bounds:

        int[] pos = new int[size.length];
        // Get the absolute position of the Cell in our Grid:
        for (int j = 0; j < size.length; j++) {
          pos[j] = coord[j] + neighborhood.getCell(i)[j];
        }

        int temp = 1;
        int position = 0;
        for (int j = 0; j < size.length; j++) {
          position = position + temp * pos[j];
          temp = temp * size[j];
        }
        result.add(getCoordID()[position]);
      }

    }
    return result;
  }

  @Override
  public synchronized int[] getSize() {
    return size.clone();
  }

  @Override
  public void initGrid(List<ICACell> initStates) {

  }

  @Override
  public void setState(int state, int... coords) {
    if (state >= getNumberOfStates()) {
      setNumberOfStates(state + 1);
    }

    // Get the coordinates of the corresponding field:
    int[] dataCoords = new int[size.length];
    // Also get the field internal coords for later
    int[] fieldCoords = new int[size.length];
    for (int i = 0; i < size.length; i++) {
      // coords1[i] = (int) java.lang.Math.floor((float) coords[i] / (float)
      // expansion[i]);
      dataCoords[i] = coords[i] / expansion[i];
      // internalCoords[i] = (int) Math.round(Math.IEEEremainder(coords[i],
      // expansion[i]) * expansion[i]);
      // internalCoords[i] =
      // expansion[i]*(coords[i]-expansion[i]*(coords[i]/expansion[i]));
      fieldCoords[i] = (coords[i] - expansion[i] * (coords[i] / expansion[i]));
    }
    // Get the position in the data:
    int dataPos = 0;
    int temp = 1;
    for (int i = 0; i < size.length; i++) {
      dataPos = dataPos + temp * dataCoords[i];
      temp = temp * getDataSize()[i];
    }

    setState(dataPos, fieldCoords, state);
  }

  @Override
  public int getState(int... coords) {
    // Get the coordinates of the corresponding field:
    int[] dataCoords = new int[size.length];
    // Also get the field internal coords for later
    int[] fieldCoords = new int[size.length];
    for (int i = 0; i < size.length; i++) {
      // coords1[i] = (int) java.lang.Math.floor((float) coords[i] / (float)
      // expansion[i]);
      dataCoords[i] = coords[i] / expansion[i];
      // internalCoords[i] = (int) Math.round(Math.IEEEremainder(coords[i],
      // expansion[i]) * expansion[i]);
      // internalCoords[i] =
      // expansion[i]*(coords[i]-expansion[i]*(coords[i]/expansion[i]));
      fieldCoords[i] = (coords[i] - expansion[i] * (coords[i] / expansion[i]));
    }
    // Get the position in the data:
    int dataPos = 0;
    int temp = 1;
    for (int i = 0; i < size.length; i++) {
      dataPos = dataPos + temp * dataCoords[i];
      temp = temp * getDataSize()[i];
    }
    return getState(dataPos, fieldCoords);
  }

  @Override
  public synchronized void setSize(int... size) {

    this.size = size;

    // int[] dataSize = new int[size.length];
    for (int i = 0; i < size.length; i++) {
      // Precision???
      getDataSize()[i] =
          (int) java.lang.Math.ceil((float) size[i] / (float) expansion[i]);
    }

    // initData();
    int length = 1;
    for (int i = 0; i < size.length; i++) {
      length = length * getDataSize()[i];
    }
    setData(new long[length]);

    // TODO: Add backup and copy

  }

  public int[] getExpansion() {
    return expansion;
  }

  /**
   * Calculate hash code.
   */
  private synchronized void calculateHashCode() {
    // TODO: Hash Code
    hashCode = 7;

    hashCode = 31 * hashCode + 2; // dimension
    hashCode = 31 * hashCode + size[0]; // width
    hashCode = 31 * hashCode + size[1]; // width

    // now for all cells add state
    for (int j = 0; j < size[1]; j++) {
      for (int i = 0; i < size[0]; i++) {
        hashCode = 31 * hashCode + getState(new int[] { i, j }); // store cell
        // state in hash
      }
    }
  }

  @Override
  public synchronized int hashCode() {
    calculateHashCode();
    return hashCode;
  }

  /*
   * private void initData() { int length = 1; for(int i=0; i<size.length; i++)
   * { length = length*dataSize[i]; } long defState = 0; //TODO: set default
   * state!!! Arrays.fill(data, defState); }
   */

  public void setNumberOfStates(int stateNum) {
    int dimensions = size.length;
    int[] dataSize2 = new int[dimensions];

    // the number of bits per cell used in each field
    int bits2 = 0;
    // the expansion of each field
    int[] expansion2 = new int[dimensions];

    // calculate number of bits per cell:
    for (int i = 0; i < stateNum; i++) {
      // equal of double to int???? hm...
      // should work now...
      if (integerPow(2, i) >= stateNum) {
        bits2 = i;
      }
    }
    // estimate the semantics of each data-field
    // each field has an expansion in each dimension
    // trying to math a cube etc.
    int end = 0;
    while (end < dimensions) {
      end = 0;
      for (int i = 0; i < dimensions; i++) {
        expansion2[i]++;
        int prod = 1;
        // get the volume of the expansion
        for (int j = 0; j < dimensions; j++) {
          prod = prod * expansion2[j];
        }
        // if the expansion is to big
        if (prod > getSystemBits() / bits2) {
          expansion2[i]--;
          end++;
        }
      }
    }

    // Create the (bit) mask:
    long mask2 = 0x0000000000000001;
    for (int i = 1; i < bits2; i++) {
      mask2 = mask2 << 1;
      mask2 = mask2 | 0x0000000000000001;
    }

    for (int i = 0; i < dimensions; i++) {
      // Precision???
      dataSize2[i] =
          (int) java.lang.Math.ceil((float) size[i] / (float) expansion2[i]);
    }

    // initData();
    int length = 1;
    for (int i = 0; i < dimensions; i++) {
      length = length * dataSize2[i];
    }
    long[] data2 = new long[length];

    this.setData(getData().clone());

    int temp = 1;
    for (int i = 0; i < dimensions; i++) {
      temp = temp * size[i];
    }

    for (int[] coords : getCoordID()) {
      int[] dataCoords2 = new int[dimensions];
      int[] fieldCoords2 = new int[dimensions];
      for (int i = 0; i < dimensions; i++) {
        dataCoords2[i] = coords[i] / expansion2[i];
        fieldCoords2[i] =
            (coords[i] - expansion2[i] * (coords[i] / expansion2[i]));
      }
      int dataPos2 = 0;
      int temp2 = 1;
      for (int i = 0; i < dimensions; i++) {
        dataPos2 = dataPos2 + temp2 * dataCoords2[i];
        temp2 = temp2 * dataSize2[i];
      }

      int fieldPos2 = 0;
      temp2 = 1;
      for (int i = 0; i < dimensions; i++) {
        fieldPos2 = fieldPos2 + temp2 * coords[i];
        temp2 = temp2 * expansion2[i];
      }
      long field2 = data2[dataPos2];
      field2 = field2 & ~(mask2 << fieldPos2 * bits2);
      data2[dataPos2] =
          field2 | (((long) getState(coords)) << fieldPos2 * bits2);
    }

    expansion = expansion2;
    setMask(mask2);
    setBits(bits2);
    setDataSize(dataSize2);
    setMask(mask2);
    setData(data2);
    numberOfStates = integerPow(2, getBits());
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
    // Position in the field
    int fieldPos = 0;
    int temp = 1;
    for (int i = 0; i < size.length; i++) {
      fieldPos = fieldPos + temp * coords[i];
      temp = temp * expansion[i];
    }
    /*
     * Done in cons // mask for the information long mask = 0; for(int i=0;
     * i<bits; i++) { mask = mask << 1; mask++; }
     */
    long field = getData()[dataPos];
    // Clearing current state at this position
    field = field & ~(getMask() << fieldPos * getBits());
    // Setting new state
    getData()[dataPos] = field | (((long) state) << fieldPos * getBits());
  }

  /**
   * Gets the state in the field using coords (field internal coords!)
   * 
   * @param field
   * @param coords1
   * @param state
   */
  private int getState(int dataPos, int[] coords) {

    // Position in the field
    int fieldPos = 0;
    int temp = 1;
    for (int i = 0; i < size.length; i++) {
      fieldPos = fieldPos + temp * coords[i];
      temp = temp * expansion[i];
    }
    /*
     * Done in Constructor // mask for the information long mask = 0; for(int
     * i=0; i<bits; i++) { mask = mask << 1; mask++; }
     */
    // Retrieving information, shifting, returning
    return (int) ((getData()[dataPos] & (getMask() << fieldPos * getBits())) >> fieldPos
        * getBits());
  }

  /**
   * java.lang.Math.pow() with int's
   * 
   * @param base
   * @param exp
   * @return int
   */
  private int integerPow(int base, int exp) {
    int result = 1;
    for (int i = 0; i < exp; i++) {
      result = result * base;
    }
    return result;
  }

  /**
   * Somewhat like positive Rest (Modulo) BUT should be MUCH faster than pMod in
   * our cases here (only int operations)
   * 
   * @param value
   * @param base
   * @return value mod base
   */
  protected int normalize(int value, int base) {
    int result = value;
    while (result < 0) {
      result += base;
    }
    while (result >= base) {
      result -= base;
    }
    return value;
  }

  /**
   * @return the coordID
   */
  protected final int[][] getCoordID() {
    return coordID;
  }

  /**
   * @param coordID
   *          the coordID to set
   */
  protected final void setCoordID(int[][] coordID) {
    this.coordID = coordID;
  }

  /**
   * @return the numberOfStates
   */
  protected final int getNumberOfStates() {
    return numberOfStates;
  }

  /**
   * @return the dataSize
   */
  protected final int[] getDataSize() {
    return dataSize;
  }

  /**
   * @param dataSize
   *          the dataSize to set
   */
  protected final void setDataSize(int[] dataSize) {
    this.dataSize = dataSize;
  }

  /**
   * @return the data
   */
  protected final long[] getData() {
    return data;
  }

  /**
   * @param data
   *          the data to set
   */
  protected final void setData(long[] data) {
    this.data = data;
  }

  /**
   * @return the mask
   */
  protected final long getMask() {
    return mask;
  }

  /**
   * @param mask
   *          the mask to set
   */
  protected final void setMask(long mask) {
    this.mask = mask;
  }

  /**
   * @return the bits
   */
  protected final int getBits() {
    return bits;
  }

  /**
   * @param bits
   *          the bits to set
   */
  protected final void setBits(int bits) {
    this.bits = bits;
  }

  /**
   * Get the value of the systembits.
   * 
   * @return the systembits
   */
  private static int getSystemBits() {
    return SYSTEM_BITS;
  }
}
