/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Required meta-data for predictor generators, regarding performance tuples.
 * For example, the categories of nominal attributes have to be known
 * beforehand. An analysis of the data to find out all possibilities within the
 * predictor generator is problematic for two reasons: first, each predictor has
 * to do this, so this causes a large overhead. Second, not all predictors might
 * be exposed to instances with all attributes in their training data (e.g.,
 * when using bagging or cross-validation), which might lead to errors.
 * 
 * @author Roland Ewald
 * 
 */
public class PerfTupleMetaData implements Serializable {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -2168519012998206549L;

  /**
   * Map from nominal configuration/feature attribute names to a set of the
   * values they can have.
   */
  private final Map<String, Set<String>> nominalAttribs =
      new HashMap<>();

  /**
   * Set of numeric attributes that are defined.
   */
  private final Set<String> numericAttribs = new HashSet<>();

  /** The flag that defines whether performance shall be maximised or not. */
  private boolean maximizePerformance = false;

  /**
   * Checks all attributes in map for meta-data extraction.
   * 
   * @param attribMap
   *          name->value map of an instance's attributes
   */
  protected void checkForAttributes(Map<String, ?> attribMap) {
    for (Entry<String, ?> entry : attribMap.entrySet()) {
      String name = entry.getKey();
      // Is it a nominal attribute?
      if (entry.getValue() instanceof String) {
        if (!nominalAttribs.containsKey(name)) {
          nominalAttribs.put(name, new HashSet<String>());
        }
        nominalAttribs.get(name).add((String) entry.getValue());
      }
      // Else: numeric attribute
      else {
        numericAttribs.add(name);
      }
    }
  }

  /**
   * Creates a list containing all attribute names in alphabetical order.
   * 
   * @return list of attribute names in alphabetical order
   */
  public List<String> getSortedAttributeList() {
    List<String> attributes = new ArrayList<>();
    attributes.addAll(nominalAttribs.keySet());
    attributes.addAll(numericAttribs);
    Collections.sort(attributes);
    return attributes;
  }

  public Set<String> getNumericAttribs() {
    return new HashSet<>(numericAttribs);
  }

  public int getNumOfAttributes() {
    return nominalAttribs.size() + numericAttribs.size();
  }

  public void setNumericAttribs(Set<String> numericAttribs) {
    this.numericAttribs.clear();
    this.numericAttribs.addAll(numericAttribs);
  }

  public Map<String, Set<String>> getNominalAttribs() {
    return new HashMap<>(nominalAttribs);
  }

  public void setNominalAttribs(Map<String, Set<String>> nominalAttribs) {
    this.nominalAttribs.clear();
    this.nominalAttribs.putAll(nominalAttribs);
  }

  public boolean isMaximizePerformance() {
    return maximizePerformance;
  }

  public void setMaximizePerformance(boolean maximizePerformance) {
    this.maximizePerformance = maximizePerformance;
  }

}
