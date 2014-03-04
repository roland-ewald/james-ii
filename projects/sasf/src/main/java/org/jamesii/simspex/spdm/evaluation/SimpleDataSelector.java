/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple data selector. Filters data from one fixed index to another.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleDataSelector implements IDataSelector {

  /** Start index (inclusive). */
  private final int start;

  /** End index (exclusive). */
  private final int end;

  /**
   * Default constructor.
   * 
   * @param startIndex
   *          start index (inclusive)
   * @param endIndex
   *          end index (exclusive)
   */
  public SimpleDataSelector(int startIndex, int endIndex) {
    start = startIndex;
    end = endIndex;
  }

  @Override
  public <X> List<X> selectData(List<X> input) {
    ArrayList<X> result = new ArrayList<>();
    for (int i = 0; i < input.size(); i++) {
      if (i >= start && i < end) {
        result.add(input.get(i));
      }
    }
    return result;
  }

}
