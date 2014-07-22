/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.reproducible;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.logging.Level;

import org.jamesii.SimSystem;

/**
 * A seed implementation abstracting from standard seeds expressed {@link Long}
 * or {@link Integer} which are restricted in how many different seeds they can
 * represent. This implementation does not restrict the resolution of the seed,
 * hence allows for an arbitrary resolution of the seed. Here, a seed is
 * internally represented as a number of bytes.
 * 
 * @author Stefan Rybacki
 * 
 */
public final class Seed implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final int LONG_SIZE = Long.SIZE / 8;

  private final byte[] seed;

  private final int hashCode;

  /**
   * Creates a new seed based on an arbitrary length byte array.
   * 
   * @param seed
   *          the seed initialization array
   */
  public Seed(byte[] seed) {
    this.seed = new byte[seed.length];
    System.arraycopy(seed, 0, this.seed, 0, seed.length);
    hashCode = new String(seed).hashCode();
  }

  /**
   * Returns a copy of the internal seed byte array
   * 
   * @return
   */
  public byte[] getValue() {
    // defensive copy
    byte[] out = new byte[seed.length];
    System.arraycopy(seed, 0, out, 0, seed.length);
    return out;
  }

  /**
   * Returns the lengths of the seed, meaning the resolution of the seed
   * 
   * @return the seed's length
   */
  public int getSeedLength() {
    return seed.length;
  }

  /**
   * Returns the byte value of the seed at the given index
   * 
   * @param index
   *          the index to lookup
   * @return the seed value at given index
   */
  public byte getValueAt(int index) {
    return seed[index];
  }

  /**
   * Convenience helper method to create a {@link Seed} from a given
   * {@link Long} based seed value (basically for easier integration in legacy
   * code)
   * 
   * @param seed
   *          the seed as long
   * @return the seed based on given seed
   */
  public static Seed fromLong(long seed) {
    return new Seed(ByteBuffer.allocate(LONG_SIZE).putLong(seed).array());
  }

  /**
   * Convenience helper method to create a {@link Seed} from a given
   * {@link Integer} based seed value (basically for easier integration in
   * legacy code)
   * 
   * @param seed
   *          the seed as int
   * @return the seed based on given seed
   */
  public static Seed fromInt(int seed) {
    return fromLong(seed);
  }

  /**
   * Convenience helper method to create a {@link Long} value from a given
   * {@link Seed} (basically for easier integration in legacy code)
   * 
   * @param seed
   *          the seed to convert to long
   * @return the long seed based on given seed
   */
  public static long asLong(Seed seed) {
    int length = seed.seed.length;
    if (length > LONG_SIZE) {
      SimSystem.report(Level.WARNING, "Seed " + seed + " is longer than "
          + LONG_SIZE + " bytes but only the first " + LONG_SIZE
          + " bytes are used!");
    }

    ByteBuffer buf = ByteBuffer.allocate(LONG_SIZE);
    buf.position(Math.max(0, LONG_SIZE - length));
    buf.put(seed.seed, 0, Math.min(LONG_SIZE, length));
    buf.position(0);

    return buf.getLong();
  }

  @Override
  public int hashCode() {
    return hashCode;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj instanceof Seed) {
      Seed s = (Seed) obj;
      return Arrays.equals(seed, s.seed);
    }

    return false;
  }

}
