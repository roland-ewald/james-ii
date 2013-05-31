/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.special.initphase;

import java.io.Serializable;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;

/**
 * The Class InitPhaseSkipGenerator.
 * 
 * You can use this class to get rid of the first n random numbers. This is a
 * recommended procedure, because some random numbers need a warm up phase until
 * they produce "good" random numbers.
 * 
 * You can combine this random number with and other "special" generator
 * (filter). If you want to combine this class with the period control
 * {@link org.jamesii.core.math.random.generators.special.periodcontrol.PeriodControlledGenerator}
 * class you should use an instance of this class here as parameter for the
 * PeriodControlledGenerator -- this makes sure that if a new seed is used by
 * the period controlled generator the first n random numbers are skipped again.
 * 
 * @author Jan Himmelspach
 */
public class InitPhaseSkipGenerator implements IRandom {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8575224423831667469L;

  /**
   * The default amount of number to be skipped.
   */
  private static final long DEFAULTINITSKIP = 100000;

  /** The controlled prng. */
  private IRandom controlledPRNG;

  /** The init skip. */
  private long initSkip = DEFAULTINITSKIP;

  /**
   * Instantiates a new period controlled generator.
   * 
   * @param controlledPRNG
   *          the controlled prng
   */
  public InitPhaseSkipGenerator(IRandom controlledPRNG) {
    super();
    this.controlledPRNG = controlledPRNG;

    control();
  }

  /**
   * Instantiates a new period controlled generator.
   * 
   * @param controlledPRNG
   *          the controlled prng
   * @param initSkip
   *          the init skip
   */
  public InitPhaseSkipGenerator(IRandom controlledPRNG, long initSkip) {
    super();
    this.controlledPRNG = controlledPRNG;

    this.initSkip = initSkip;

    control();
  }

  @Override
  public RNGInfo getInfo() {
    RNGInfo info = controlledPRNG.getInfo();
    return new RNGInfo("Init Phase Skip: " + info.getName(),
        info.getRngFamily(), info.getPeriod(), info.getNumberOfBits(),
        info.getUsableBits(), info.getUsableBitsEnd(),
        info.getUsableBitsForCombinedGenerators(),
        info.getUsableBitsForCombinedGeneratorsEnd());
  }

  @Override
  public Serializable getSeed() {
    return controlledPRNG.getSeed();
  }

  @Override
  public long next() {
    return controlledPRNG.next();
  }

  @Override
  public boolean nextBoolean() {
    return controlledPRNG.nextBoolean();
  }

  @Override
  public double nextDouble() {
    return controlledPRNG.nextDouble();
  }

  @Override
  public float nextFloat() {
    return controlledPRNG.nextFloat();
  }

  @Override
  public int nextInt() {
    return controlledPRNG.nextInt();
  }

  @Override
  public int nextInt(int n) {
    return controlledPRNG.nextInt(n);
  }

  @Override
  public long nextLong() {
    return controlledPRNG.nextLong();
  }

  @Override
  public void setSeed(Serializable seed) {
    controlledPRNG.setSeed(seed);
    control();
  }

  /**
   * Control (draw initSkip random numbers). The numbers drawn will not be used.
   */
  private void control() {
    for (long l = 0; l < initSkip; l++) {
      controlledPRNG.next();
    }
  }

  @Override
  public long nextLong(long n) {
    return controlledPRNG.nextLong(n);
  }

}
