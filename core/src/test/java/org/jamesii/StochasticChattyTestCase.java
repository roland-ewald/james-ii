/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii;

import java.util.logging.Level;

/**
 * This class provides various functionality to support stochastic testing. All
 * stochastic tests should inherit from it, as it also allows to set a fixed
 * random seed and thus making this viable for continuous integration.
 * 
 * @author Roland Ewald
 * 
 */
public abstract class StochasticChattyTestCase extends ChattyTestCase {

  /** The name of the property to set the seed. */
  public static final String SEED_PROPERTY = "org.jamesii.test.seed";

  /** Fixed seed if it is set, otherwise null. */
  public static final Long FIXED_SEED;
  static {
    FIXED_SEED = retrieveFixedSeed();
  }

  /**
   * If the seed shall be the same for every test function, call
   * <code>super.setUp()</code> in your class' <code>setUp</code> method.
   */
  @Override
  public void setUp() {
    setFixedSeed();
  }

  public StochasticChattyTestCase(String name) {
    super(name);
    setFixedSeed();
  }

  public StochasticChattyTestCase() {
    setFixedSeed();
  }

  /**
   * Sets the fixed seed to a concrete value.
   */
  private static void setFixedSeed() {
    if (FIXED_SEED == null) {
      SimSystem.report(Level.INFO,
          "No fixed seed for stochastic testing has been set.");
      return;
    }
    SimSystem.getRNGGenerator().setSeed(FIXED_SEED);
    SimSystem.report(Level.INFO, "Set fixed seed for stochastic testing to:"
        + FIXED_SEED);
  }

  /**
   * Retrieves fixed seed from Java properties.
   * 
   * @return the long
   */
  protected static Long retrieveFixedSeed() {
    String property = System.getProperty(SEED_PROPERTY);
    if (property != null) {
      try {
        return Long.valueOf(property);
      } catch (NumberFormatException ex) {
        SimSystem.report(Level.SEVERE,
            "Could not set fixed seeed, as it is not in Long number format: '"
                + property + "'", ex);
      }
    }
    return null;
  }

}
