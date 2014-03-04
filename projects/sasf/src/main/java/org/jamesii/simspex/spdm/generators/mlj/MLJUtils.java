/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;


import java.util.Map;
import java.util.Map.Entry;

import org.jamesii.core.util.misc.Pair;

import shared.AttrInfo;
import shared.AttrValue;
import shared.Instance;
import shared.NominalAttrInfo;
import shared.RealAttrInfo;

/**
 * Auxiliary functions for MLJ integration.
 * 
 * @author Roland Ewald
 * 
 */
final class MLJUtils {

  /**
   * Should not be instantiated.
   */
  private MLJUtils() {
  }

  /** Default category for missing nominal attributes. */
  static final String MISSING_NOM_VAL = "none";

  /** Default value for missing numeric attributes. */
  static final double MISSING_NUM_VAL = -1.0;

  /**
   * Sets attribute values for an instance.
   * 
   * @param instance
   *          instance whose attributes shall be set
   * @param attrMap
   *          attribute map with meta information
   * @param valueMap
   *          map attribute values to be set
   */
  static void setAttributeValues(Instance instance,
      Map<String, Pair<AttrInfo, Integer>> attrMap, Map<String, ?> valueMap) {

    for (Entry<String, ?> confEntry : valueMap.entrySet()) {

      Pair<AttrInfo, Integer> attrInfos = attrMap.get(confEntry.getKey());
      if (attrInfos == null) {
        throw new IllegalArgumentException("Attribute '" + confEntry.getKey()
            + "' could not be found in attrbute map!");
      }

      AttrInfo attrInfo = attrInfos.getFirstValue();
      AttrValue val = new AttrValue();

      if (attrInfo instanceof NominalAttrInfo) {
        attrInfo.set_nominal_string(val, confEntry.getValue().toString(), true);
      } else if (attrInfo instanceof RealAttrInfo) {
        attrInfo.set_real_val(val,
            ((Number) confEntry.getValue()).doubleValue());
      }

      instance.values[attrInfos.getSecondValue()] = val;
    }
  }

  /**
   * Assigns default 'missing' attribute of given name to given instance.
   * 
   * @param instance
   *          the instance where the attribute is missing
   * @param attrInfos
   *          attribute information required for setting the value
   */
  static void setMissingAttribute(Instance instance,
      Pair<AttrInfo, Integer> attrInfos) {
    AttrInfo info = attrInfos.getFirstValue();
    int attribIndex = attrInfos.getSecondValue();
    AttrValue val = new AttrValue();

    if (info instanceof NominalAttrInfo) {
      info.set_nominal_string(val, MISSING_NOM_VAL, true);
    } else if (info instanceof RealAttrInfo) {
      info.set_real_val(val, MISSING_NUM_VAL);
    }

    instance.values[attribIndex] = val;
  }
}
