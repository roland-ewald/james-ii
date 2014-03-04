/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;


import org.hibernate.Session;
import org.jamesii.core.math.random.generators.java.JavaRandomGeneratorFactory;
import org.jamesii.perfdb.hibernate.ProblemDefinition;
import org.jamesii.perfdb.hibernate.ProblemInstance;

/**
 * Test for {@link ProblemInstance}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestProblemInstance extends TestHibernateEntity<ProblemInstance> {

  /** The Constant RAND_SEED. */
  static final long RAND_SEED = 12345L;

  /** The Constant RNG_FACTORY_NAME. */
  static final String RNG_FACTORY_NAME = JavaRandomGeneratorFactory.class
      .getName();

  /** The problem definition. */
  ProblemDefinition problemDefinition;

  /**
   * Instantiates a new test problem instance.
   */
  public TestProblemInstance() {
    super(true);
  }

  /**
   * Instantiates a new test problem instance.
   * 
   * @param s
   *          the session
   */
  public TestProblemInstance(Session s) {
    super(s);
  }

  @Override
  protected void changeEntity(ProblemInstance entity) {
    entity.setRandomSeed(6789L);
  }

  @Override
  protected void checkEquality(ProblemInstance entity) {
    assertEquals(RAND_SEED, entity.getRandomSeed());
    assertEquals(problemDefinition.getSchemeParameters(), entity
        .getProblemDefinition().getSchemeParameters());
    assertEquals(RNG_FACTORY_NAME, entity.getRNGFactoryName());
  }

  @Override
  protected ProblemInstance getEntity(String name) throws Exception {
    ProblemInstance probInst = new ProblemInstance();
    TestProblemDefinition tsp = new TestProblemDefinition(session);
    problemDefinition = tsp.createEntity(name);
    probInst.setProblemDefinition(problemDefinition);
    probInst.setRandomSeed(RAND_SEED);
    probInst.setRNGFactoryName(RNG_FACTORY_NAME);
    return probInst;
  }

}
