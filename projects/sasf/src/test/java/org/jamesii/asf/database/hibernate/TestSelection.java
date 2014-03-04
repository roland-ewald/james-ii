/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;


import java.util.HashSet;
import java.util.Set;

import org.hibernate.Session;
import org.jamesii.asf.database.hibernate.Selection;
import org.jamesii.perfdb.entities.IFeature;
import org.jamesii.perfdb.hibernate.TestFeatureValue;
import org.jamesii.perfdb.hibernate.TestRuntimeConfiguration;

/**
 * Tests database for managing {@link Selection} objects.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class TestSelection extends TestSelectionDBEntity<Selection> {

  /** The number of features. */
  static final int NUM_OF_FEATURES = 2;

  public TestSelection() {
    super();
  }

  public TestSelection(Session session) {
    super(session);
  }

  @Override
  protected void changeEntity(Selection entity) {
    entity.setFeatures(new HashSet<IFeature>());
  }

  @Override
  protected void checkEquality(Selection entity) {
    assertNotNull(entity.getRuntimeConfiguration());
    assertNotNull(entity.getSelector());
    assertNotNull(entity.getFeatures());
    assertEquals(NUM_OF_FEATURES, entity.getFeatures().size());
  }

  @Override
  protected Selection getEntity(String name) throws Exception {

    Selection sel = new Selection();
    TestRuntimeConfiguration trc = new TestRuntimeConfiguration(session);
    sel.setRuntimeConfiguration(trc
        .createEntity("runtime config for selection " + name));

    TestSelector ts = new TestSelector(session);
    sel.setSelector(ts.createEntity("selectorfor selection " + name));

    Set<IFeature> features = new HashSet<>();
    TestFeatureValue tfv = new TestFeatureValue(false);
    tfv.setUp();
    for (int i = 0; i < NUM_OF_FEATURES; i++) {
      features.add(tfv.createEntity("feature value for selection " + name));
    }
    tfv.tearDown();

    sel.setFeatures(features);

    return sel;
  }

}
