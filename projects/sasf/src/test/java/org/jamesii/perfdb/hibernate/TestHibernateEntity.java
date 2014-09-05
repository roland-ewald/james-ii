/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.hibernate;

import java.util.List;

import junit.framework.TestCase;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * General class to test hibernate entities.
 * 
 * @param <X>
 *          the generic type
 * @author Roland Ewald
 */
public abstract class TestHibernateEntity<X> extends TestCase {

  /** The name of the test entity. */
  static final String testName = "testEntity";

  /** The session factory. */
  SessionFactory sessionFactory = null;

  /** The session. */
  protected Session session = null;

  /** The flag whether to clean the database first. */
  boolean cleanDB = true;

  /** The flag whether the test has already been started. */
  boolean started = false;

  /** The configuration. */
  Configuration configuration = null;

  /**
   * Instantiates a new test hibernate entity.
   */
  public TestHibernateEntity() {
    this(true);
  }

  /**
   * Instantiates a new test hibernate entity.
   * 
   * @param clean
   *          the flag whether to clean the database
   */
  public TestHibernateEntity(boolean clean) {
    cleanDB = clean;
  }

  /**
   * Instantiates a new test hibernate entity.
   * 
   * @param s
   *          the session
   */
  public TestHibernateEntity(Session s) {
    this(false);
    session = s;
  }

  @Override
  public void setUp() throws Exception {
    super.setUp();
    if (session == null) {
      configuration = new Configuration();
      for (String configLocation : getConfigLocations()) {
        configuration.configure(configLocation);
      }
      sessionFactory = configuration.buildSessionFactory();
      sessionFactory = configuration.buildSessionFactory();
      session = sessionFactory.openSession();
    }
    if (cleanDB && !started) {
      SchemaExport export = new SchemaExport(configuration);
      export.create(false, true);
      started = true;
    }
  }

  /**
   * Gets the location of the Hibernate configuration.
   * 
   * @return the location of the Hibernate configuration
   */
  protected String[] getConfigLocations() {
    return new String[] { PerformanceDatabase.HIBERNATE_CONFIG };
  }

  @Override
  public void tearDown() throws Exception {
    super.tearDown();
    session.flush();
    session.clear();
    session.close();
    sessionFactory.close();
    session = null;
  }

  /**
   * Test entity creation.
   * 
   * @throws Exception
   *           the exception
   */
  public void testEntityCreation() throws Exception {
    for (int i = 0; i < 3; i++) {
      createEntity(getEntity(testName + i));
      System.err.println("Created entity:" + testName + i);
    }
    load();
    lookUpSearchAndDestroy();
  }

  /**
   * Creates the entity.
   * 
   * @param entity
   *          the entity
   */
  public void createEntity(X entity) {
    Transaction transaction = session.beginTransaction();
    try {
      session.save(entity);
      transaction.commit();
      System.err.println("Saved " + entity.getClass());
    } catch (HibernateException ex) {
      if (transaction != null) {
        transaction.rollback();
      }
      throw ex;
    }
    if (session != null) {
      finish(session);
    }
  }

  /**
   * Finish.
   * 
   * @param s
   *          the session
   */
  private void finish(Session s) {
    s.flush();
    s.clear();
  }

  /**
   * Creates the entity.
   * 
   * @param name
   *          the name
   * @return the x
   * @throws Exception
   *           the exception
   */
  public X createEntity(String name) throws Exception {
    X entity = getEntity(name);
    createEntity(entity);
    return entity;
  }

  /**
   * Load entity.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  public void load() throws Exception {
    try {
      checkEquality((X) session.load(getEntity("").getClass(), 1L));
    } finally {
      if (session != null && session.isConnected()) {
        finish(session);
      }
    }
  }

  /**
   * Look up search and destroy.
   * 
   * @throws Exception
   *           the exception
   */
  @SuppressWarnings("unchecked")
  // Type safeness is provided by Hibernate
  public void lookUpSearchAndDestroy() throws Exception {
    try {
      Class<?> entiyClass = getEntity("").getClass();

      Query query =
          session.createQuery("from " + entiyClass.getName() + " where id='1'");
      List<X> result = query.list();
      assertEquals(1, result.size());
      X te = result.get(0);
      checkEquality(te);

      Transaction t = session.beginTransaction();
      changeEntity(te);
      t.commit();

      t = session.beginTransaction();
      te = (X) session.load(entiyClass, 2L);
      session.delete(te);
      t.commit();

      te = (X) session.get(entiyClass, 2L);
      assertNull(te);
    } finally {
      session.flush();
      session.clear();
    }
  }

  /**
   * Gets the entity.
   * 
   * @param name
   *          the name
   * @return the entity
   * @throws Exception
   *           the exception
   */
  protected abstract X getEntity(String name) throws Exception;

  /**
   * Check equality.
   * 
   * @param entity
   *          the entity
   */
  protected abstract void checkEquality(X entity);

  /**
   * Change entity.
   * 
   * @param entity
   *          the entity
   * @throws Exception
   *           the exception
   */
  protected abstract void changeEntity(X entity) throws Exception;
}