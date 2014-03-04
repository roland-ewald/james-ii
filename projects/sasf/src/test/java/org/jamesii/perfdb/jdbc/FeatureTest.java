/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.FeatureType;
import org.jamesii.perfdb.recording.features.plugintype.FeatureExtractorFactory;

/**
 * Test for database entities of type {@link FeatureType}.
 * 
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FeatureTest extends PerfDBTest<FeatureType> {

  /**
   * Dummy name.
   */
  final String featureName = "TestFeature";

  /**
   * Dummy description.
   */
  final String featureDesc = "This is a test feature";

  /**
   * Dummy alternative name.
   */
  final String testName = "AnotherTestName";

  /**
   * Dummy alternative description.
   */
  final String testDesc = "Another description";

  public FeatureTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(FeatureType instance) {
    instance.setName(testName);
    instance.setDescription(testDesc);
  }

  @Override
  public void compareRetrievedEntity(FeatureType original, FeatureType retrieved) {
    assertEquals(original.getName(), retrieved.getName());
    assertEquals(original.getDescription(), retrieved.getDescription());
  }

  @Override
  public FeatureType getEntity() {
    return new FeatureType(featureName, featureDesc,
        FeatureExtractorFactory.class);
  }

}
