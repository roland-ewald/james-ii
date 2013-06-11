/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators;

import java.io.Serializable;

import org.jamesii.core.base.Entity;

/**
 * Abstract class for random number generators. This class handles generation of
 * various numbers from a random bit stream. Furthermore seeds can be saved and
 * retrieved without further work of a deriving class.
 * <p>
 * The two main methods that need to be implemented by a deriving class are
 * {@code init(Long)} and {@code next()}. {@code init(Long)} should initialise
 * the generator, using the {@code Long} parameter as seed. {@code next()}
 * should return the complete next number from the generator, without further
 * post-processing. The number must be aligned at the low bits of the returned
 * {code long} and all unused high bits must be zero, so for a generator that
 * yields 32 bit numbers the returned number can never be negative.
 * <p>
 * The number returned by {@code next()} is interpreted as part of the
 * generator's bit stream and calls to {@code nextInt()}, {@code nextDouble()},
 * etc. are using this bit stream to construct the appropriate numbers. Provided
 * that {@code getInfo()} returns a valid and useful {@link RNGInfo} object the
 * generator knows about which bits to used from the stream and which to
 * discard. For testing, however, it may be beneficial to use the complete bit
 * stream unaltered.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public abstract class AbstractRandom extends Entity implements IRandom {

  /** The serialisation ID. */
  private static final long serialVersionUID = -4842955440126157378L;

  /** Number of bits in long data type */
  private static final int LONG_BITS = 64;

  /** Number of bits in int data type */
  private static final int INT_BITS = 32;

  /** Number of bits in int data type */
  private static final int FLOAT_BITS = 24;

  /** The seed. */
  private Long mySeed;

  /**
   * The rng info cache. Caches the return value of the {@link #getInfo()}
   * method. Will be set upon creation and after the seed has changed.
   * Consequently the {@link #setSeed(Serializable)} method can be used to
   * update the cached value. This might be ncessary in case of parameter
   * changes which change the characteristics if the random number generator.
   * This attribute is used in the {@link #next(int)} method. This avoids calls
   * to the {@link #getInfo()} method which causes a significant overhead in
   * case of generating millions of random numbers otherwise.
   */
  private RNGInfo rngInfoCache = null;

  /**
   * Initialises the generator with the given seed.
   * 
   * @param seed
   *          The seed to use. Same seeds always yield the same sequence of
   *          pseudo-random numbers.
   */
  public AbstractRandom(Long seed) {
    mySeed = seed;
    setRngInfoCache(getInfo());
  }

  @Override
  public void setSeed(Serializable seed) {
    if (seed instanceof Long) {
      mySeed = (Long) seed;
    } else {
      // try to convert the string representation of the seed passed to a long
      mySeed = Long.valueOf(seed.toString());
    }
    init(mySeed);
    setRngInfoCache(getInfo());
  }

  /**
   * Initialises the generator.
   * 
   * @param seed
   *          the seed
   */
  protected abstract void init(long seed);

  @Override
  public Serializable getSeed() {
    return mySeed;
  }

  @Override
  public boolean nextBoolean() {
    return next(1) == 1;
  }

  @Override
  public double nextDouble() {
    return next(53) / (double) (1L << 53);
  }

  @Override
  public float nextFloat() {
    // float has 24 digits, although only 23 of them appear explicitly
    // in the mantissa
    return next(FLOAT_BITS) / (float) (1L << FLOAT_BITS);
  }

  @Override
  public int nextInt() {
    return (int) next(INT_BITS);
  }

  @Override
  public int nextInt(int n) {
    // code taken from the Javadoc of java.util.Random.nextInt(int). Since the
    // Javadoc belongs to the Java Specification this should be OK.
    if (n <= 0) {
      throw new IllegalArgumentException("n must be positive.");
    }

    if ((n & -n) == n) {
      return (int) ((n * next(INT_BITS - 1)) >> (INT_BITS - 1));
    }

    int bits, val;
    do {
      bits = (int) next(INT_BITS - 1);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  @Override
  public long nextLong() {
    return next(LONG_BITS);
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
      bits = next(LONG_BITS - 1);
      val = bits % n;
    } while (bits - val + (n - 1) < 0);
    return val;
  }

  /**
   * Generate the given number of random bits, taking into account
   * characteristics of the respective generator, specifically which bits to use
   * for constructing random numbers. This method may use more than one state of
   * the generator to achieve that but can generate up to {@value #LONG_BITS}
   * random bits. The generated bits are aligned at the low end of the returned
   * long. <br/>
   * <i> Please note: this method is based on the {@link #rngInfoCache}
   * attribute which caches the characteristic information for this random
   * number generator. Thus this attribute has to be updated in case of any
   * parameter changes to an RNG. </i>
   * 
   * @param bits
   *          The number of bits to generate. Must be at least 1 and at most 64.
   * @return A long value that contains the specified number of random bits in
   *         the low bits
   */
  public long next(int bits) {
    if (bits <= 0 || bits > LONG_BITS) {
      throw new IllegalArgumentException(
          "The number of bits must be between 1 and 64");
    }

    // number of bits generated by the RNG
    int generatedBits = getRngInfoCache().getNumberOfBits();

    // shorten this method a bit in some special cases
    if (bits == generatedBits) {
      return next();
    }

    // maximum number of bits to use
    int maxBits = getRngInfoCache().getUsableBits();
    // sample from upper end of the bit range?
    boolean upperBits = getRngInfoCache().sampleFromUpperRange();

    // shorten this method some more in less special cases
    if (maxBits > bits) {
      if (upperBits) {
        return next() >>> (generatedBits - bits);
      }
      // else
      return next() & ((1L << bits) - 1);
    }

    int bitsLeft = bits;

    long retval = 0;
    long val;
    int min;
    while (bitsLeft > 0) {
      min = Math.min(maxBits, bitsLeft);
      val = next();
      // reduce to number of recommended bits
      if (upperBits) {
        val >>>= generatedBits - min;
      } else {
        val &= (1L << min) - 1;
      }
      retval <<= min;
      retval ^= val;
      bitsLeft -= min;
    }
    return retval;
  }

  /**
   * @return the rngInfoCache
   */
  protected final RNGInfo getRngInfoCache() {
    return rngInfoCache;
  }

  /**
   * @param rngInfoCache
   *          the rngInfoCache to set
   */
  protected final void setRngInfoCache(RNGInfo rngInfoCache) {
    this.rngInfoCache = rngInfoCache;
  }
}
