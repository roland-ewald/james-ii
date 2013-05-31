/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators;

import java.io.Serializable;

/**
 * The Class SynchronizedRandomNumberGenerator. A thread-safe decorator for
 * {@link IRandom} instances that makes all instance methods
 * {@code synchronized}.
 * 
 * @author Johannes Rössel
 * @author Jan Himmelspach
 */
public class SynchronizedRandomNumberGenerator implements IRandom {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -9060792529773692425L;

  /** The wrapped (and thus synchronized) rng used **/
  private IRandom random;

  /**
   * Initialises a new instance of the {@link ThreadsafeRandom} class,
   * decorating the given {@link IRandom} instance.
   * 
   * @param random
   *          the random number generated to be synchronized
   */
  public SynchronizedRandomNumberGenerator(IRandom random) {
    super();
    this.random = random;
  }

  @Override
  public synchronized RNGInfo getInfo() {
    return random.getInfo();
  }

  @Override
  public synchronized Serializable getSeed() {
    return random.getSeed();
  }

  @Override
  public synchronized long next() {
    return random.next();
  }

  @Override
  public synchronized boolean nextBoolean() {
    return random.nextBoolean();
  }

  @Override
  public synchronized double nextDouble() {
    return random.nextDouble();
  }

  @Override
  public synchronized float nextFloat() {
    return random.nextFloat();
  }

  @Override
  public synchronized int nextInt() {
    return random.nextInt();
  }

  @Override
  public synchronized int nextInt(int n) {
    return random.nextInt(n);
  }

  @Override
  public synchronized long nextLong() {
    return random.nextLong();
  }

  @Override
  public synchronized long nextLong(long n) {
    return random.nextLong(n);
  }

  @Override
  public synchronized void setSeed(Serializable seed) {
    random.setSeed(seed);
  }

}
