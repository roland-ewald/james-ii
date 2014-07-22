/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.math.random.reproducible;

/**
 * Implementations of this interface create reproducible {@link Seed}s based on
 * a base seed and an identifier.
 * 
 * @author Stefan Rybacki
 * 
 */
public interface ISeedPolicy {
  /**
   * Create a new {@link Seed} based on the {@value from} seed and the {@value
   * withName} identifier.
   * 
   * Important: For the same base seed and the same identifier equal
   * {@link Seed}s must be returned.
   * 
   * @param from
   *          the base seed to derive a new {@link Seed} from
   * @param withName
   *          identifier used to derive with
   * @return a new seed
   */
  Seed createSeed(Seed from, String withName);
}
