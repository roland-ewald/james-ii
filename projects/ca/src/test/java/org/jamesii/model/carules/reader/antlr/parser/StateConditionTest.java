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
import org.junit.Before;
import org.junit.Test;

/**
 * @author Stefan Rybacki
 * 
 */
public class StateConditionTest extends ChattyTestRule {

  @Before
  public void setUp() throws Exception {

  }

  /**
   * Test method for
   * {@link org.jamesii.model.carules.reader.antlr.parser.AndExpression#isTrue(int, INeighborStates)}
   * .
   */
  @Test
  public final void testIsTrue() {
    long seed = (long) (Math.random() * Long.MAX_VALUE);
    addParameter("seed", seed);
    Random rnd = new Random(seed);

    for (int i = 0; i < 1000; i++) {
      int state = rnd.nextInt(1000);
      int min = rnd.nextInt(1000);
      int max = rnd.nextInt(1000);
      if (min > max) {
        int temp = min;
        min = max;
        max = temp;
      }
      StateCondition condition = new StateCondition(state, min, max);

      List<Integer> list = new ArrayList<>(8);
      int c = 0;
      for (int j = 0; j < 8; j++) {
        int a = rnd.nextInt(1000);
        if (a == state) {
          c++;
        }
        list.add(a);
      }

      assertEquals(c >= min && c <= max,
          condition.isTrue(i, new FakeNeighborStates(list)));
    }

  }

}
