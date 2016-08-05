/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.logging.Level;

import junit.framework.TestCase;
import model.mlspace.entities.values.AbstractValueRange;

import org.jamesii.core.util.logging.ApplicationLogger;

/**
 * Test for {@link AbstractValueRange}s and subclasses
 * 
 * @author Arne Bittig
 */
public class AttributeValueSetRangeOrIntervalTest extends TestCase {

  /** test containsAll */
  public final void testRangeIntervalAndSetInclusions() {
    AbstractValueRange<Integer> vSet123Int =
        AbstractValueRange.newSet(new HashSet<>(Arrays.asList(1, 2, 3)));
    AbstractValueRange<Double> vSet123Dbl =
        AbstractValueRange.newSet(new HashSet<>(Arrays.asList(1., 2., 3.)));
    AbstractValueRange<Integer> vRange123Int =
        AbstractValueRange.newRange(1, 1, 3);
    AbstractValueRange<Double> vRange123Dbl =
        AbstractValueRange.newRange(1.0, 1.0, 3.0);

    assertTrue(vRange123Int.containsAll(vSet123Int));
    assertTrue(vRange123Int.containsAll(vSet123Dbl));
    assertTrue(vRange123Dbl.containsAll(vSet123Int));
    assertTrue(vRange123Dbl.containsAll(vSet123Dbl));
    if (vSet123Int.containsAll(vRange123Int)) {
      ApplicationLogger.log(Level.INFO,
          "Integer ranges are not converted to Double");
    } else {
      ApplicationLogger.log(Level.INFO,
          "Integer ranges are still converted to Double");
    }
    assertTrue(vRange123Int.containsAll(vSet123Int));
    if (vRange123Int.containsAll(vSet123Dbl)) {
      ApplicationLogger.log(Level.INFO,
          "Integer range contains the eq. Double range");
    } else {
      ApplicationLogger.log(Level.INFO, "Integer range does not contain "
          + "the equivalent Double range");
    }

    AbstractValueRange<Double> vIntv1to3 = AbstractValueRange.newInterval(1, 3);
    assertTrue(vIntv1to3.containsAll(vSet123Int));
    assertTrue(vIntv1to3.containsAll(vSet123Dbl));
    assertTrue(vIntv1to3.containsAll(vRange123Int));
    assertTrue(vIntv1to3.containsAll(vRange123Dbl));
    assertFalse(vRange123Int.containsAll(vIntv1to3));
    assertFalse(vRange123Dbl.containsAll(vIntv1to3));
    assertFalse(vSet123Int.containsAll(vIntv1to3));

    AbstractValueRange<Double> vIntv2to2 =
        AbstractValueRange.newInterval(2., 2.);
    assertTrue(vSet123Dbl.containsAll(vIntv2to2));
    assertFalse(vSet123Int.containsAll(vIntv2to2));

    AbstractValueRange<Double> vIntv2to1 =
        AbstractValueRange.newInterval(2., 1.);
    assertTrue(vSet123Dbl.containsAll(vIntv2to1));
    // empty set is contained in every other
    assertTrue(vSet123Int.containsAll(vIntv2to1));

    // TODO: interval overlap
  }

  // public final void testRandomElementDetermination() {
  // // TODO
  // }
}
