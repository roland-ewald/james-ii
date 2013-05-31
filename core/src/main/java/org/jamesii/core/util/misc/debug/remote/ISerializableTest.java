/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc.debug.remote;

import java.io.Serializable;

/**
 * Interface for {@link SerializableTest}.
 * 
 * @author Simon Bartels
 * 
 */
public interface ISerializableTest {

  /**
   * Tests if an object can be transferred via RMI.
   * 
   * @param testObject
   *          object to be tested.
   * @return true if transfer succeeded
   */
  boolean isSerializable(Serializable testObject);

  /**
   * Tests if an object can be transferred via RMI. The difference to method1 is
   * that this method does not start a test thread which mean's you have to do
   * it manually. (Call main of {@link SerializableTest}) It has some
   * performance advantages and better debug possibilities. BEWARE: Don't use
   * both methods in the same run as isSerializable() will shutdown the test
   * JVM.
   * 
   * @param o
   *          The object to be tested.
   * 
   * @return True, if so and false, otherwise.
   */
  boolean isSerializable2(Serializable o);

}
