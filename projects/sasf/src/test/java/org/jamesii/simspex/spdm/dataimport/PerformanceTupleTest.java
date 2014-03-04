/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport;

import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;


/**
 * Tests for {@link PerformanceTuple} serialization.
 * 
 * @author Roland Ewald
 * 
 */
public class PerformanceTupleTest extends
    SimpleSerializationTest<PerformanceTuple> {

  /** The test feature. */
  public static final String TEST_FEATURE = "TestFeature";

  /** The value of the test feature. */
  public static final Double TEST_FEATURE_VALUE = 0.33;

  /** The test configuration. */
  public static final String TEST_CONFIG = "TestConfig";

  /** The value of the test configuration. */
  public static final String TEST_CONFIG_VALUE = "TestConfigVal";

  /** Performance tuple to be tested. */
  PerformanceTuple perfTuple;

  @Override
  public void setUp() {
    Features features = new Features();
    features.put(TEST_FEATURE, TEST_FEATURE_VALUE);
    Configuration configuration = new Configuration(null);
    configuration.put(TEST_CONFIG, TEST_CONFIG_VALUE);
    perfTuple =
        new PerformanceTuple(features, configuration,
            TotalRuntimePerfMeasurerFactory.class, 10);
  }

  /**
   * Test constructor.
   */
  public void testConstructor() {
    assertEquals(TotalRuntimePerfMeasurerFactory.class,
        perfTuple.getPerfMeasureFactory());
  }

  @Override
  public void assertEquality(PerformanceTuple original,
      PerformanceTuple deserialisedVersion) {
    assertEquals(original, deserialisedVersion);
  }

  @Override
  public PerformanceTuple getTestObject() throws Exception {
    return perfTuple;
  }

}
