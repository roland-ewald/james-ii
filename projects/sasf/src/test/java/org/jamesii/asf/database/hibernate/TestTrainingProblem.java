/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.database.hibernate;


import org.hibernate.Session;
import org.jamesii.asf.database.hibernate.TrainingProblem;
import org.jamesii.perfdb.hibernate.TestProblemDefinition;

/**
 * Tests {@link TrainingProblem}.
 * 
 * @author Roland Ewald
 */
@Deprecated
public class TestTrainingProblem extends TestSelectionDBEntity<TrainingProblem> {

  static final int NUM_OF_CONFIGS = 12;

  static final int NUM_OF_FEATURES = 34;

  public TestTrainingProblem() {
    super(true);
  }

  public TestTrainingProblem(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(TrainingProblem entity) {
    entity.setNumOfConfigs(56);
    entity.setNumOfFeatures(312);
  }

  @Override
  protected void checkEquality(TrainingProblem entity) {
    assertEquals(NUM_OF_CONFIGS, entity.getNumOfConfigs());
    assertEquals(NUM_OF_FEATURES, entity.getNumOfFeatures());
    assertNotNull(entity.getSelector());
    assertNotNull(entity.getSimulationProblem());
  }

  @Override
  protected TrainingProblem getEntity(String name) throws Exception {
    TrainingProblem tp = new TrainingProblem();
    tp.setNumOfConfigs(NUM_OF_CONFIGS);
    tp.setNumOfFeatures(NUM_OF_FEATURES);
    TestSelector ts = new TestSelector(session);
    tp.setSelector(ts.createEntity("selector for training problem " + name));
    tp.setSimulationProblem((new TestProblemDefinition(session))
        .createEntity("simulation problem for training problem " + name));
    return tp;
  }

}
