/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.integration;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.generators.IPerformancePredictor;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.misc.Strings;


/**
 * This implements an optimal performance prediction scheme, to compare with
 * automatically generated ones.
 * 
 * @author Roland Ewald
 * 
 */
public class OptimalBogusSimulatorPerformancePredictor implements
    IPerformancePredictor {

  /** Serialisation ID. */
  private static final long serialVersionUID = -4469733064154772370L;

  @Override
  public double predictPerformance(Features features, Configuration config) {
    String processorFactory =
        config.get("&&null&&" + ProcessorFactory.class.getName()).toString();
    String featureName = "";
    try {
      featureName = Strings.dispClassName(Class.forName(processorFactory));
    } catch (ClassNotFoundException e) {
      e.printStackTrace();
    }
    return features.get(featureName) == null ? 0 : (Integer) features
        .get(featureName);
  }

}
