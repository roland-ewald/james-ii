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
 * 
 * The Class HistoryItemComparatorByName.
 * 
 * This class compares two HistoryItems by the alphabetical order of there
 * values.
 * 
 * @author Enrico Seib
 * 
 */

final class HistoryItemComparatorByName implements Comparator<Object>,
    Serializable {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -876056429745417929L;

  @Override
  public int compare(Object arg0, Object arg1) {

    if (!(arg0 instanceof HistoryItem)) {
      throw new ClassCastException();
    }
    if (!(arg1 instanceof HistoryItem)) {
      throw new ClassCastException();
    }

    return ((HistoryItem) arg0).getValue().compareTo(
        ((HistoryItem) arg1).getValue());
  }
}