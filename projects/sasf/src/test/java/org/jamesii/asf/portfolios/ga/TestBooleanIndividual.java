/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.portfolios.ga;

import junit.framework.TestCase;

import org.jamesii.core.util.misc.Pair;

/**
 * Simple tests for the recombination of {@link BooleanIndividual}.
 * 
 * @author Roland Ewald
 * @author Rene Schulz
 * 
 */
public class TestBooleanIndividual extends TestCase {

  /**
   * Test for {@link GenePool#recombineGenomes(int, boolean[], boolean[])}.
   */
  public void testGenomeRecombination() {

    // Recombine 0000 and 1111 at position 3 (= index 2)
    Pair<boolean[], boolean[]> recombinedGenomes = BooleanIndividual
        .recombineGenomes(2, new boolean[] { false, false, false, false },
            new boolean[] { true, true, true, true });

    // Check results:
    boolean[] genome1 = recombinedGenomes.getFirstValue();
    assertTrue("Genome one should be '0011'", !genome1[0]
        && !genome1[1] && genome1[2] && genome1[3]);

    boolean[] genome2 = recombinedGenomes.getSecondValue();
    assertTrue("Genome two should be '1100'", genome2[0] && genome2[1]
        && !genome2[2] && !genome2[3]);

  }
}
