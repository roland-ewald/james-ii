/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.BenchmarkModel;
import org.jamesii.perfdb.jdbc.SimulationProblem;

/**
 * @author Roland Ewald
 * 
 */
@Deprecated
public class SimProblemTest extends PerfDBTest<SimulationProblem> {

  public SimProblemTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(SimulationProblem instance) {
  }

  @Override
  public void compareRetrievedEntity(SimulationProblem original,
      SimulationProblem retrieved) {
  }

  @Override
  public SimulationProblem getEntity() throws Exception {
    BenchmarkModel bmModel = (new BenchmarkModelTest()).getConnectedEntity();
    bmModel.create();
    return new SimulationProblem(bmModel, null);
  }

}
