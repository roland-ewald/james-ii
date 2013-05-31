/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.special.periodcontrol;

import java.io.Serializable;
import java.math.BigInteger;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGPeriod;
import org.jamesii.core.math.random.rnggenerator.IRNGGenerator;
import org.jamesii.core.math.random.rnggenerator.RandomNumberGeneratorException;

/**
 * The Class PeriodControlledGenerator.
 * <p>
 * BUGS: This class can't work as intended as it just adds one to the count of
 * random numbers produced for each call of nextInt, nextDouble, etc. However,
 * nextDouble might take two or three generator states, likewise nextLong, so
 * this can't ever work correctly.
 * 
 * @author Jan Himmelspach
 * @author Johannes Rössel
 */
public class PeriodControlledGenerator implements IRandom {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 8575224423831667469L;

  /** The controlled prng. */
  private IRandom controlledPRNG;

  /** The seed generator. */
  private IRNGGenerator seedGenerator = null;

  /**
   * @return the seedGenerator
   */
  public final IRNGGenerator getSeedGenerator() {
    return seedGenerator;
  }

  /**
   * @param seedGenerator
   *          the seedGenerator to set
   */
  public final void setSeedGenerator(IRNGGenerator seedGenerator) {
    this.seedGenerator = seedGenerator;
  }

  /** The count. */
  private BigInteger count = BigInteger.ZERO;

  /** The period as big integer value */
  private BigInteger period;

  /** The period barrier as big integer value */
  private BigInteger period_barrier;

  /**
   * Instantiates a new period controlled generator.
   * 
   * @param controlledPRNG
   *          the controlled prng
   */
  public PeriodControlledGenerator(IRandom controlledPRNG) {
    super();
    this.controlledPRNG = controlledPRNG;

    RNGPeriod per = controlledPRNG.getInfo().getPeriod();

    period = new BigInteger(Integer.valueOf(per.getBase()).toString());

    period = period.pow(per.getExponent());

    period =
        period.multiply(new BigInteger(Double.valueOf(per.getMultiplier())
            .toString()));

    // we should use the root here, but half is a first step ;-)
    period_barrier = period.divide(BigInteger.ONE.add(BigInteger.ONE));

  }

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
    control();
    return controlledPRNG.next();
  }

  @Override
  public boolean nextBoolean() {
    control();
    return controlledPRNG.nextBoolean();
  }

  @Override
  public double nextDouble() {
    control();
    return controlledPRNG.nextDouble();
  }

  @Override
  public float nextFloat() {
    control();
    return controlledPRNG.nextFloat();
  }

  @Override
  public int nextInt() {
    control();
    return controlledPRNG.nextInt();
  }

  @Override
  public int nextInt(int n) {
    control();
    return controlledPRNG.nextInt(n);
  }

  @Override
  public long nextLong() {
    control();
    return controlledPRNG.nextLong();
  }

  @Override
  public void setSeed(Serializable seed) {
    controlledPRNG.setSeed(seed);
  }

  /**
   * Control.
   */
  private void control() {
    count = count.add(BigInteger.ONE);
    if (count.compareTo(period_barrier) == 0) {
      count = BigInteger.ZERO;
      controlledPRNG.setSeed(System.currentTimeMillis()); // FIXME

      if (seedGenerator == null) {
        throw new RandomNumberGeneratorException(
            "Period of a PRNG used exhausted. You should either use more than one PRNG or use a PRNG with a longer period!");
      }
    }
  }

  @Override
  public long nextLong(long n) {
    control();
    return controlledPRNG.nextLong(n);
  }

}
