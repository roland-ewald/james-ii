/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.randu;

import org.jamesii.core.math.random.generators.AbstractRandom;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGPeriod;
import org.jamesii.core.math.random.generators.RNGInfo.UsableBits;

/**
 * Historical, well-known, very bad generator. RANDU was notorious for providing
 * about a decade worth of flawed research due to its inferior random numbers.
 * They all lie on just 15 planes in a spectral test with <i>k</i> = 3
 * dimensions which went unnoticed for quite some time while it was in general
 * use. It is implemented here for repeatability of past results and testing
 * with known bad generators.
 * <p>
 * Don’t (I repeat: <strong>don’t</strong>) use this generator for serious and
 * new simulations. It has done enough damage already.
 * 
 * @author Johannes Rössel
 */
public class RANDU extends AbstractRandom implements IRandom {

  /** The Constant rnginfo. */
  private static final RNGInfo RNGINFO = new RNGInfo("RANDU", "LCG",
      new RNGPeriod(2, 29), 31, 31, UsableBits.UPPER, 31, UsableBits.UPPER);

  /** Serial version UID. */
  private static final long serialVersionUID = 7097564786909653250L;

  /** Last computed value. */
  private int last;

  /**
   * Instantiates a new rANDU.
   * 
   * @param seed
   *          the seed
   */
  public RANDU(Long seed) {
    super(seed);
    init(seed);
  }

  @Override
  protected final void init(long seed) {
    if (seed % 2 == 0) {
      // ensure that the seed is odd
      last = (int) (seed + 1);
    } else {
      last = (int) (seed & 0x7fffffff);
    }
    last &= 0x7FFFFFFF;
  }

  @Override
  public long next() {
    last *= 65539;
    last &= 0x7FFFFFFF; // RANDU generates only 31 bits
    return last;
  }

  @Override
  public RNGInfo getInfo() {
    // TODO: Information may be inaccurate
    return RNGINFO;
  }

}
