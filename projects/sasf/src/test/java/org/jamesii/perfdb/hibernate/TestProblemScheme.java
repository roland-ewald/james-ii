/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import static org.jamesii.simspex.util.DatabaseUtils.convertModelTypeToSchemeType;

import java.net.URI;

import org.hibernate.Session;
import org.jamesii.perfdb.hibernate.ProblemScheme;
import org.jamesii.simspex.util.BenchmarkModelType;


/**
 * Tests the implementation of a {@link ProblemScheme}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestProblemScheme extends TestHibernateEntity<ProblemScheme> {

  /** The test uri. */
  public static URI testURI = null;

  /**
   * The type of the benchmark model to be tested (additional domain-specific
   * information).
   */
  public static final String testType =
      convertModelTypeToSchemeType(BenchmarkModelType.SYNTHETIC);

  /**
   * Instantiates a new test problem scheme.
   */
  public TestProblemScheme() {
    super(true);
  }

  /**
   * Instantiates a new test problem scheme.
   * 
   * @param s
   *          the session
   */
  public TestProblemScheme(Session s) {
    super(s);
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    testURI = new URI("http://this.is.a/test");
  }

  @Override
  public ProblemScheme getEntity(String name) {
    ProblemScheme bm = new ProblemScheme();
    bm.setName(name);
    bm.setUri(testURI);
    bm.setType(testType);
    return bm;
  }

  @Override
  public void changeEntity(ProblemScheme entity) {
    entity.setName("Entity777");
    entity.setDescription("no description");
    entity.setType(convertModelTypeToSchemeType(BenchmarkModelType.COMMON));
  }

  @Override
  public void checkEquality(ProblemScheme entity) {
    assertEquals("testEntity0", entity.getName());
    assertEquals(testURI, entity.getUri());
    assertEquals(testType, entity.getType());
  }
}