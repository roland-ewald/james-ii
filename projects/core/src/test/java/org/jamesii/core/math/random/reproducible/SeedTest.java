/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.reproducible;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Random;

import org.jamesii.ChattyTestCase;


/**
 *
 * LICENCE: JAMESLIC
 * @author Stefan Rybacki
 *
 */
public class SeedTest extends ChattyTestCase {

  private byte[] bytes;

  private Random rng;

  @Override
  protected void setUp() throws Exception {
    super.setUp();

    long seed = (long) (Math.random() * Long.MAX_VALUE);
    addParameter("seed", seed);

    rng = new Random(seed);

    bytes = new byte[rng.nextInt(Long.SIZE / 8 * 4) + Integer.SIZE / 8];
    for (int i = 0; i < bytes.length; i++) {
      bytes[i] = (byte) (rng.nextInt(255) - 128);
    }
  }

  /**
   * Test method for {@link Seed#hashCode()}.
   */
  public void testHashCode() {
    Seed s1 = new Seed(bytes);
    Seed s2 = new Seed(bytes);

    Seed s3 = Seed.fromLong(Seed.asLong(s2) + 1);

    assertEquals(s1.hashCode(), s2.hashCode());
    assertFalse(s1.hashCode() == s3.hashCode());
  }

  /**
   * Test method for {@link Seed#Seed(byte[])}.
   */
  public void testSeed() {
    Seed s = new Seed(bytes);
    assertNotNull(s);
  }

  /**
   * Test method for {@link Seed#getValue()}.
   */
  public void testGetValue() {
    Seed s = new Seed(bytes);
    byte[] value = s.getValue();

    assertTrue(Arrays.equals(value, bytes));
  }

  /**
   * Test method for {@link Seed#getSeedLength()}.
   */
  public void testGetSeedLength() {
    Seed s = new Seed(bytes);

    assertEquals(bytes.length, s.getSeedLength());
  }

  /**
   * Test method for {@link Seed#getValueAt(int)}.
   */
  public void testGetValueAt() {
    Seed s = new Seed(bytes);

    for (int i = 0; i < bytes.length; i++) {
      assertEquals(bytes[i], s.getValueAt(i));
    }
  }

  /**
   * Test method for {@link Seed#fromLong(long)}.
   */
  public void testFromLong() {
    long n = rng.nextLong();
    Seed s = Seed.fromLong(n);

    assertNotNull(s);
    assertTrue(Arrays.equals(s.getValue(), ByteBuffer.allocate(Long.SIZE / 8)
        .putLong(n).array()));
  }

  /**
   * Test method for {@link Seed#fromInt(int)}.
   */
  public void testFromInt() {
    int n = rng.nextInt();
    Seed s1 = Seed.fromInt(n);

    Seed s2 = Seed.fromLong(n);

    assertEquals(s1, s2);
  }

  /**
   * Test method for {@link Seed#asLong(Seed)}.
   */
  public void testAsLong() {
    long n = rng.nextLong();
    Seed s = Seed.fromLong(n);

    assertEquals(n, Seed.asLong(s));
  }

  /**
   * Test method for {@link Seed#equals(java.lang.Object)}.
   */
  public void testEqualsObject() {
    Seed s1 = new Seed(bytes);
    Seed s2 = new Seed(bytes);

    Seed s3 = Seed.fromLong(Seed.asLong(s2) + 1);

    assertEquals(s1, s2);
    assertFalse(s3.equals(s2));
  }

}
