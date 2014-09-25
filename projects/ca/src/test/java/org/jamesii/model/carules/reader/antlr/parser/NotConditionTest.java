/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jamesii.ChattyTestRule;
import org.jamesii.model.cacore.INeighborStates;
import org.jamesii.model.carules.ICACondition;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Rybacki
 * 
 */
public class NotConditionTest extends ChattyTestRule {

  @Before
  public void setUp() throws Exception {

  }

  /**
   * Test method for
   * {@link org.jamesii.model.carules.reader.antlr.parser.NotCondition#isTrue(int, INeighborStates)}
   * .
   */
  @Test
  public final void testIsTrue() {
    long seed = (long) (Math.random() * Long.MAX_VALUE);
    addParameter("seed", seed);
    Random rnd = new Random(seed);

    // condition should always return true if initialized with false, and always
    // false if initialized with true
    ICACondition con = new NotCondition(new BooleanCondition(true));
    for (int i = 0; i < 1000; i++) {
      List<Integer> list = new ArrayList<>(8);
      for (int j = 0; j < 8; j++) {
        list.add((int) (rnd.nextDouble() * 1000));
      }
      assertFalse(con.isTrue(i, new FakeNeighborStates(list)));
    }

    con = new NotCondition(new BooleanCondition(false));
    for (int i = 0; i < 1000; i++) {
      List<Integer> list = new ArrayList<>(8);
      for (int j = 0; j < 8; j++) {
        list.add((int) (rnd.nextDouble() * 1000));
      }
      assertTrue(con.isTrue(i, new FakeNeighborStates(list)));
    }
  }

}
