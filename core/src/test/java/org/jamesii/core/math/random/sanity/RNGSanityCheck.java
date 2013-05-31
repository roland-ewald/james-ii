/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.sanity;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.plugintype.RandomGeneratorFactory;

import junit.framework.TestCase;

/**
 * Abstract base class for simple sanity checks for random number generators. If
 * those fail there are probably severe errors in the implementation as they do
 * not meet the specifications of the interface. Each test generates a million
 * random numbers to catch potential problems with a high probability.
 * <p>
 * Deriving classes must override the {@link #getRNGFactory()} method to provide
 * a suitable {@link RandomGeneratorFactory}. The test then runs without further
 * changes.
 * 
 * @author Johannes Rössel
 */
public abstract class RNGSanityCheck extends TestCase {

  /** The number of random numbers to sample for the test. */
  private static final int NUM_RUNS = 1000000;

  /** The random number generator in use. */
  protected IRandom rng;

  /**
   * Method to override when sub-classing this test. It simply returns a factory
   * which subsequently will be used to create the random number generator to
   * test.
   * 
   * @return A random number generator factory.
   */
  protected abstract RandomGeneratorFactory getRNGFactory();

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    // the seed should suffice, we just want to catch abnormal conditions
    rng = getRNGFactory().create(System.currentTimeMillis());
  }

  /** Checks for {@link IRandom#nextDouble()} being in the range [0, 1). */
  public void testNextDouble() {
    double x;
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextDouble();
      assertTrue(x + " is not between 0 and 1", (x < 1) && (x >= 0));
    }
  }

  /** Checks for {@link IRandom#nextFloat()} being in the range [0, 1). */
  public void testNextFloat() {
    float x;
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextFloat();
      assertTrue(x + " is not between 0 and 1", (x < 1) && (x >= 0));
    }
  }

  /** Checks for {@link IRandom#nextInt(int)} being in the range [0, <i>n</i>). */
  public void testNextIntInt() {
    int x;
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextInt(20);
      assertTrue(x < 20 && x >= 0);
    }
    // also test with power of two, since that's a separate code path
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextInt(64);
      assertTrue(x < 64 && x >= 0);
    }
  }

  /**
   * Checks for {@link IRandom#nextLong(long)} being in the range [0, <i>n</i>).
   */
  public void testNextLongLong() {
    long x;
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextLong(5134856846L);
      assertTrue(x < 5134856846L && x >= 0);
    }
    // also test with power of two, since that's a separate code path
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.nextLong(140737488355328L);
      assertTrue(x < 140737488355328L && x >= 0);
    }
  }

  /**
   * Checks for the RNG yielding the same sequence if initialised with the same
   * seed.
   */
  public void testSeedBehaviour() {
    long seed = System.currentTimeMillis();

    long[] values1 = new long[50];
    long[] values2 = new long[50];

    rng.setSeed(seed);
    for (int i = 0; i < 50; i++) {
      values1[i] = rng.next();
    }

    // sleep a while, just in case some generator uses the current time to salt
    // the seed.
    try {
      Thread.sleep(2000);
    } catch (InterruptedException e) {
    }

    rng.setSeed(seed);
    for (int i = 0; i < 50; i++) {
      values2[i] = rng.next();
    }

    for (int i = 0; i < 50; i++) {
      assertEquals(
          "The generator didn't produce the same sequence given the same seed at value "
              + i + ".", values1[i], values2[i]);
    }
  }

  /**
   * Checks for {@link IRandom#getInfo()} returning a non-null object. This is
   * important since pretty much the entire random number generation via
   * AbstractRandom depends on this. Furthermore, a generator without accurate
   * metadata is probably not worth using.
   * <p>
   * This method also checks whether the number of generated bits is larger or
   * equal than the number of bits marked safe for use.
   */
  public void testGetInfo() {
    // getInfo() must not return null
    assertNotNull(rng.getInfo());
    // the number of generated bits must exceed the number of usable bits
    assertTrue(rng.getInfo().getUsableBits() <= rng.getInfo().getNumberOfBits());
    assertTrue(rng.getInfo().getUsableBitsForCombinedGenerators() <= rng
        .getInfo().getNumberOfBits());
  }

  /**
   * Checks whether the number of bits returned by a call to
   * {@link IRandom#next()} is consistent with the metadata in the
   * {@link RNGInfo} data structure.
   */
  public void testReturnedBits() {
    long x;
    for (int i = 0; i < NUM_RUNS; i++) {
      x = rng.next();
      assertTrue(
          "Returned word has one bits at the upper end, but there shouldn't be.",
          Long.numberOfLeadingZeros(x) >= (64 - rng.getInfo().getNumberOfBits()));
    }
  }
}
