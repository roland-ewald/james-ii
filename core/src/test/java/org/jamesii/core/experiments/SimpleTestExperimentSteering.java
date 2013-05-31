/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments;

import java.util.List;

import org.jamesii.core.experiments.replication.MaximizingReplicationCriterionFactory;
import org.jamesii.core.experiments.replication.RepNumberCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * Tests interface for simple experiment steerer.
 * 
 * @author Roland Ewald
 */
public class SimpleTestExperimentSteering extends
    TestExperimentSteering<SimpleExperimentSteerer> {

  @Override
  protected SimpleExperimentSteerer createExperimentSteerer() {
    return new SimpleExperimentSteerer(NUM_VAR_ASSIGNMENTS, NUM_REPLICATIONS);
  }

  @Override
  protected Class<SimpleExperimentSteerer> getSteererClass() {
    return SimpleExperimentSteerer.class;
  }

  /**
   * Test steerer exp var.
   */
  public void testSteererExpVar() {
    ExperimentVariables expVariables =
        getTestExperiment().getExperimentVariables();
    expVariables.init(expVariables);
    List<TestExecSetup> paramSetups = getSetups(expVariables);
    int firstHalf = paramSetups.size() >> 1;
    for (int i = 0; i < firstHalf; i++) {
      assertEquals(
          steerer1,
          paramSetups.get(i).getFirstValue()
              .get("steererVar_" + expSteererVariable.hashCode()));
    }
    for (int i = firstHalf; i < paramSetups.size(); i++) {
      assertEquals(
          steerer2,
          paramSetups.get(i).getFirstValue()
              .get("steererVar_" + expSteererVariable.hashCode()));
    }
  }

  public void testReplicationConfiguration() {

    List<TaskConfiguration> taskConfigs = getTestExperimentTaskConfigs();
    ParameterizedFactory<RepCriterionFactory> prf =
        taskConfigs.get(0).getReplicationCriterionFactory();
    assertEquals(MaximizingReplicationCriterionFactory.class, prf.getFactory()
        .getClass());

    boolean suitableFactoryFound = false;
    List<ParameterizedFactory<RepCriterionFactory>> repCriterionFactories =
        prf.getParameter()
            .getSubBlockValue(
                MaximizingReplicationCriterionFactory.REPLICATION_CRITERION_FACTORIES);
    for (ParameterizedFactory<RepCriterionFactory> repFac : repCriterionFactories) {
      if (repFac.getFactory().getClass()
          .equals(RepNumberCriterionFactory.class)) {
        int definedReps =
            repFac.getParameter().getSubBlockValue(
                RepNumberCriterionFactory.NUM_REPS);
        if (NUM_REPLICATIONS == definedReps) {
          suitableFactoryFound = true;
          break;
        }

      }
    }

    assertTrue("Expected " + RepNumberCriterionFactory.class
        + " that sets replcations to " + NUM_REPLICATIONS, suitableFactoryFound);
  }
}
