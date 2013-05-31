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
 * The Class HistoryItemComparatorByCounter.
 * 
 * This class compares two HistoryItems by number of usages. The most often used
 * element will be first.
 * 
 * @author Enrico Seib
 */
final class HistoryItemComparatorByCounter implements Comparator<Object>,
    Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -389137613247053698L;

  /**
   * if counter of arg0 > counter of arg1 --> return 1 if counter of arg1 >
   * counter of arg0 --> return -1 else (same) --> return 0
   */

  @Override
  public int compare(Object arg0, Object arg1) {
    if (!(arg0 instanceof HistoryItem)) {
      throw new ClassCastException();
    }
    if (!(arg1 instanceof HistoryItem)) {
      throw new ClassCastException();
    }

    if (((HistoryItem) arg0).getCounter() > ((HistoryItem) arg1).getCounter()) {
      return -1;
    } else if (((HistoryItem) arg1).getCounter() > ((HistoryItem) arg0)
        .getCounter()) {
      return 1;
    } else {
      return 0;
    }
  }
}
