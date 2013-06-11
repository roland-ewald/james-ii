/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.java;

import java.io.Serializable;
import java.util.Random;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGPeriod;
import org.jamesii.core.math.random.generators.RNGInfo.UsableBits;

/**
 * This class encapsulates the default Java random number generator (
 * {@link Random}). Since this is a linear congruential generator it should not
 * be used for serious simulation purposes.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public class JavaRandom implements IRandom {

  /** Information about the generator. */
  private static final RNGInfo RNGINFO = new RNGInfo("Java's Random", "LCG",
      new RNGPeriod(1, 2, 48), 32, 32, UsableBits.UPPER, 32, UsableBits.UPPER);

  /** Serialisation ID. */
  private static final long serialVersionUID = 5880169302951265629L;

  /** The initial seed. */
  private Serializable initialSeed;

  /** The rng used internally. */
  private Random rng = new Random();

  /**
   * Initialises the generator with a default seed.
   * <p>
   * <strong>NOT RECOMMENDED AND ONLY A TEMPORARY STOPGAP SOLUTION!</strong>
   */
  public JavaRandom() {
    setSeed(System.currentTimeMillis());
  }

  /**
   * Initialises the generator with the given seed.
   * 
   * @param seed
   *          The seed to use. Same seeds always yield the same sequence of
   *          pseudo-random numbers.
   */
  public JavaRandom(long seed) {
    setSeed(seed);
  }

  @Override
  public RNGInfo getInfo() {
    // TODO: Information may be inaccurate
    return RNGINFO;
  }

  @Override
  public long next() {
    return rng.nextInt() & 0xffffffffl;
  }

  @Override
  public Serializable getSeed() {
    return initialSeed;
  }

  @Override
  public long nextLong(long n) {
    // code adapted from the Javadoc of java.util.Random.nextInt(int). Since the
    // Javadoc belongs to the Java Specification this should be OK.

    // Note that I do not know how well this method adapts to 64 bits instead of
    // 32. Here may well be dragons, erm, bugs. It seems to work, though.
    if (n <= 0) {
      throw new IllegalArgumentException("n must be positive.");
    }

    long bits, val;
    do {
      bits = rng.nextLong() >>> 1;
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  @Override
  public final void setSeed(Serializable seed) {
    initialSeed = seed;
    // Note: To ensure the right overload of setSeed() we need a little type
    // trickery here
    if (seed instanceof Long) {
      rng.setSeed(((Long) seed).longValue());
    } else {
      rng.setSeed(Long.valueOf(seed.toString()));
    }

  }

  @Override
  public boolean nextBoolean() {
    return rng.nextBoolean();
  }

  @Override
  public double nextDouble() {
    return rng.nextDouble();
  }

  @Override
  public float nextFloat() {
    return rng.nextFloat();
  }

  @Override
  public int nextInt() {
    return rng.nextInt();
  }

  @Override
  public int nextInt(int n) {
    return rng.nextInt(n);
  }

  @Override
  public long nextLong() {
    return rng.nextLong();
  }
}
