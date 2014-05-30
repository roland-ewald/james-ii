/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random;

import java.util.Random;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.math.random.generators.java.JavaRandom;

/**
 * Tests whether {@link JavaRandom} does have exactly the same properties as
 * {@link java.util.Random} to allow it being used to repeat simulations that
 * were conducted with {@link java.util.Random}.
 * 
 * @author Johannes Rössel
 */
public class JavaRandomTest extends ChattyTestCase {

  /** The instance of {@link java.util.Random} to test against. */
  private Random rnd;

  /** The instance of JAMES's {@link JavaRandom} to test. */
  private JavaRandom rnd2;

  /**
   * The number of iterations for each test. This basically set how many numbers
   * should be drawn and compared.
   */
  private static final int NUM_TESTS = 100000;

  @Override
  protected void setUp() throws Exception {
    long seed = System.currentTimeMillis();
    addParameter("seed", seed);
    rnd = new Random(seed);
    rnd2 = new JavaRandom(seed);
  }

  @Override
  protected void tearDown() throws Exception {
    rnd = null;
    rnd2 = null;
  }

  /**
   * Tests whether {@link JavaRandom#nextBoolean()} yields exactly the same
   * sequence as {@link Random#nextBoolean()}.
   */
  public void testNextBoolean() {
    for (int i = 0; i < NUM_TESTS; i++) {
      assertEquals(rnd.nextBoolean(), rnd2.nextBoolean());
    }
  }

  /**
   * Tests whether {@link JavaRandom#nextDouble()} yields exactly the same
   * sequence as {@link Random#nextDouble()}.
   */
  public void testNextDouble() {
    for (int i = 0; i < NUM_TESTS; i++) {
      assertEquals(rnd.nextDouble(), rnd2.nextDouble());
    }
  }

  /**
   * Tests whether {@link JavaRandom#nextFloat()} yields exactly the same
   * sequence as {@link Random#nextFloat()}.
   */
  public void testNextFloat() {
    for (int i = 0; i < NUM_TESTS; i++) {
      assertEquals(rnd.nextFloat(), rnd2.nextFloat());
    }
  }

  /**
   * Tests whether {@link JavaRandom#nextInt()} yields exactly the same sequence
   * as {@link Random#nextInt()}.
   */
  public void testNextInt() {
    for (int i = 0; i < NUM_TESTS; i++) {
      assertEquals(rnd.nextInt(), rnd2.nextInt());
    }
  }

  /**
   * Tests whether {@link JavaRandom#nextInt(int)} yields exactly the same
   * sequence as {@link Random#nextInt(int)}.
   */
  public void testNextIntInt() {
    int[] tests = { 20, 100, 150, 800, 234234, 46456423 };
    for (int x : tests) {
      for (int i = 0; i < NUM_TESTS; i++) {
        assertEquals(rnd.nextInt(x), rnd2.nextInt(x));
      }
    }
  }

  /**
   * Tests whether {@link JavaRandom#nextLong()} yields exactly the same
   * sequence as {@link Random#nextLong()}.
   */
  public void testNextLong() {
    for (int i = 0; i < NUM_TESTS; i++) {
      assertEquals(rnd.nextLong(), rnd2.nextLong());
    }
  }

}
