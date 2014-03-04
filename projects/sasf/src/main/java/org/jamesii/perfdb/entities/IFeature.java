/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.entities;

import java.io.Serializable;
import java.util.Map;

/**
 * Common interface for all problem features. A problem feature value associates
 * a simulation problem with concrete values regarding a certain feature type.
 * 
 * @author Roland Ewald
 * 
 */
public interface IFeature extends IIDEntity {

  /**
   * Gets the feature type.
   * 
   * @return the feature type
   */
  IFeatureType getFeatureType();

  /**
   * Sets the feature type.
   * 
   * @param feature
   *          the new feature type
   */
  void setFeatureType(IFeatureType feature);

  /**
   * Gets the application that exhibits this feature.
   * 
   * @return the application
   */
  IApplication getApplication();

  /**
   * Sets the application that exhibits this feature.
   * 
   * @param app
   *          the new application
   */
  void setApplication(IApplication app);

  /**
   * Gets the feature value.
   * 
   * @return the value
   */
  Map<String, Serializable> getValue();

  /**
   * Sets the feature value.
   * 
   * @param value
   *          the value
   */
  void setValue(Map<String, Serializable> value);

}
