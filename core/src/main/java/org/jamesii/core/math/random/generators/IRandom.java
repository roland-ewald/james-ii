/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators;

import java.io.Serializable;

/**
 * Interface for (pseudo-)random number generators. Must be implemented by every
 * class that wishes to be used as such.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public interface IRandom extends Serializable {

  /**
   * Sets the seed of the generator. For PRNGs this should ensure that the same
   * seed yields the same sequence, as this is a usual property of PRNGs and
   * their seeds.
   * 
   * @param seed
   *          The seed
   */
  void setSeed(Serializable seed);

  /**
   * Gets the seed with which this RNG object was initialized.
   * 
   * @return the seed
   */
  Serializable getSeed();

  /**
   * Returns a uniformly distributed 32-bit {@code int} value from the random
   * number generator.
   * 
   * @return A (pseudo-)random 32-bit integer.
   */
  int nextInt();

  /**
   * Returns a uniformly distributed {@code int} value between 0 (inclusive) and
   * <i>n</i> (exclusive).
   * 
   * @param n
   *          The upper bound. The returned number is always lower than
   *          <i>n</i>.
   * @return A (pseudo-)random integer in the interval [0, <i>n</i>).
   */
  int nextInt(int n);

  /**
   * Returns a uniformly distributed 64-bit {@code long} value from the random
   * number generator.
   * 
   * @return A (pseudo-)random 64-bit integer.
   */
  long nextLong();

  /**
   * Returns a uniformly distributed {@code long} value between 0 (inclusive)
   * and <i>n</i> (exclusive).
   * 
   * @param n
   *          The upper bound. The returned number is always lower than
   *          <i>n</i>.
   * @return A (pseudo-)random long integer in the interval [0, <i>n</i>).
   */
  long nextLong(long n);

  /**
   * Returns a {@code boolean} value from the random number generator.
   * {@code true} and {@code false} should be returned with approximately the
   * same probability.
   * 
   * @return A (pseudo-)random boolean.
   */
  boolean nextBoolean();

  /**
   * Returns a uniformly distributed {@code float} value between 0.0 (inclusive)
   * and 1.0 (exclusive).
   * 
   * @return A (pseudo-)random single-precision floating point value in the
   *         interval [0, 1).
   */
  float nextFloat();

  /**
   * Returns a uniformly distributed {@code double} value between 0.0
   * (inclusive) and 1.0 (exclusive).
   * 
   * @return A (pseudo-)random double-precision floating point value in the
   *         interval [0, 1).
   */
  double nextDouble();

  /**
   * Generates the next number in the sequence of the generator. This should
   * return the complete bit pattern as {@link #getInfo()} can be called to
   * determine which bits should be used.
   * <p>
   * This method should <em>not</em> be used to get a random long value, that's
   * what {@code nextLong()} is for! Use this method only if you know you need
   * or want it. For anything else there is {@code next*}!
   * 
   * @return A pattern of at most 64 bits that constitute the next number from
   *         the generated stream.
   */
  long next();

  /**
   * Returns an {@code RNGInfo} object, containing some metadata about the
   * generator itself.
   * 
   * @return The generator information.
   */
  RNGInfo getInfo();
}
