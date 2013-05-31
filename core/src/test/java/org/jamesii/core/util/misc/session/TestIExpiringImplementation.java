/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.session;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.jamesii.core.util.misc.session.IExpiring;

import junit.framework.TestCase;

/**
 * Tests the correctness of objects implementing {@link IExpiring}.
 * 
 * @author Simon Bartels
 * 
 */
public abstract class TestIExpiringImplementation extends TestCase {

  /**
   * It is assumed that every object implementing {@link IExpiring} has an
   * interface where every method will prolong the sessions lifetime. This test
   * calls every method the Interface given by {@link #getInterface()} has and
   * checks that after calling it {@link IExpiring#hasBeenActive()} returns
   * true.
   */
  public void testHasBeenActive() {

    IExpiring testObject = getTestObject();

    Method[] methods = getInterface().getMethods();

    // after two times calling this method has to return false
    testObject.hasBeenActive();
    assertFalse(testObject.hasBeenActive());

    // after activated(), hasBeenActive has to return true
    testObject.activated();
    assertTrue(testObject.hasBeenActive());

    for (Method m : methods) {
      try {
        testObject = getTestObject();
        testObject.hasBeenActive(); // let's make sure the method will return
                                    // false next time if we don't do anything
        Class<?>[] paramTypes = m.getParameterTypes();
        Object[] params = new Object[paramTypes.length];
        m.invoke(testObject, params);
      } catch (IllegalArgumentException | IllegalAccessException e) {
        e.printStackTrace();
        fail();
      } catch (InvocationTargetException e) {
        // there'll probably a big fuss going on inside the method but we'll
        // ignore it
      }
      boolean b = testObject.hasBeenActive();
      if (!b) {
        System.out.println(m.getName() + " is not resetting #hasBeenActive()!");
      }
    }
  }

  /**
   * Perfoms a test whether the object is subject to the garbage collector after
   * calling {@link IExpiring#expire()}.
   */
  public void testDisposal() {
    IExpiring sessionObject = bringObjectInContext();
    WeakReference<IExpiring> testRef = new WeakReference<>(sessionObject);
    assertNotNull(testRef.get());
    sessionObject.activated();
    sessionObject.hasBeenActive();
    sessionObject.hasBeenActive();
    sessionObject.expire();
    sessionObject = null;
    System.gc();
    assertNull("There should be no references left to the object.",
        testRef.get());
  }

  /**
   * This method is called within the test whether the object is disposing
   * itself after expiration. Please make sure you set up this object in a
   * realistic context. This means that every object that could have references
   * to the expiring object should have it for this test.
   * 
   * @return
   */
  protected abstract IExpiring bringObjectInContext();

  /**
   * The interface, {@link IExpiring} should apply. It is assumed that every
   * method will set the return value of {@link IExpiring#hasBeenActive()} to
   * true.
   * 
   * @return the interface that was the reason to implement {@link IExpiring}
   */
  protected abstract Class<?> getInterface();

  /**
   * Has to return the object under test. Note that the method is called various
   * times in the test.
   * 
   * @return the very object
   */
  protected abstract IExpiring getTestObject();
}
