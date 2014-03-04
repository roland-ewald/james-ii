/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import static org.jamesii.simspex.util.DatabaseUtils.convertModelTypeToSchemeType;


import java.net.URI;

import org.jamesii.perfdb.jdbc.BenchmarkModel;
import org.jamesii.simspex.util.BenchmarkModelType;


/**
 * Test for benchmark model database entities.
 * 
 * @author Roland Ewald
 */
@Deprecated
public class BenchmarkModelTest extends PerfDBTest<BenchmarkModel> {

  /**
   * URI of the dummy model.
   */
  URI modelURI = new URI("http://test");

  /**
   * Name of the dummy model.
   */
  final String modelName = "TestModel";

  /**
   * Description of the dummy model.
   */
  final String modelDesc = "This is a test model";

  /**
   * Alternative dummy model name.
   */
  final String testName = "AnotherTestName";

  /**
   * Alternative scheme type.
   */
  final String schemeType =
      convertModelTypeToSchemeType(BenchmarkModelType.SYNTHETIC);

  /**
   * Dummy scheme test type.
   */
  final String testType =
      convertModelTypeToSchemeType(BenchmarkModelType.COMMON);

  public BenchmarkModelTest() throws Exception {
    super();
  }

  @Override
  public BenchmarkModel getEntity() {
    return new BenchmarkModel(modelURI, modelName, schemeType, modelDesc);
  }

  @Override
  public void changeEntity(BenchmarkModel instance) {
    instance.setName(testName);
    instance.setType(testType);
  }

  @Override
  public void compareRetrievedEntity(BenchmarkModel original,
      BenchmarkModel retrieved) {
    assertEquals(original.getName(), retrieved.getName());
  }

}
