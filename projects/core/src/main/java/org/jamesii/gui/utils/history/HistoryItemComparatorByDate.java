/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import java.io.Serializable;
import java.util.Comparator;

/**
 * The Class HistoryItemComparatorByDate.
 * 
 * This class compares two HistoryItems by there last usage. Latest used item
 * will be first.
 * 
 * @author Enrico Seib
 */
final class HistoryItemComparatorByDate implements Comparator<Object>,
    Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8225902251365547654L;

  /**
   * if date of arg0 before date of arg1 --> return -1; <br>
   * if date of arg0 after date of arg1 --> return 1 <br>
   * else (same) --> return 0
   */

  @Override
  public int compare(Object arg0, Object arg1) {

    if (!(arg0 instanceof HistoryItem)) {
      throw new ClassCastException();
    }
    if (!(arg1 instanceof HistoryItem)) {
      throw new ClassCastException();
    }

    if (((HistoryItem) arg0).getLastUsage().before(
        ((HistoryItem) arg1).getLastUsage())) {
      return 1;
    } else if (((HistoryItem) arg0).getLastUsage().after(
        ((HistoryItem) arg1).getLastUsage())) {
      return -1;
    } else {
      return 0;
    }
  }
}
