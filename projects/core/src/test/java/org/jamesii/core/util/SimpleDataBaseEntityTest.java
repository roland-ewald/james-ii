/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.util.database.SimpleDataBaseEntity;

import junit.framework.TestCase;

/**
 * Standard test for all database entities. Tests all primitive functions.
 * 
 * @param <X>
 *          the database-stored entity to be tested
 * @author Roland Ewald
 */
public abstract class SimpleDataBaseEntityTest<X extends SimpleDataBaseEntity<X>>
    extends TestCase {

  /** Entity instance for the tests. */
  protected X entity;

  /**
   * Instantiates a new simple data base entity test.
   */
  public SimpleDataBaseEntityTest() {
    SimSystem.getRegistry();
  }

  /**
   * Change entity.
   * 
   * @param instance
   *          the instance
   * 
   * @throws Exception
   *           the exception
   */
  public abstract void changeEntity(X instance) throws Exception;

  /**
   * Compare retrieved entity.
   * 
   * @param expected
   *          the expected
   * @param actual
   *          the actual
   */
  public abstract void compareRetrievedEntity(X expected, X actual);

  /**
   * Gets the connection.
   * 
   * @return the connection
   * 
   * @throws Exception
   *           the exception
   */
  public abstract Connection getConnection() throws Exception;

  /**
   * Gets the entity.
   * 
   * @return the entity
   * 
   * @throws Exception
   *           the exception
   */
  public abstract X getEntity() throws Exception;

  @Override
  protected void setUp() throws Exception {
    if (isDeactivated()) {
      return;
    }
    entity = getConnectedEntity();
  }

  /**
   * Gets the connected entity.
   * 
   * @return the connected entity
   * 
   * @throws Exception
   *           the exception
   */
  public X getConnectedEntity() throws Exception {
    X ent = getEntity();
    ent.connect(getConnection());
    return ent;
  }

  /**
   * Test get entities.
   * 
   * @throws Exception
   *           the exception
   */
  public void testGetEntities() throws Exception {
    if (isDeactivated()) {
      return;
    }
    entity.create();
    entity.create();
    List<X> entities = entity.getEntities(null);
    assertTrue(entities.size() > 0);
  }

  /**
   * Test model update.
   * 
   * @throws Exception
   *           the exception
   */
  public void testModelUpdate() throws Exception {
    if (isDeactivated()) {
      return;
    }
    entity.create();
    changeEntity(entity);
    entity.update();
    X newEntity = entity.getEntity(entity.getID());
    compareRetrievedEntity(entity, newEntity);
    assertEquals(entity.getID(), newEntity.getID());
  }

  /**
   * Test multi inserts.
   * 
   * @throws Exception
   *           the exception
   */
  public void testMultiInserts() throws Exception {
    if (isDeactivated()) {
      return;
    }

    List<X> entityList = new ArrayList<>();

    int numOfEntities = entity.getEntities(null).size();

    entityList.add(entity);
    entityList.add(entity.copy());
    entityList.add(entity.copy());

    entity.create(entityList);
    assertEquals(numOfEntities + 3, entity.getEntities(null).size());
  }

  /**
   * Test deletion.
   * 
   * @throws Exception
   *           the exception
   */
  public void testDeletion() throws Exception {
    if (isDeactivated()) {
      return;
    }

    entity.create();
    assertEquals(1, entity.getEntities("id=" + entity.getID()).size());
    entity.remove();
    assertEquals(0, entity.getEntities("id=" + entity.getID()).size());
  }

  /**
   * Checks if this test is deactivated.
   * 
   * @return true, if deactivated
   */
  protected boolean isDeactivated() {
    return false;
  }
}
