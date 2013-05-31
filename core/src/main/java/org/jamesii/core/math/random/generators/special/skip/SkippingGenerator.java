/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.special.skip;

import java.io.Serializable;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;

/**
 * A RNG filter which skips a specified amount of numbers from the sequence
 * before returning the next one.
 * <p>
 * BUGS: The controlled PRNG should be set somewhere, Each next* method should
 * skip generator states by using next() instead of nextInt, nextDouble etc.,
 * getInfo should alter the period accordingly.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public class SkippingGenerator implements IRandom {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -3355036558596089528L;

  /** The controlled prng. */
  private IRandom controlledPRNG;

  /** The step size. stepSize = 1 means no skipping! */
  private int stepSize = 2;

  @Override
  public RNGInfo getInfo() {
    return controlledPRNG.getInfo();
  }

  @Override
  public Serializable getSeed() {
    return controlledPRNG.getSeed();
  }

  @Override
  public long next() {

    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.next();
    }

    return controlledPRNG.next();
  }

  @Override
  public boolean nextBoolean() {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextBoolean();
    }
    return controlledPRNG.nextBoolean();
  }

  @Override
  public double nextDouble() {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextDouble();
    }
    return controlledPRNG.nextDouble();
  }

  @Override
  public float nextFloat() {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextFloat();
    }
    return controlledPRNG.nextFloat();
  }

  @Override
  public int nextInt() {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextInt();
    }
    return controlledPRNG.nextInt();
  }

  @Override
  public int nextInt(int n) {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextInt(n);
    }
    return controlledPRNG.nextInt(n);
  }

  @Override
  public long nextLong() {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextLong();
    }
    return controlledPRNG.nextLong();
  }

  @Override
  public void setSeed(Serializable seed) {
    controlledPRNG.setSeed(seed);
  }

  @Override
  public long nextLong(long n) {
    for (int i = 1; i < stepSize; i++) {
      controlledPRNG.nextLong(n);
    }
    return controlledPRNG.nextLong(n);
  }

}
