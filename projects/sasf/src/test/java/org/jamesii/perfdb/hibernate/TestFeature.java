/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import org.hibernate.Session;
import org.jamesii.perfdb.hibernate.FeatureType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;

/**
 * Tests {@link FeatureType}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestFeature extends TestHibernateEntity<FeatureType> {

  /** The feature extractor factory to be used. */
  static final Class<? extends FeatureExtractorFactory> FEAT_EXTRACTOR_FACTORY =
      FeatureExtractorFactory.class;

  /**
   * Instantiates a new test feature.
   */
  public TestFeature() {
    super(true);
  }

  /**
   * Instantiates a new test feature.
   * 
   * @param s
   *          the session
   */
  public TestFeature(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(FeatureType entity) {
    entity.setFeatureExtractorFactory(null);
  }

  @Override
  protected void checkEquality(FeatureType entity) {
    assertEquals(FEAT_EXTRACTOR_FACTORY.getClass(), entity
        .getFeatureExtractorFactory().getClass());
  }

  @Override
  protected FeatureType getEntity(String name) {
    FeatureType feature = new FeatureType();
    feature.setName(name);
    feature.setFeatureExtractorFactory(FEAT_EXTRACTOR_FACTORY);
    return feature;
  }

}
