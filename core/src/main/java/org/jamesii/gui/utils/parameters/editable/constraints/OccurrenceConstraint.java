/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.editable.constraints;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.util.IConstraint;
import org.jamesii.gui.utils.parameters.editable.IEditable;

/**
 * This class controls the occurrence constraints (min/maxOccurrence) of
 * elements within a parameter set. Until now, only sequences and elements in
 * them are supported.
 * 
 * Created on June 24, 2004
 * 
 * @author Roland Ewald
 */
public class OccurrenceConstraint implements IConstraint<List<IEditable<?>>> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2675239502467680028L;

  /** Occurrence list. */
  private Map<String, OccurrenceInfo> occurrenceList = new Hashtable<>();

  /**
   * Adds the occurrence info.
   * 
   * @param paramName
   *          the parameter name
   * @param info
   *          the info
   */
  public void addOccurenceInfo(String paramName, OccurrenceInfo info) {
    occurrenceList.put(paramName, info);
  }

  @Override
  public IConstraint<List<IEditable<?>>> getCopy() {
    OccurrenceConstraint copy = new OccurrenceConstraint();
    for (Entry<String, OccurrenceInfo> infoEntry : occurrenceList.entrySet()) {
      OccurrenceInfo info = infoEntry.getValue();
      copy.addOccurenceInfo(infoEntry.getKey(),
          new OccurrenceInfo(info.getMin(), info.getMax()));
    }
    return copy;
  }

  /**
   * Gets the max occurrence.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return maximal occurrence number (Integer.MAX_VALUE if unbounded, null if
   *         element does not have an entry)
   */
  public Integer getMaxOccurrence(IEditable<?> parameter) {
    OccurrenceInfo oInfo = occurrenceList.get(parameter.getName());
    if (oInfo == null) {
      return null;
    }
    return oInfo.getMax();
  }

  /**
   * Gets the min occurrence.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return minimal occurrence number (-1 if unbounded, null ifelement does not
   *         have an entry)
   */
  public Integer getMinOccurrence(IEditable<?> parameter) {
    OccurrenceInfo oInfo = occurrenceList.get(parameter.getName());
    if (oInfo == null) {
      return null;
    }
    return oInfo.getMin();
  }

  /**
   * Determines, if parameter count is constant.
   * 
   * @param parameter
   *          the parameter
   * 
   * @return true, if parameter has a constant occurrence, otherwise false
   */
  public boolean hasConstantOccurrence(IEditable<?> parameter) {
    OccurrenceInfo oInfo = occurrenceList.get(parameter.getName());
    if (oInfo == null) {
      return false;
    }
    return (oInfo.getMin() == oInfo.getMax());
  }

  @Override
  public boolean isFulfilled(List<IEditable<?>> value) {

    ArrayList<IEditable<?>> params = new ArrayList<>();

    for (IEditable<?> editable : value) {
      params.add(editable);
    }

    Hashtable<String, Integer> occurrences = new Hashtable<>();

    for (IEditable<?> param : params) {
      String name = param.getName();
      Integer count = occurrences.get(name);
      if (count == null) {
        occurrences.put(name, Integer.valueOf(1));
      } else {
        occurrences.put(name, Integer.valueOf(count.intValue() + 1));
      }
    }

    // Check with old occurrences
    for (Map.Entry<String, OccurrenceInfo> entry : occurrenceList.entrySet()) {

      String name = entry.getKey();
      OccurrenceInfo oInfo = entry.getValue();
      Integer count = occurrences.get(name);

      if (oInfo == null || (oInfo.getMin() > 0 && count == null)) {
        return false;
      }

      if (count == null) {
        continue;
      }

      if (oInfo.getMin() > count || oInfo.getMax() < count) {
        return false;
      }
    }

    return true;
  }

  /**
   * Removes the occurence info.
   * 
   * @param paramName
   *          the param name
   */
  public void removeOccurenceInfo(String paramName) {
    occurrenceList.remove(paramName);
  }

}
