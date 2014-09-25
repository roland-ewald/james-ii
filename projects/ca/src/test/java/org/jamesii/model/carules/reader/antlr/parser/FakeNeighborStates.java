/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.cacore.neighborhood.INeighborhood;

/**
 * @author Stefan Rybacki
 * 
 */
class FakeNeighborStates implements INeighborStates<Integer> {
  private Map<Integer, Integer> cache = new HashMap<>();

  public FakeNeighborStates(List<Integer> states) {
    for (Integer state : states) {
      Integer count = cache.remove(state);
      if (count == null) {
        count = 0;
      }
      count++;
      cache.put(state, count);
    }
  }

  @Override
  public Integer getState(int... coord) {
    throw new RuntimeException(
        "Not supported by this simulator, use getCountOf only.");
  }

  @Override
  public int getCountOf(Integer state) {
    Integer res = cache.get(state);
    if (res == null) {
      return 0;
    }
    return res;
  }

  @Override
  public int getCountOf(Integer state, INeighborhood in) {
    throw new UnsupportedOperationException();
  }

}
