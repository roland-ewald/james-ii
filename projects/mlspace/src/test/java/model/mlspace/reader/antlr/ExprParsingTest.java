/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.reader.antlr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.logging.Level;

import junit.framework.TestCase;
import model.mlspace.entities.values.AbstractValueRange;

import org.antlr.runtime.RecognitionException;
import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Test {@link MLSpaceSmallParser}'s ability of parsing of numerical expressions
 * and value sets/ranges/intervals
 * 
 * @author Arne Bittig
 */
public class ExprParsingTest extends TestCase {

  private static final Map<String, Double> TEST_NUM_EXPRS =
      new LinkedHashMap<>();
  {
    TEST_NUM_EXPRS.put("1+2-3+4", 4.); // test normal evaluation order
    TEST_NUM_EXPRS.put("5e-1", 1. / 2.); // test power of 10 shift
    TEST_NUM_EXPRS.put("7 - 6/4/2 + 3", 9.25); // test mult/add priorities
    TEST_NUM_EXPRS.put("2^2^3", 256.); // test different evaluation order
    TEST_NUM_EXPRS.put("-1+2-(-4^2+5^2)^(+2^-1)", -2.); // test signs &
    // parentheses
    TEST_NUM_EXPRS.put("(2²)³", 64.); // test power shortcut ?!
    TEST_NUM_EXPRS.put("+PI^-.5", 0.564189584); // test PI keyword & DELTA
    // comparison
    TEST_NUM_EXPRS.put("[PI^+.5]^10", 1.); // test floor function
    TEST_NUM_EXPRS.put("/%44+#", Double.NaN); // test nonsense
    TEST_NUM_EXPRS.put("360°", 2 * Math.PI); // test degrees symbol
    TEST_NUM_EXPRS.put("(-(1-2)*3)*4", 12.); // parentheses
    TEST_NUM_EXPRS.put("(2>3)*0.5", 0.); // boolean-to-0-or-1
    TEST_NUM_EXPRS.put("(4>3)*0.5", 0.5); // boolean-to-0-or-1
    TEST_NUM_EXPRS.put("if 1<2 then 0.5", 0.5); // if-then without else
    TEST_NUM_EXPRS.put("if 1>2 then 0.5", 0.); // if-then without else
    TEST_NUM_EXPRS.put("if 1>2 then 0.5 else -0.5", -0.5); // if-then-else
  }

  private static final double DELTA = 1e-7;

  /**
   * @param toParse
   *          String to parse
   * @return Parser
   */
  public static MLSpaceSmallParser getParser(String toParse) {
    try {
      return new MLSpaceSmallParser(toParse, false);
    } catch (IOException e) {
      ApplicationLogger.log(Level.SEVERE, "Could not create input"
          + " stream from " + toParse, e);
      return null;
    }

  }

  /**
   * Test numerical expression parsing
   */
  public void testNumExprParsing() {
    Map<String, Double> actualResults =
        new LinkedHashMap<>(TEST_NUM_EXPRS.size());

    // calculate results for all test expressions first (for sysout)
    for (Map.Entry<String, Double> tee : TEST_NUM_EXPRS.entrySet()) {
      String toParse = tee.getKey();
      MLSpaceSmallParser parser = getParser(toParse);
      try {
        Double val = parser.numexpr().val;
        ApplicationLogger.log(Level.INFO, toParse + " is: " + val
            + ", should: " + tee.getValue());
        actualResults.put(toParse, val);
      } catch (NullPointerException npe) { // NOSONAR: NPE unavoidably
        // caused by auto-gen parser code (/%... -> div by null)
        ApplicationLogger.log(Level.INFO,
            toParse + " is NaN, should: " + tee.getValue());
        actualResults.put(toParse, null);
      } catch (RecognitionException e) {
        ApplicationLogger.log(e);
        fail("Recognition exception parsing " + toParse);
      }
    }

    // then, assert that the calculated results are correct
    for (Map.Entry<String, Double> are : actualResults.entrySet()) {
      Double valShould = TEST_NUM_EXPRS.get(are.getKey());
      Double valIs = are.getValue();
      if (valIs == null || valIs.isNaN()) {
        assertTrue(valShould == null || valShould.isNaN());
      } else {
        assertEquals(valShould, valIs, DELTA);
      }
    }
  }

  /**
   * Helper class to hold positive and negative examples for testing parsed
   * ranges, sets and intervals
   */
  private static class PosNeg<T> {
    private Collection<T> positive;

    private Collection<T> negative;

    private PosNeg() {
    }

    static <T> PosNeg<T> newPositive(@SuppressWarnings("unchecked") T... pos) {
      PosNeg<T> rv = new PosNeg<>();
      rv.positive = Arrays.asList(pos);
      return rv;
    }

    PosNeg<T> setNegative(@SuppressWarnings("unchecked") T... neg) {
      this.negative = Arrays.asList(neg);
      return this;
    }

    Collection<T> checkPositive(AbstractValueRange<?> valRange) {
      Collection<T> rv = new LinkedList<>(positive);
      for (Iterator<T> it = rv.iterator(); it.hasNext();) {
        T pos = it.next();
        if (valRange.contains(pos)) {
          it.remove();
        }
      }
      return rv;
    }

    Collection<T> checkNegative(AbstractValueRange<?> valRange) {
      Collection<T> rv = new ArrayList<>();
      for (T neg : negative) {
        if (valRange.contains(neg)) {
          rv.add(neg);
        }
      }
      return rv;
    }
  }

  private static final Map<String, PosNeg<?>> TEST_RANGE_EXPRS =
      new LinkedHashMap<>();
  {
    TEST_RANGE_EXPRS.put("{1,2,3}", PosNeg.newPositive(1.0, 2.0, 3.0)
        .setNegative(0.0, 4.0, 1.5));
    TEST_RANGE_EXPRS.put("0.5:0.5:4", PosNeg.newPositive(.5, 1.0, 3.5, 4.0)
        .setNegative(0.0, 4.1, 4.5));
    TEST_RANGE_EXPRS.put("1.5-1:0.5:3+1", PosNeg.newPositive(.5, 1.0, 3.5, 4.0)
        .setNegative(0.0, 4.1, 4.5));
    TEST_RANGE_EXPRS.put("0.5:4",
        PosNeg.newPositive(.5, 3.5).setNegative(1.0, 3.0, 4.0));
    TEST_RANGE_EXPRS.put("4.2",
        PosNeg.newPositive(4.2).setNegative(0., 4.0, 4.20000000001));
    TEST_RANGE_EXPRS.put("abc",
        PosNeg.newPositive("abc").setNegative(null, "ab"));
    TEST_RANGE_EXPRS.put("[0.5..4]", PosNeg.newPositive(.5, 3.5, 4.0)
        .setNegative(0.0, 4.1, 4.5));
  }

  public void testSetRangeIntervalParsing() throws RecognitionException {
    for (Map.Entry<String, PosNeg<?>> e : TEST_RANGE_EXPRS.entrySet()) {
      String toParse = e.getKey();
      ApplicationLogger.log(Level.INFO, "Parsing " + toParse);
      MLSpaceSmallParser parser = getParser(toParse);
      AbstractValueRange<?> valRange = parser.valset_or_const().val;
      Collection<?> falseNegative = e.getValue().checkPositive(valRange);
      assertTrue(falseNegative + " must be in " + valRange + " (parsed from "
          + toParse + ")", falseNegative.isEmpty());
      Collection<?> falsePositive = e.getValue().checkNegative(valRange);
      assertTrue(falsePositive + " must not be in " + valRange
          + " (parsed from " + toParse + ")", falsePositive.isEmpty());
    }
  }
}
