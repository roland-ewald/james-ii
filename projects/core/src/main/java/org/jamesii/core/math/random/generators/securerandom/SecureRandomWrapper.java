/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.generators.securerandom;

import java.security.SecureRandom;

import org.jamesii.core.math.random.generators.AbstractRandom;
import org.jamesii.core.math.random.generators.RNGInfo;
import org.jamesii.core.math.random.generators.RNGInfo.UsableBits;

/**
 * Simple wrapper class around Java's {@link SecureRandom} class. Not intended
 * for simulation (due to speed concerns), but should provide a decent
 * comparison as it is cryptographically secure and will probably pass all tests
 * we have (and will have).
 * 
 * @author Johannes Rössel
 */
public class SecureRandomWrapper extends AbstractRandom {

  /**
   * Cached RNG information. As this is constant there is no need in re-creating
   * the object every time it is requested.
   */
  private static final RNGInfo RNGINFO = new RNGInfo("Java's SecureRandom",
      "SHA1", null, 64, 64, UsableBits.LOWER, 64, UsableBits.LOWER);

  /** Serial version ID. */
  private static final long serialVersionUID = 4789335277598945938L;

  /** The {@link SecureRandom} object to which calls are delegated. */
  private SecureRandom rng = new SecureRandom();

  /**
   * Instantiates a new secure random wrapper.
   * 
   * @param seed
   *          the seed
   */
  public SecureRandomWrapper(Long seed) {
    super(seed);
    init(seed);
  }

  @Override
  public RNGInfo getInfo() {
    return RNGINFO;
  }

  @Override
  protected final void init(long seed) {
    rng.setSeed(new byte[] { (byte) (seed & 0xFF), // NOSONAR
        (byte) ((seed >>> 8) & 0xFF), (byte) ((seed >>> 16) & 0xFF), // NOSONAR
        (byte) ((seed >>> 24) & 0xFF), (byte) ((seed >>> 32) & 0xFF),// NOSONAR
        (byte) ((seed >>> 40) & 0xFF), (byte) ((seed >>> 48) & 0xFF),// NOSONAR
        (byte) ((seed >>> 56) & 0xFF) });// NOSONAR
  }

  @Override
  public long next() {
    return rng.nextLong();
  }

}
