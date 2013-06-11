/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.jamesii.core.math.random.generators.IRandom;

import junit.framework.TestCase;

/**
 * Abstract base class for testing serialisation of random number generators.
 * The test uses a given RNG, sets a certain state and saves it with
 * serialisation. After that the next number in the sequence is taken from the
 * RNG and saved. The RNG is then restored from its serialised state and the
 * next number of the de-serialised RNG is compared with the previously saved
 * one.
 * <p>
 * This test ensures that serialisation works for random number generators and
 * that a de-serialised RNG will yield the same sequence as before
 * serialisation.
 * <p>
 * Deriving classes must override the {@link #getRNG()} method to return a valid
 * {@link IRandom} instance. The test should then run without further changes.
 * 
 * @author Johannes Rössel
 */
public abstract class TestRNGSerialization extends TestCase {

  /** The random number generator in use. */
  protected IRandom rnd;

  /**
   * Method to override when subclassing this test. It simply returns an
   * {@link IRandom} instance which is then used by this test.
   * 
   * @return A random number generator.
   */
  protected abstract IRandom getRNG();

  /**
   * Tests whether serialisation and subsequent de-serialisation works as
   * expected for the random number generator. Basically this means that the
   * RNG's state after de-serialisation must match the state that was
   * Serialised.
   */
  public void testSerialization() {
    rnd = getRNG();
    rnd.setSeed(12345L);
    rnd.nextInt(); // throw one value away, just to “randomise” the seed a bit
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream o = new ObjectOutputStream(baos);
      // Serialise the RNG
      o.writeObject(rnd);
      // save the next number
      int val = rnd.nextInt();
      // grab the output from the stream
      byte[] b = baos.toByteArray();
      // reconstruct the original state
      rnd =
          (IRandom) (new ObjectInputStream(new ByteArrayInputStream(b))
              .readObject());
      // now the next value generated should match the one generated directly
      // after serialisation
      assertEquals(val, rnd.nextInt());
    } catch (IOException e) {
      e.printStackTrace();
      fail(e.getMessage());
    } catch (ClassNotFoundException e) {
      fail(e.getMessage());
    }

  }
}
