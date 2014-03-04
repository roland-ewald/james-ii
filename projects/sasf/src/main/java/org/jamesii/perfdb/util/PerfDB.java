/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.util;


import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.Factory;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;

/**
 * Class that contains static auxiliary functions for tasks related to the
 * {@link IPerformanceDatabase}.
 * 
 * @author Roland Ewald
 */
public final class PerfDB {

  /**
   * Should not be instantiated.
   */
  private PerfDB() {
  }

  /**
   * Reads all available feature types from the {@link IPerformanceDatabase} and
   * stored them into a map feature extractor factory name => feature type.
   * 
   * @param perfDB
   *          the current {@link IPerformanceDatabase}
   * 
   * @return map factory name => feature type
   * 
   * @throws Exception
   *           if database look-up fails
   */
  public static Map<String, IFeatureType> getFeatureMap(
      IPerformanceDatabase perfDB) {
    List<IFeatureType> features = perfDB.getAllFeatureTypes();
    Map<String, IFeatureType> featureMap = new HashMap<>();
    for (IFeatureType feat : features) {
      featureMap
          .put(feat.getFeatureExtractorFactory().getCanonicalName(), feat);
    }
    return featureMap;
  }

  /**
   * Synchronises the database with the registry (only one-way synchronisation,
   * performance measurements are not removed from the database when they are no
   * longer registered).
   * 
   * @param perfTypes
   *          performance types already registered within the data base
   * @return list of new performance type factories
   */
  public static Collection<PerformanceMeasurerFactory> getNewPerfTypeFactories(
      Collection<IPerformanceType> perfTypes) {

    Map<String, PerformanceMeasurerFactory> factoryMap =
        createFactoryMap(PerformanceMeasurerFactory.class);

    for (IPerformanceType perfMeasure : perfTypes) {
      factoryMap.remove(perfMeasure.getPerformanceMeasurerFactory()
          .getCanonicalName());
    }

    return factoryMap.values();
  }

  /**
   * Synchronises the database with the registry (only one-way synchronisation,
   * performance measurements are not removed from the database when they are no
   * longer registered).
   * 
   * @param featureTypes
   *          feature types already registered within the data base
   * @return list of new feature extractor factories
   */
  public static Collection<FeatureExtractorFactory> getNewFeatureTypeFactories(
      Collection<IFeatureType> featureTypes) {

    Map<String, FeatureExtractorFactory> factoryMap =
        createFactoryMap(FeatureExtractorFactory.class);

    for (IFeatureType featureType : featureTypes) {
      factoryMap.remove(featureType.getFeatureExtractorFactory()
          .getCanonicalName());
    }

    return factoryMap.values();
  }

  /**
   * Creates a map factory class name => factory.
   * 
   * @param <F>
   *          the factory type
   * @param factoryClass
   *          the factory class (base factory required)
   * @return the map factory class name => factory
   */
  @SuppressWarnings("unchecked")
  // Cast is ensured by registry
  private static <F extends Factory<?>> Map<String, F> createFactoryMap(
      Class<F> factoryClass) {

    List<F> perfTypeFactories =
        SimSystem.getRegistry().getFactories(factoryClass);
    Map<String, F> factoryMap = new HashMap<>();

    for (Factory<?> factory : perfTypeFactories) {
      factoryMap.put(factory.getClass().getCanonicalName(), (F) factory);
    }
    return factoryMap;
  }
}
