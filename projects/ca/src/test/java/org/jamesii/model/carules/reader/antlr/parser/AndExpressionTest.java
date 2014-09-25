/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.carules.reader.antlr.parser;

/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */

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
public class AndExpressionTest extends ChattyTestRule {

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

    boolean expected = rnd.nextBoolean();

    AndExpression condition = new AndExpression(new BooleanCondition(expected));

    for (int k = 0; k < 1000; k++) {
      boolean c = rnd.nextBoolean();
      expected &= c;
      BooleanCondition con = new BooleanCondition(c);
      condition.addCondition(con);
    }

    for (int i = 0; i < 1000; i++) {
      List<Integer> list = new ArrayList<>(8);
      for (int j = 0; j < 8; j++) {
        list.add((int) (rnd.nextDouble() * 1000));
      }
      assertEquals(expected, condition.isTrue(i, new FakeNeighborStates(list)));
    }
  }

}
