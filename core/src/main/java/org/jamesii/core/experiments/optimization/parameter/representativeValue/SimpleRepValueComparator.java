/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.optimization.parameter.representativeValue;

import java.util.Map;

/**
 * Simple comparator that decides one map to be smaller/larger than the other if
 * *all* its elements are smaller/larger than those of the other. Otherwise, 0
 * is returned.
 * 
 * In case map sizes don't match or one of the maps is empty, 0 is returned as
 * well.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleRepValueComparator implements
    IRepresentativeValuesComparator {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3297775781665391626L;

  @Override
  public int compare(Map<String, Double> map1, Map<String, Double> map2) {

    if (map1.size() != map2.size() || map1.size() == 0 || map2.size() == 0) {
      return 0;
    }

    int counter = 0;
    Integer compResult = null;
    for (Map.Entry<String, Double> entry : map1.entrySet()) {
      counter++;
      if (counter > map2.size()) {
        return 0;
      }
      Double map1Value = entry.getValue();
      Double map2Value = map2.get(entry.getKey());
      if (compResult == null) {
        compResult = map1Value.compareTo(map2Value);
      } else if (Math.signum(compResult) != Math.signum(map1Value
          .compareTo(map2Value))) {
        return 0;
      }
    }

    return compResult;
  }

}
