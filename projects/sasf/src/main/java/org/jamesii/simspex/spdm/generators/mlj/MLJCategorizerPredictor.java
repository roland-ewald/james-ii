/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.generators.mlj;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.util.misc.Pair;

import shared.AttrInfo;
import shared.Categorizer;
import shared.Instance;
import shared.Schema;

/**
 * 
 * Implementation of {@link org.jamesii.asf.spdm.generators.IPerfComparisonPredictor} for
 * categorisers built upon MLJ.
 * 
 * @author Roland Ewald
 * 
 */
public class MLJCategorizerPredictor implements IPerformancePredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = -2565783923899499436L;

  /** Categoriser to be used for prediction. */
  private Categorizer categorizer;

  /** Attributes information. */
  private final Map<String, Pair<AttrInfo, Integer>> attributes =
      new HashMap<>();

  /** MLJ meta-information. */
  private final Schema schema;

  /**
   * Default constructor.
   * 
   * @param nbc
   *          Naive Bayesian categoriser
   * @param attribMap
   *          mapping name -> (attribute info, position of attribute in schema)
   * @param schem
   *          the MLJ schema (for instance creation)
   */
  public MLJCategorizerPredictor(Categorizer nbc,
      Map<String, Pair<AttrInfo, Integer>> attribMap, Schema schem) {
    categorizer = nbc;
    attributes.putAll(attribMap);
    schema = schem;
  }

  /**
   * Predicts performance of a single entry.
   * 
   * @param entry
   *          the configuration entry whose performance shall be predicted
   * @return estimated performance (class)
   */
  @Override
  public double predictPerformance(Features features, Configuration config) {

    Instance instance = new Instance(schema);

    Set<String> setAttribs = new HashSet<>();
    MLJUtils.setAttributeValues(instance, attributes, config);
    setAttribs.addAll(config.keySet());

    MLJUtils.setAttributeValues(instance, attributes, features);
    setAttribs.addAll(features.keySet());

    // Care for missing values
    Set<String> missingAttribs = new HashSet<>(attributes.keySet());
    missingAttribs.removeAll(setAttribs);
    for (String missingAttrib : missingAttribs) {
      MLJUtils.setMissingAttribute(instance, attributes.get(missingAttrib));
    }

    return categorizer.categorize(instance).Category();
  }

}
