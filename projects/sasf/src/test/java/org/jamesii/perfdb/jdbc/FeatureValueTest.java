/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.Feature;
import org.jamesii.perfdb.jdbc.FeatureType;
import org.jamesii.perfdb.jdbc.SimulationProblem;

/**
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FeatureValueTest extends PerfDBTest<Feature> {

  public FeatureValueTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(Feature instance) {
  }

  @Override
  public void compareRetrievedEntity(Feature expected, Feature actual) {
    assertEquals(expected.getApplication().getProblemInstance()
        .getProblemDefinition(), actual.getApplication().getProblemInstance()
        .getProblemDefinition());
  }

  @Override
  public Feature getEntity() throws Exception {

    SimulationProblem sp = (new SimProblemTest()).getConnectedEntity();
    sp.create();
    FeatureType f = (new FeatureTest()).getConnectedEntity();
    f.create();
    return new Feature(sp, f, null);
  }

}
