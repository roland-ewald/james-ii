/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.features;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.NoFactoryFoundException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.recording.features.plugintype.AbstractFeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.IFeatureExtractor;
import org.jamesii.perfdb.util.PerfDB;

/**
 * Generic algorithm to extract any kinds of features from the simulation
 * problems in the performance database.
 * 
 * @author Roland Ewald
 * 
 */
public class FeatureExtraction {

  /** The performance database to use for extraction. */
  private IPerformanceDatabase perfDB;

  /**
   * Default constructor.
   * 
   * @param performanceDatabase
   *          database with open connection
   */
  public FeatureExtraction(IPerformanceDatabase performanceDatabase) {
    perfDB = performanceDatabase;
  }

  /**
   * Synchronizes feature extractors with the feature type database.
   * 
   * @throws Exception
   *           if synchronization goes wrong (on the DB side)
   */
  public static void refreshFeatureExtractors(IPerformanceDatabase perfDB) {

    List<FeatureExtractorFactory> exFactories =
        SimSystem.getRegistry().getFactories(FeatureExtractorFactory.class);

    for (FeatureExtractorFactory exFactory : exFactories) {
      IFeatureType feat = null;

      try {
        feat = perfDB.getFeatureForFactory(exFactory.getClass());
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE,
            "Error retrieving feature extractor factory.", ex);
        continue;
      }

      // Add feature if necessary
      if (feat == null) {
        perfDB.newFeatureType(exFactory.getFeatureName(),
            exFactory.getFeatureDescription(), exFactory.getClass());
      }
    }
  }

  /**
   * Extracts all features.
   */
  public void extractFeatures() {
    Map<String, IFeatureType> featureMap = PerfDB.getFeatureMap(perfDB);
    List<IProblemDefinition> problemDefinitions =
        perfDB.getAllProblemDefinitions();
    for (IProblemDefinition problemDefinition : problemDefinitions) {
      for (IApplication app : perfDB.getAllApplications(problemDefinition)) {
        extractFeatures(app, perfDB, featureMap);
      }
    }
  }

  /**
   * Extracts the features of a given {@link IApplication}. Features that
   * already have been extracted will not be updated or removed, they will just
   * be ignored.
   * 
   * @param application
   *          the given application
   * @param perfDB
   *          the current {@link IPerformanceDatabase}
   * @param featureMap
   *          a map factory name -> feature type
   */
  public static void extractFeatures(IApplication app,
      IPerformanceDatabase perfDB, Map<String, IFeatureType> featureMap) {
    extractFeatures(app, perfDB, featureMap,
        getFeatureExtractorsForApplication(app));
  }

  /**
   * Get {@link FeatureExtractorFactory} instances that are eligible for the
   * {@link IProblemDefinition} at hand.
   * 
   * @param app
   *          the application at hand
   * @return list of {@link FeatureExtractorFactory} instances
   */
  public static List<FeatureExtractorFactory> getFeatureExtractorsForApplication(
      IApplication app) {
    ParameterBlock fep =
        new ParameterBlock(app, FeatureExtractorFactory.PROBLEM_REPRESENTATION);
    List<FeatureExtractorFactory> feFactories = null;
    try {
      feFactories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFeatureExtractorFactory.class, fep);
    } catch (NoFactoryFoundException nffex) {
      return new ArrayList<>();
    }
    return feFactories;
  }

  /**
   * Same as
   * {@link FeatureExtraction#extractFeatures(IProblemDefinition, IPerformanceDatabase, Map)}
   * , but allows to hand over the {@link FeatureExtractorFactory} instances to
   * be applied.
   * 
   * @param app
   *          the application
   * @param perfDB
   *          the performance database
   * @param featureMap
   *          the map factory name -> feature type
   * @param feFactories
   *          the {@link FeatureExtractorFactory} instances to be used
   */
  public static void extractFeatures(IApplication app,
      IPerformanceDatabase perfDB, Map<String, IFeatureType> featureMap,
      List<FeatureExtractorFactory> feFactories) {
    for (FeatureExtractorFactory feFactory : feFactories) {
      IFeatureType availableFeature =
          featureMap.get(feFactory.getClass().getCanonicalName());
      try {
        @SuppressWarnings("unchecked")
        IFeatureExtractor<ParameterBlock> extractor =
            (IFeatureExtractor<ParameterBlock>) feFactory.create(null);
        perfDB.newFeature(app, availableFeature, extractor);
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE,
            "Storing new feature to the performance database failed.", ex);
      }
    }
  }
}
