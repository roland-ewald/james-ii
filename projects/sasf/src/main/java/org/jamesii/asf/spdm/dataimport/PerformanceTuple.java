/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.dataimport;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Represents performance tuple.
 * 
 * @author Roland Ewald
 */
public class PerformanceTuple implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4837302222649355167L;

  /** Kind of measured performance. */
  private Class<? extends PerformanceMeasurerFactory> perfMeasureFactory;

  /** Measured performance. */
  private double performance;

  /** Configuration for the measured performance. */
  private Configuration configuration;

  /** Features of the simulation problem. */
  private Features features;

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public PerformanceTuple() {
  }

  /**
   * Default constructor.
   * 
   * @param feats
   *          features of the simulation problem
   * @param config
   *          mapping of attribute names to the configuration aspects, e.g.
   *          'model' -> 'TIS' , 'model_size'->'600', 'ssa'->'DRM',
   *          'rng'->'DefJava'
   * @param perfMeasureFac
   *          class of the performance measure factory
   * @param perf
   *          real-valued performance result (negative number indicates
   *          measurement error)
   */
  public PerformanceTuple(Features feats, Configuration config,
      Class<? extends PerformanceMeasurerFactory> perfMeasureFac, double perf) {
    features = feats;
    configuration = config;
    perfMeasureFactory = perfMeasureFac;
    performance = perf;
  }

  /**
   * Gets the perf measure factory.
   * 
   * @return the perf measure factory
   */
  public Class<? extends PerformanceMeasurerFactory> getPerfMeasureFactory() {
    return perfMeasureFactory;
  }

  /**
   * Gets the performance.
   * 
   * @return the performance
   */
  public double getPerformance() {
    return performance;
  }

  /**
   * Gets the configuration.
   * 
   * @return the configuration
   */
  public Configuration getConfiguration() {
    return configuration;
  }

  /**
   * Gets the features.
   * 
   * @return the features
   */
  public Features getFeatures() {
    return features;
  }

  @Override
  public String toString() {

    StringBuilder result = new StringBuilder("(");

    for (Object o : features.values()) {
      result.append(o);
      result.append(',');
    }

    for (Object o : configuration.values()) {
      result.append(o);
      result.append(',');
    }

    result.append(perfMeasureFactory + "," + performance + ")");
    return result.toString();
  }

  /**
   * Creates new map with all attributes (regarding configuration and problem
   * features).
   * 
   * @return map with all attributes held within this performance tuple
   */
  public Map<String, Object> getAllAttributes() {
    Map<String, Object> result = new HashMap<>();
    result.putAll(configuration);
    result.putAll(features);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof PerformanceTuple)) {
      return false;
    }
    PerformanceTuple compare = (PerformanceTuple) obj;
    if (Double.compare(performance, compare.getPerformance()) != 0
        || !features.equals(compare.getFeatures())
        || !configuration.equals(compare.getConfiguration())
        || !perfMeasureFactory.equals(compare.getPerfMeasureFactory())) {
      return false;
    }
    return true;
  }

  @Override
  public int hashCode() {
    return features.hashCode()
        + configuration.hashCode()
        + (perfMeasureFactory == null ? 0 : perfMeasureFactory.getName()
            .hashCode()) + Double.valueOf(performance).hashCode();
  }

  /**
   * Sets the perf measure factory.
   * 
   * @param perfMeasureFactory
   *          the new perf measure factory
   */
  public void setPerfMeasureFactory(
      Class<? extends PerformanceMeasurerFactory> perfMeasureFactory) {
    this.perfMeasureFactory = perfMeasureFactory;
  }

  /**
   * Sets the performance.
   * 
   * @param performance
   *          the new performance
   */
  public void setPerformance(double performance) {
    this.performance = performance;
  }

  /**
   * Sets the configuration.
   * 
   * @param configuration
   *          the new configuration
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }

  /**
   * Sets the features.
   * 
   * @param features
   *          the new features
   */
  public void setFeatures(Features features) {
    this.features = features;
  }
}
