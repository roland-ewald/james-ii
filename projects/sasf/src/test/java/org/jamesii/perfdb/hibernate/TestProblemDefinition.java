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
import java.util.Set;

import org.hibernate.Session;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulationrun.stoppolicy.SimTimeStopFactory;
import org.jamesii.perfdb.entities.IProblemInstance;
import org.jamesii.perfdb.hibernate.ProblemDefinition;
import org.jamesii.perfdb.hibernate.ProblemScheme;
import org.jamesii.simspex.util.SimulationProblemDefinition;


/**
 * Tests {@link ProblemDefinition}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestProblemDefinition extends
    TestHibernateEntity<ProblemDefinition> {

  /** The value of the first test parameter. */
  static final Integer PARAMETER_1 = 1;

  /** The value of the second test parameter. */
  static final Double PARAMETER_2 = 2.0;

  /** The name of the first test parameter. */
  static final String PARAMETER_1_NAME = "param1";

  /** The name of the second test parameter. */
  static final String PARAMETER_2_NAME = "param2";

  /** The name of the test model. */
  static final String MODEL_NAME = "problemTestModel";

  /** The test stop time. */
  static final Double TEST_STOP_TIME = 123.45;

  /** The Constant testStopTimePolicyFactory. */
  static final Class<? extends ComputationTaskStopPolicyFactory> testStopTimePolicyFactory =
      SimTimeStopFactory.class;

  /** The problem scheme. */
  ProblemScheme problemScheme;

  /** The instances. */
  Set<IProblemInstance> instances;

  /**
   * Instantiates a new test problem definition.
   */
  public TestProblemDefinition() {
    super(true);
  }

  /**
   * Instantiates a new test problem definition.
   * 
   * @param s
   *          the session
   */
  public TestProblemDefinition(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(ProblemDefinition entity) {
    entity.getSchemeParameters().remove(PARAMETER_2_NAME);
  }

  @Override
  protected void checkEquality(ProblemDefinition entity) {
    Map<String, Serializable> params = entity.getSchemeParameters();
    assertEquals(2, params.size());
    assertTrue(params.containsKey(PARAMETER_1_NAME));
    assertTrue(params.containsKey(PARAMETER_2_NAME));
    assertEquals(PARAMETER_1, params.get(PARAMETER_1_NAME));
    assertEquals(PARAMETER_2, params.get(PARAMETER_2_NAME));
    assertEquals(MODEL_NAME, entity.getProblemScheme().getName());
    assertEquals(TEST_STOP_TIME,
        SimulationProblemDefinition.getSimStopTime(entity));
    assertEquals(testStopTimePolicyFactory,
        SimulationProblemDefinition.getStopFactoryClass(entity));
  }

  @Override
  protected ProblemDefinition getEntity(String name) throws Exception {
    ProblemDefinition simProb = new ProblemDefinition();
    TestProblemScheme tbm = new TestProblemScheme(session);
    problemScheme = tbm.createEntity(MODEL_NAME);
    simProb.setProblemScheme(problemScheme);
    HashMap<String, Serializable> params = new HashMap<>();
    params.put(PARAMETER_1_NAME, PARAMETER_1);
    params.put(PARAMETER_2_NAME, PARAMETER_2);
    simProb.setSchemeParameters(params);
    simProb.setDefinitionParameters(SimulationProblemDefinition
        .getDefinitionParameters(testStopTimePolicyFactory, new ParameterBlock(
            TEST_STOP_TIME, SimTimeStopFactory.SIMEND)));
    return simProb;
  }

}