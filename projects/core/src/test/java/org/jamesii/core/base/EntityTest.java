/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.base;

import java.util.List;

import junit.framework.TestCase;

import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.observe.Mediator;
import org.jamesii.core.observe.ObserverException;

/**
 * The Class EntityTest.
 * 
 * @author Jan Himmelspach
 */
public class EntityTest extends TestCase {

  /** The executed flag, written by the TestObserver. */
  IEntity executed = null;

  /** The executed_obj. */
  Object executed_obj = null;

  /**
   * Test changed.
   */
  public void testChanged() {
    Entity e = new Entity();
    IMediator medi = new Mediator();
    e.setMediator(medi);
    e.registerObserver(new TestObserver());
    e.changed();

    IEntity b = executed;
    executed = null;

    assertTrue("Observer has not been activated", b == e);
  }

  /**
   * Test changed object.
   */
  public void testChangedObject() {
    Entity e = new Entity();
    IMediator medi = new Mediator();
    e.setMediator(medi);
    e.registerObserver(new TestObserver());
    Object o = new Object();
    e.changed(o);

    IEntity b = executed;
    executed = null;

    assertTrue("Observer has not been activated", (b == e)
        && (o == executed_obj));
  }

  /**
   * Test get complete info string.
   */
  public void testGetCompleteInfoString() {
    assertTrue(new Entity().getCompleteInfoString().compareTo("") == 0);
  }

  /**
   * Test get mediator.
   */
  public void testGetMediator() {
    Entity e = new Entity();
    IMediator medi = new Mediator();
    e.setMediator(medi);

    assertTrue(medi == e.getMediator());
  }

  /**
   * Test get uid.
   */
  public void testGetUid() {
    // pretty simple test, uid generation should be tested more generally (and
    // it is in the default implementation because this is based in the
    // SimSystem getUid method
    Entity e1 = new Entity();
    Entity e2 = new Entity();
    assertTrue(e1.getSimpleId() != e2.getSimpleId());
  }

  /**
   * Test is observed.
   */
  public void testIsObserved() {
    Entity e = new Entity();
    assertTrue(!e.isObserved());

    e.setMediator(new Mediator());
    assertTrue(e.isObserved());
  }

  /**
   * Test register observer.
   */
  public void testRegisterObserver() {
    Entity e = new Entity();

    boolean exc = false;

    try {
      e.registerObserver(new TestObserver());
    } catch (ObserverException oe) {
      exc = true;
    }

    assertTrue("Registered observer without a mediator ", exc);

    e.setMediator(new Mediator());

    exc = false;

    try {
      e.registerObserver(new TestObserver());
    } catch (ObserverException oe) {
      exc = true;
    }

    assertTrue("Did not register the observer, but a mediator is there", !exc);

    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());

    List<IObserver<?>> list = e.getMediator().getObserver(e);

    assertTrue("The number of servers is not ok ", list.size() == 6);
  }

  /**
   * Test set mediator.
   */
  public void testSetMediator() {
    Entity e = new Entity();
    Mediator medi = new Mediator();
    e.setMediator(medi);

    assertTrue("Something exchanged the mediator ", medi == e.getMediator());
  }

  /**
   * Test unregister.
   */
  public void testUnregister() {
    // check wether unregistering from a not existent mediator is fine
    Entity e = new Entity();
    e.unregister();

    // check whether unregistering the mediator (wo observers) is fine
    Mediator medi = new Mediator();
    e.setMediator(medi);
    e.unregister();
    assertTrue(medi.getObserver(e) == null);

    // check whether unregistering the mediator (with observers) is fine
    e.setMediator(medi);
    e.registerObserver(new TestObserver());

    e.unregister();
    assertTrue(medi.getObserver(e) == null);
  }

  /**
   * Test unregister observer.
   */
  public void testUnregisterObserver() {
    Entity e = new Entity();
    Mediator medi = new Mediator();
    e.setMediator(medi);

    TestObserver t1 = new TestObserver();
    e.registerObserver(t1);
    TestObserver t2 = new TestObserver();
    e.registerObserver(t2);
    TestObserver t3 = new TestObserver();
    e.registerObserver(t3);
    TestObserver t4 = new TestObserver();
    e.registerObserver(t4);
    TestObserver t5 = new TestObserver();
    e.registerObserver(t5);
    TestObserver t6 = new TestObserver();
    e.registerObserver(t6);

    assertTrue(medi.getObserver(e).size() == 6);

    e.unregisterObserver(t2);

    assertTrue(medi.getObserver(e).size() == 5);

    e.unregisterObserver(t5);

    assertTrue(medi.getObserver(e).size() == 4);

    e.unregisterObserver(t1);

    assertTrue(medi.getObserver(e).size() == 3);

    List<IObserver<?>> list = medi.getObserver(e);

    assertTrue(list.contains(t3));
    assertTrue(list.contains(t4));
    assertTrue(list.contains(t6));
  }

  /**
   * Test unregister observers.
   */
  public void testUnregisterObservers() {
    Entity e = new Entity();
    Mediator medi = new Mediator();
    e.setMediator(medi);

    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());
    e.registerObserver(new TestObserver());

    e.unregisterObservers();

    assertTrue((medi.getObserver(e) == null)
        || (medi.getObserver(e).size() == 0));
  }

  /**
   * Helping class.
   */
  private class TestObserver implements IObserver<IEntity> {

    @Override
    public void update(IEntity entity) {
      executed = entity;
    }

    @Override
    public void update(IEntity entity, Object hint) {
      executed = entity;
      executed_obj = hint;
    }

  }

}
