/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import org.jamesii.perfdb.hibernate.Feature;
import org.jamesii.perfdb.hibernate.FeatureType;

/**
 * Tests {@link Feature} entities.
 * 
 * @author Roland Ewald
 * 
 */
public class TestFeatureValue extends TestHibernateEntity<Feature> {

  /** The feature values. */
  static final Map<String, Serializable> vals =
      new HashMap<>();

  /** The another feat type. */
  FeatureType anotherFeatType;

  /**
   * Instantiates a new test feature value.
   */
  public TestFeatureValue() {
    super(true);
  }

  /**
   * Instantiates a new test feature value.
   * 
   * @param cleanDB
   *          the flag whether to clean the database before testing
   */
  public TestFeatureValue(boolean cleanDB) {
    super(cleanDB);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    vals.clear();
    for (int i = 0; i < 5; i++) {
      vals.put("key_" + i, i);
    }
  }

  @Override
  protected void changeEntity(Feature entity) throws Exception {
    entity.setFeatureType(anotherFeatType);
  }

  @Override
  protected void checkEquality(Feature entity) {
    assertNotNull(entity.getApplication());
    assertNotNull(entity.getFeatureType());
    Map<String, Serializable> eVals = entity.getValue();
    assertEquals(vals.size(), eVals.size());
    for (int i = 0; i < 5; i++) {
      assertEquals(vals.get("key_" + i), entity.getValue().get("key_" + i));
    }
  }

  @Override
  protected Feature getEntity(String name) throws Exception {
    Feature fVal = new Feature();
    TestApplication testApp = new TestApplication(session);
    fVal.setApplication(testApp.createEntity("application for feat value test"));
    TestFeature tf = new TestFeature(session);
    fVal.setFeatureType(tf.createEntity("testFeature for feat value test"));
    anotherFeatType = tf.createEntity("Another testFeature.");
    fVal.setValue(vals);
    return fVal;
  }

}
