/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.mersennetwister;

import java.util.Random;

import org.jamesii.core.math.random.generators.AbstractRandom;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGPeriod;
import org.jamesii.core.math.random.generators.RNGInfo.UsableBits;

/**
 * This class implements the Mersenne Twister pseudo-random number generator
 * algorithm MT19937 as described by Matsumoto and Nishimura.
 * <p>
 * The Mersenne Twister is a very high-quality PRNG specifically suited for
 * stochastic sampling and simulation. It has very high-order of dimensional
 * equidistribution and passes even the Diehard tests. The period of this
 * algorithm is 2<sup>19937</sup> − 1, so repetition is pretty far away. That
 * said it is also pretty fast in average, except for the first number to be
 * generated and if the internal pool runs out. It is <strong>not</strong>
 * recommend for cryptographic purposes!
 * 
 * @author Johannes Rössel
 */
public class MersenneTwister extends AbstractRandom implements IRandom {

  /** The serialization ID. */
  private static final long serialVersionUID = -7388021287284276347L;

  /** The Constant RNGINFO. */
  private static final RNGInfo RNGINFO = new RNGInfo("MT19937", "MT",
      new RNGPeriod(1, 2, 19937), 32, 32, UsableBits.UPPER, 32,
      UsableBits.UPPER);

  /* Period parameters */
  /** The Constant N. */
  private static final int N = 624;

  /** The Constant M. */
  private static final int M = 397;

  /** The Constant MATRIX_A. */
  private static final int MATRIX_A = 0x9908b0df;

  /** The Constant UPPER_MASK. */
  private static final int UPPER_MASK = 0x80000000;

  /** The Constant LOWER_MASK. */
  private static final int LOWER_MASK = 0x7fffffff;

  /* Tempering parameters */
  /** The Constant TEMPERING_MASK_B. */
  private static final int TEMPERING_MASK_B = 0x9d2c5680;

  /** The Constant TEMPERING_MASK_C. */
  private static final int TEMPERING_MASK_C = 0xefc60000;

  /** Array to hold the working area. */
  private int[] mt;

  /**
   * Current index in the working area. Required to recompute the working area
   * if needed.
   */
  private int mti = N + 1;

  /**
   * Creates a MersenneTwister with a default random seed taken from
   * {@link Random}.
   */
  public MersenneTwister() {
    this(Math.round(Math.random() * Long.MAX_VALUE));
  }

  /**
   * Creates a MersenneTwister with a given seed.
   * 
   * @param seed
   *          The seed to initialize the internal pool with.
   */
  public MersenneTwister(long seed) {
    super(seed);
    init(seed);
  }

  @Override
  protected void init(long seed) {
    assert (seed != 0); // this is only active if activated
    if (seed == 0) {
      seed++; // just use another number
    }
    mt = new int[N];
    mt[0] = (int) (seed & 0xffffffff);// NOSONAR
    for (mti = 1; mti < N; mti++) {
      mt[mti] = (69069 * mt[mti - 1]) & 0xffffffff;// NOSONAR
    }
  }

  /**
   * Generate words.
   */
  private void generateWords() {
    int kk, y;
    int mag01[] = { 0, MATRIX_A };

    for (kk = 0; kk < N - M; kk++) {
      y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
      mt[kk] = mt[kk + M] ^ (y >>> 1) ^ mag01[y & 1];
    }
    for (; kk < N - 1; kk++) {
      y = (mt[kk] & UPPER_MASK) | (mt[kk + 1] & LOWER_MASK);
      mt[kk] = mt[kk + (M - N)] ^ (y >>> 1) ^ mag01[y & 1];
    }
    y = (mt[N - 1] & UPPER_MASK) | (mt[0] & LOWER_MASK);
    mt[N - 1] = mt[M - 1] & (y >>> 1) ^ mag01[y & 1];

    mti = 0;
  }

  @Override
  public final long next() {
    int y;

    /* word area exhausted? */
    if (mti >= N) {
      generateWords();
    }

    y = mt[mti++];
    y ^= y >>> 11; // NOSONAR
    y ^= (y << 7) & TEMPERING_MASK_B;// NOSONAR
    y ^= (y << 15) & TEMPERING_MASK_C;// NOSONAR
    y ^= (y >>> 18);// NOSONAR

    // and-ing is necessary here, otherwise negative numbers are a problem
    return y & 0xffffffffL;// NOSONAR
  }

  @Override
  public RNGInfo getInfo() {
    return RNGINFO;
  }

}
