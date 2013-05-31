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
import java.security.SecureRandom;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.securerandom.SecureRandomWrapper;
import org.jamesii.core.math.random.serialization.TestRNGSerialization;

/**
 * Basic serialization test of the {@link SecureRandom} RNG.
 * 
 * @author Johannes Rössel
 */
public class TestSecureRandomSerialization extends TestRNGSerialization {

  @Override
  protected IRandom getRNG() {
    return new SecureRandomWrapper(123L);
  }

  @Override
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
      // now the next value generated should not match the one generated
      // directly
      // after serialisation
      assertFalse(val == rnd.nextInt());
    } catch (IOException | ClassNotFoundException e) {
      fail();
    }

  }

}
