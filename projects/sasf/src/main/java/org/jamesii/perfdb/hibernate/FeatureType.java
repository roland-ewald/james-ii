/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import org.jamesii.perfdb.entities.IFeatureType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;

/**
 * Hibernate implementation of features.
 * 
 * @author Roland Ewald
 * 
 */
public class FeatureType extends NamedDBEntity implements IFeatureType {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2691336636645171126L;

  /**
   * Class of the feature extractor that is able to extract features of this
   * type.
   */
  private Class<? extends FeatureExtractorFactory> featureExtractorFactory;

  /**
   * Empty constructor for beans compliance.
   */
  public FeatureType() {
  }

  /**
   * Instantiates a new feature type.
   * 
   * @param typeName
   *          the name of the feature type
   * @param typeDesc
   *          the description of the feature type
   * @param factoryClass
   *          the factory class
   */
  public FeatureType(String typeName, String typeDesc,
      Class<? extends FeatureExtractorFactory> factoryClass) {
    super(typeName, typeDesc);
    featureExtractorFactory = factoryClass;
  }

  @Override
  public Class<? extends FeatureExtractorFactory> getFeatureExtractorFactory() {
    return featureExtractorFactory;
  }

  @Override
  public void setFeatureExtractorFactory(
      Class<? extends FeatureExtractorFactory> feFactory) {
    featureExtractorFactory = feFactory;
  }

}
