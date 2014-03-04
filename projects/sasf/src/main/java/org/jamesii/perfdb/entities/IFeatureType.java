/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;

/**
 * Common interface for all feature types. A feature type represents a certain
 * *kind* of problem feature, e.g. the size of the model or the number of
 * available CPU cores. Features are extracted, feature extractors are normal
 * plug-ins.
 * 
 * @author Roland Ewald
 * 
 */
public interface IFeatureType extends INamedDBEntity {

  /**
   * Gets the feature extractor factory.
   * 
   * @return the feature extractor factory
   */
  Class<? extends FeatureExtractorFactory> getFeatureExtractorFactory();

  /**
   * Sets the feature extractor factory.
   * 
   * @param featureExtractorFactory
   *          the new feature extractor factory
   */
  void setFeatureExtractorFactory(
      Class<? extends FeatureExtractorFactory> featureExtractorFactory);
}
