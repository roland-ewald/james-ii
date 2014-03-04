/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;


/**
 * This class handles a configuration and its current performance (w.r.t. some
 * feature set). It can be repeatedly used by performance predictors.
 * 
 * @author Roland Ewald
 * @author Kaustav Saha
 * 
 */
public class ConfigurationEntry {
  static {
    SerialisationUtils.addDelegateForConstructor(ConfigurationEntry.class,
        new IConstructorParameterProvider<ConfigurationEntry>() {
          @Override
          public Object[] getParameters(ConfigurationEntry oldInstance) {
            return new Object[] { oldInstance.getConfig() };
          }
        });
  }

  /** Associated configuration. */
  private final Configuration config;

  /** Currently predicted performance. */
  private Double performance = null;

  /** Features last used for predictions. */
  private Features features = null;

  public ConfigurationEntry(Configuration configuration) {
    config = configuration;
  }

  public Features getFeatures() {
    return features;
  }

  public void setFeatures(Features features) {
    this.features = features;
  }

  public void setPerformance(Double performance) {
    this.performance = performance;
  }

  public Double getPerformance() {
    return performance;
  }

  public Configuration getConfig() {
    return config;
  }
}