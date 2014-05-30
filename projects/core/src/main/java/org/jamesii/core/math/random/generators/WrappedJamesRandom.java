/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators;

import java.util.Random;

/**
 * Wrapper class that extends, and can be used in place of,
 * {@link java.util.Random}, but delegates public method calls those of a given
 * (James II) {@link IRandom} instance. This can be used to pass a
 * James-II-style random number generator to a James-unaware Java method that
 * can use a random number generator argument, e.g.
 * {@link java.util.Collections#shuffle(java.util.List, Random)}.
 * 
 * This unfortunately means overriding most implementations of the super class'
 * public methods, as there is no equivalent random number generator interface
 * in Java, only the concrete implementation {@link Random} (and a few
 * subclasses). Since most of the super class' methods rely on the protected
 * method next(int), it is overridden here throwing an exception to sort of
 * ensure that no method of the super class is accidentally used. Not overridden
 * methods of the super class are {@link #nextBytes(byte[])} and
 * {@link #nextGaussian()}, which have no equivalent in {@link IRandom}) and
 * whose implementations only rely on {@link #nextInt()} and
 * {@link #nextDouble()}, respectively, which in turn are overridden/delegated
 * here.
 * 
 * @author Arne Bittig
 * @date 22.08.2012
 */
public class WrappedJamesRandom extends Random {

  private static final long serialVersionUID = -8385557746707864624L;

  private final IRandom random;

  /**
   * Simple constructor (no check whether parameter is itself wrapping a
   * {@link Random} instance)
   * 
   * @param random
   *          James II style random number generator to wrap
   */
  public WrappedJamesRandom(IRandom random) {
    this.random = random;
  }

  @Override
  protected int next(int bits) {
    throw new UnsupportedOperationException();
  }

  @Override
  public synchronized void setSeed(long seed) {
    if (random == null) {
      return; // ignore call from super constructor
    }
    random.setSeed(seed);
  }

  @Override
  public int nextInt() {
    return random.nextInt();
  }

  @Override
  public int nextInt(int n) {
    return random.nextInt(n);
  }

  @Override
  public long nextLong() {
    return random.nextLong();
  }

  @Override
  public boolean nextBoolean() {
    return random.nextBoolean();
  }

  @Override
  public float nextFloat() {
    return random.nextFloat();
  }

  @Override
  public double nextDouble() {
    return random.nextDouble();
  }

  /**
   * Get the wrapped James II style random number generator
   * 
   * @return Internal {@link IRandom} instance
   */
  public final IRandom getWrappedRandom() {
    return random;
  }

}
