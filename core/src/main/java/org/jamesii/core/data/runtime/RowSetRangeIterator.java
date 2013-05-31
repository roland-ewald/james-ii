/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

/**
 * Very simple iterator for the {@link RowSetImpl}. This allows concurrent reads
 * on the row set. Will not detect modifications of the row set! Please take
 * care that no one is going to modify the row set in between.
 * 
 * @author Jan Himmelspach
 * 
 */
public class RowSetRangeIterator {

  /** The row set to work on */
  private RowSetImpl rsi;

  /**
   * the internal cursor which has not to be equal to the cursor of the row set
   * implementation
   */
  private int cursor = -1;

  /** the start of the range to iterate over */
  private int start = -1;

  /** the end of the range to iterator over */
  private int stop = -1;

  /** auto detected search direction */
  private boolean backward = false;

  /**
   * Create a new row set range iterator. Iterates over the range passed (from
   * start to stop). If start is greater than stop it will iterate backwards,
   * otherwise forwards.
   * 
   * @param rsi
   *          RowSet to work on
   * @param start
   *          the start index
   * @param stop
   *          the stop index
   */
  public RowSetRangeIterator(RowSetImpl rsi, int start, int stop) {
    this.rsi = rsi;
    cursor = start;
    this.start = start;
    this.stop = stop;
    if (start > stop) {
      backward = true;
    }
  }

  /**
   * Return true if there are more items to iterate over.
   * 
   * @return true if there are more items.
   */
  public boolean hasNext() {
    if (backward) {
      return cursor > stop;
    }
    return cursor < stop;
  }

  /**
   * Step to the next row set entry. Don't use this method if {@link #hasNext()}
   * returns false!
   * 
   * @return the values of the current row
   */
  public Object[] next() {
    if (backward) {
      cursor--;
    } else {
      cursor++;
    }
    return rsi.getContent().get(cursor);
  }

}
