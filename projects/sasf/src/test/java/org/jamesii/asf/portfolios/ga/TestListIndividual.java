/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;


import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.portfolios.ga.IIndividual;
import org.jamesii.asf.portfolios.ga.ListIndividual;
import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.math.random.generators.java.JavaRandom;
import org.jamesii.core.util.misc.Pair;

import junit.framework.TestCase;

/**
 * Simple tests for the recombination of {@link ListIndividual}. Check result
 * manually.
 * 
 * @author Roland Ewald
 * @author Rene Schulz
 * 
 */
public class TestListIndividual extends TestCase {

  /**
   * Test genome recombination.
   */
  public void testGenomeRecombination() {

    IRandom rng = new JavaRandom();

    // Recombine 0000 and 1111
    ListIndividual parent1 = new ListIndividual(null, 4, rng);
    ListIndividual parent2 = new ListIndividual(null, 4, rng);
    parent1.generateRandomGenome(0, 4);
    parent2.generateRandomGenome(4, 4);
    boolean[] genome1 = parent1.getBooleanRepresentation();
    boolean[] genome2 = parent2.getBooleanRepresentation();
    for (int i = 0; i < 4; i++) {
      SimSystem.report(Level.INFO, genome1[i] + "  " + genome2[i]);
    }

    Pair<IIndividual, IIndividual> children = parent1.recombine(parent2);

    // Print results
    genome1 = children.getFirstValue().getBooleanRepresentation();
    genome2 = children.getSecondValue().getBooleanRepresentation();
    for (int i = 0; i < 4; i++) {
      SimSystem.report(Level.INFO, genome1[i] + "  " + genome2[i]);
    }

  }

}
