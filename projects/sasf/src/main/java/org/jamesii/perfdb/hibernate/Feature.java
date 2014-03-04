/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import java.io.Serializable;
import java.util.Map;

import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.entities.IFeatureType;

/**
 * Hibernate implementation of a set of feature values.
 * 
 * @author Roland Ewald
 * 
 */
@SuppressWarnings("unused")
// Hibernate uses private methods
public class Feature extends IDEntity implements IFeature {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6291557238688186402L;

  /** Reference to the feature that was extracted. */
  private FeatureType feature = null;

  /** Reference to the application for which the feature was extracted. */
  private Application application = null;

  /** The extracted value. */
  private Map<String, Serializable> value = null;

  /**
   * Empty constructor for beans compliance.
   */
  public Feature() {
  }

  /**
   * Instantiates a new feature entity.
   * 
   * @param featureType
   *          the feature type
   * @param app
   *          the application
   * @param val
   *          the feature values
   */
  public Feature(FeatureType featureType, Application app,
      Map<String, Serializable> val) {
    feature = featureType;
    application = app;
    value = val;
  }

  @Override
  public IFeatureType getFeatureType() {
    return feature;
  }

  @Override
  public IApplication getApplication() {
    return application;
  }

  @Override
  public Map<String, Serializable> getValue() {
    return value;
  }

  @Override
  public void setFeatureType(IFeatureType feat) {
    PerformanceDatabase.checkForHibernateEntities(new Object[] { feat },
        FeatureType.class);
    feature = (FeatureType) feat; // NOSONAR:{checked_above}
  }

  @Override
  public void setApplication(IApplication app) {
    application = (Application) app;
  }

  @Override
  public void setValue(Map<String, Serializable> val) {
    value = val;
  }

  private Application getApp() {
    return application; // NOSONAR:{used_by_hibernate}
  }

  private void setApp(Application app) {
    application = app; // NOSONAR:{used_by_hibernate}
  }

  private FeatureType getFeatType() {
    return feature; // NOSONAR:{used_by_hibernate}
  }

  private void setFeatType(FeatureType feat) {
    feature = feat; // NOSONAR:{used_by_hibernate}
  }

}
