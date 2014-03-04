/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb;


import java.io.Serializable;
import java.util.List;
import java.util.Map;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;
import org.jamesii.perfdb.recording.features.plugintype.IFeatureExtractor;

/**
 * Interface for feature data base.
 * 
 * @author Roland Ewald
 * 
 */
public interface IFeatureDatabase {

  /**
   * Get feature associated with a certain extractor factory.
   * 
   * @param factory
   *          the extractor factory
   * @return the feature type if DB-lookup failed
   * @throws Exception
   * @throws ConstraintException
   *           if constraints were violated (e.g., if two features with the same
   *           extractor exist)
   */
  IFeatureType getFeatureForFactory(
      Class<? extends FeatureExtractorFactory> factory);

  /**
   * Get all feature types available in the database.
   * 
   * @return list of feature types
   * @throws Exception
   *           if look-up fails
   */
  List<IFeatureType> getAllFeatureTypes();

  /**
   * Create new feature type.
   * 
   * @param featureName
   *          name of the feature
   * @param featureDescription
   *          description of the feature
   * @param featureExtractor
   *          extractor factory class
   * @return newly created and stored feature
   * @throws Exception
   *           if DB-update goes wrong
   */
  IFeatureType newFeatureType(String featureName, String featureDescription,
      Class<? extends FeatureExtractorFactory> featureExtractor);

  /**
   * All features of a simulation problem.
   * 
   * @param app
   *          the application
   * @return list of all features associated with that problem
   * @throws Exception
   */
  List<IFeature> getAllFeatures(IApplication app);

  /**
   * Retrieve feature for given simulation problem and feature type.
   * 
   * @param app
   *          the application
   * @param featureType
   *          feature type
   * @return the corresponding feature value, or null if not exists
   * @throws Exception
   *           if DB-lookup fails
   * @throws ConstraintException
   *           if constraints were violated (e.g., two features of that type
   *           exist for the same simulation problem)
   */
  IFeature getFeature(IApplication app, IFeatureType featureType);

  /**
   * Saves given feature if it does not already exist.
   * 
   * @param app
   *          the application
   * @param feature
   *          the type of the feature
   * @param extractor
   *          the extractor
   * @return (maybe newly created) feature
   * @throws Exception
   *           if DB lookup fails etc.
   */
  IFeature newFeature(IApplication app, IFeatureType feature,
      IFeatureExtractor<ParameterBlock> extractor);

  /**
   * Saves given feature for the application.
   * 
   * @param app
   *          the application
   * @param featureType
   *          the feature type
   * @param featureValues
   *          the feature values
   * @return the database entity representing the feature
   */
  IFeature newFeature(IApplication app, IFeatureType featureType,
      Map<String, Serializable> featureValues);

}
