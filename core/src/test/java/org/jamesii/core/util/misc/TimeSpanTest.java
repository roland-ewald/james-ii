/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

import org.jamesii.core.util.misc.TimeSpan;

import junit.framework.TestCase;

/**
 * Tests the {@link TimeSpan} class.
 * 
 * @author Johannes Rössel
 */
public class TimeSpanTest extends TestCase {

  /**
   * Checks whether a {@link TimeSpan}'s fields match the expected values
   * 
   * @param ts
   *          The {@link TimeSpan} instance to check.
   * @param days
   *          The expected value for the days field.
   * @param hours
   *          The expected value for the hours field.
   * @param minutes
   *          The expected value for the minutes field.
   * @param seconds
   *          The expected value for the seconds field.
   * @param milliseconds
   *          The expected value for the milliseconds field.
   * @param microseconds
   *          The expected value for the microseconds field.
   * @param nanoseconds
   *          The expected value for the nanoseconds field.
   * @param totalNanoseconds
   *          The total number of nanoseconds stored in the {@link TimeSpan}.
   */
  private void checkFields(TimeSpan ts, int days, int hours, int minutes,
      int seconds, int milliseconds, int microseconds, int nanoseconds,
      long totalNanoseconds) {
    assertEquals("Days didn't match", days, ts.getDays());
    assertEquals("Hours didn't match", hours, ts.getHours());
    assertEquals("Minutes didn't match", minutes, ts.getMinutes());
    assertEquals("Seconds didn't match", seconds, ts.getSeconds());
    assertEquals("Milliseconds didn't match", milliseconds,
        ts.getMilliseconds());
    assertEquals("Microseconds didn't match", microseconds,
        ts.getMicroseconds());
    assertEquals("Nanoseconds didn't match", nanoseconds, ts.getNanoseconds());

    assertEquals("Total nanoseconds didn't match", totalNanoseconds,
        ts.getTotalNanoseconds());
  }

  /** Tests the constructor {@link TimeSpan#TimeSpan(int, int, int)}. */
  public void testCtorInt3() {
    checkFields(new TimeSpan(0, 0, 0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(new TimeSpan(3, 5, 7), 0, 3, 5, 7, 0, 0, 0, 11107000000000L);
    // overflow into days
    checkFields(new TimeSpan(24, 0, 0), 1, 0, 0, 0, 0, 0, 0, 86400000000000L);
    checkFields(new TimeSpan(25, 1, 2), 1, 1, 1, 2, 0, 0, 0, 90062000000000L);
    // overflow into hours
    checkFields(new TimeSpan(0, 60, 0), 0, 1, 0, 0, 0, 0, 0, 3600000000000L);
    checkFields(new TimeSpan(0, 61, 28), 0, 1, 1, 28, 0, 0, 0, 3688000000000L);
    // overflow into minutes
    checkFields(new TimeSpan(0, 0, 60), 0, 0, 1, 0, 0, 0, 0, 60000000000L);
    checkFields(new TimeSpan(1, 2, 63), 0, 1, 3, 3, 0, 0, 0, 3783000000000L);

    checkFields(new TimeSpan(0, 60, 60), 0, 1, 1, 0, 0, 0, 0, 3660000000000L);
    checkFields(new TimeSpan(24, 60, 60), 1, 1, 1, 0, 0, 0, 0, 90060000000000L);
    checkFields(new TimeSpan(48, 128, 720), 2, 2, 20, 0, 0, 0, 0,
        181200000000000L);
  }

  /**
   * Tests the constructor
   * {@link TimeSpan#TimeSpan(int, int, int, int, int, int, int)}.
   */
  public void testCtorInt7() {
    checkFields(new TimeSpan(0, 0, 0, 0, 0, 0, 0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(new TimeSpan(2, 3, 5, 7, 11, 13, 17), 2, 3, 5, 7, 11, 13, 17,
        183907011013017L);
  }

  /** Tests the constructor {@link TimeSpan#TimeSpan(long)}. */
  public void testCtorLong() {
    checkFields(new TimeSpan(0L), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(new TimeSpan(1L), 0, 0, 0, 0, 0, 0, 1, 1L);
    checkFields(new TimeSpan(181200000000000L), 2, 2, 20, 0, 0, 0, 0,
        181200000000000L);
    checkFields(new TimeSpan(3783000000000L), 0, 1, 3, 3, 0, 0, 0,
        3783000000000L);
    checkFields(new TimeSpan(123L), 0, 0, 0, 0, 0, 0, 123, 123L);
    checkFields(new TimeSpan(1234L), 0, 0, 0, 0, 0, 1, 234, 1234L);
    checkFields(new TimeSpan(123456L), 0, 0, 0, 0, 0, 123, 456, 123456L);
  }

  /** Tests the {@link TimeSpan#fromDays(double)} method. */
  public void testFromDays() {
    checkFields(TimeSpan.fromDays(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromDays(1), 1, 0, 0, 0, 0, 0, 0, 86400000000000L);

    checkFields(TimeSpan.fromDays(1.5), 1, 12, 0, 0, 0, 0, 0, 129600000000000L);
    checkFields(TimeSpan.fromDays(1.25), 1, 6, 0, 0, 0, 0, 0, 108000000000000L);
    checkFields(TimeSpan.fromDays(1.235), 1, 5, 38, 24, 0, 0, 0,
        106704000000000L);
    checkFields(TimeSpan.fromDays(1.2352), 1, 5, 38, 41, 280, 0, 0,
        106721280000000L);
    checkFields(TimeSpan.fromDays(1.2352128764), 1, 5, 38, 42, 392, 520, 960,
        106722392520960L);
  }

  /** Tests the {@link TimeSpan#fromHours(double)} method. */
  public void testFromHours() {
    checkFields(TimeSpan.fromHours(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromHours(5), 0, 5, 0, 0, 0, 0, 0, 18000000000000L);

    checkFields(TimeSpan.fromHours(5.5), 0, 5, 30, 0, 0, 0, 0, 19800000000000L);
    checkFields(TimeSpan.fromHours(5.75), 0, 5, 45, 0, 0, 0, 0, 20700000000000L);
    checkFields(TimeSpan.fromHours(5.29823523512), 0, 5, 17, 53, 646, 846, 432,
        19073646846432L);

    checkFields(TimeSpan.fromHours(25), 1, 1, 0, 0, 0, 0, 0, 90000000000000L);
    checkFields(TimeSpan.fromHours(28), 1, 4, 0, 0, 0, 0, 0, 100800000000000L);
  }

  /** Tests the {@link TimeSpan#fromMinutes(double)} method. */
  public void testFromMinutes() {
    checkFields(TimeSpan.fromMinutes(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromMinutes(15), 0, 0, 15, 0, 0, 0, 0, 900000000000L);

    checkFields(TimeSpan.fromMinutes(15.2), 0, 0, 15, 12, 0, 0, 0,
        912000000000L);
    checkFields(TimeSpan.fromMinutes(15.2346), 0, 0, 15, 14, 76, 0, 0,
        914076000000L);
    checkFields(TimeSpan.fromMinutes(15.2123235456), 0, 0, 15, 12, 739, 412,
        736, 912739412736L);

    checkFields(TimeSpan.fromMinutes(150), 0, 2, 30, 0, 0, 0, 0, 9000000000000L);
    checkFields(TimeSpan.fromMinutes(8000), 5, 13, 20, 0, 0, 0, 0,
        480000000000000L);
  }

  /** Tests the {@link TimeSpan#fromSeconds(double)} method. */
  public void testFromSeconds() {
    checkFields(TimeSpan.fromSeconds(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromSeconds(10), 0, 0, 0, 10, 0, 0, 0, 10000000000L);

    checkFields(TimeSpan.fromSeconds(10.5), 0, 0, 0, 10, 500, 0, 0,
        10500000000L);
    checkFields(TimeSpan.fromSeconds(10.897), 0, 0, 0, 10, 897, 0, 0,
        10897000000L);
    checkFields(TimeSpan.fromSeconds(3.234985), 0, 0, 0, 3, 234, 985, 0,
        3234985000L);
    checkFields(TimeSpan.fromSeconds(3.234985123), 0, 0, 0, 3, 234, 985, 123,
        3234985123L);

    checkFields(TimeSpan.fromSeconds(300), 0, 0, 5, 0, 0, 0, 0, 300000000000L);
    checkFields(TimeSpan.fromSeconds(1450), 0, 0, 24, 10, 0, 0, 0,
        1450000000000L);
    checkFields(TimeSpan.fromSeconds(14500), 0, 4, 1, 40, 0, 0, 0,
        14500000000000L);
    checkFields(TimeSpan.fromSeconds(145000), 1, 16, 16, 40, 0, 0, 0,
        145000000000000L);
  }

  /** Tests the {@link TimeSpan#fromMilliseconds(double)} method. */
  public void testFromMilliseconds() {
    checkFields(TimeSpan.fromMilliseconds(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromMilliseconds(1), 0, 0, 0, 0, 1, 0, 0, 1000000L);
    checkFields(TimeSpan.fromMilliseconds(125), 0, 0, 0, 0, 125, 0, 0,
        125000000L);

    checkFields(TimeSpan.fromMilliseconds(1.5), 0, 0, 0, 0, 1, 500, 0, 1500000L);
    checkFields(TimeSpan.fromMilliseconds(1.25), 0, 0, 0, 0, 1, 250, 0,
        1250000L);
    checkFields(TimeSpan.fromMilliseconds(1.263), 0, 0, 0, 0, 1, 263, 0,
        1263000L);
    checkFields(TimeSpan.fromMilliseconds(1.26315), 0, 0, 0, 0, 1, 263, 150,
        1263150L);
    checkFields(TimeSpan.fromMilliseconds(1.263157), 0, 0, 0, 0, 1, 263, 157,
        1263157L);

    checkFields(TimeSpan.fromMilliseconds(1256), 0, 0, 0, 1, 256, 0, 0,
        1256000000L);
    checkFields(TimeSpan.fromMilliseconds(81457), 0, 0, 1, 21, 457, 0, 0,
        81457000000L);
  }

  /** Tests the {@link TimeSpan#fromMicroseconds(double)} method. */
  public void testFromMicroseconds() {
    checkFields(TimeSpan.fromMicroseconds(0), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromMicroseconds(15), 0, 0, 0, 0, 0, 15, 0, 15000L);
    checkFields(TimeSpan.fromMicroseconds(731), 0, 0, 0, 0, 0, 731, 0, 731000L);

    checkFields(TimeSpan.fromMicroseconds(1.153), 0, 0, 0, 0, 0, 1, 153, 1153L);
    checkFields(TimeSpan.fromMicroseconds(1.1), 0, 0, 0, 0, 0, 1, 100, 1100L);

    checkFields(TimeSpan.fromMicroseconds(1153), 0, 0, 0, 0, 1, 153, 0,
        1153000L);
    checkFields(TimeSpan.fromMicroseconds(1216153), 0, 0, 0, 1, 216, 153, 0,
        1216153000L);
  }

  /** Tests the {@link TimeSpan#fromNanoseconds(long)} method. */
  public void testFromNanoseconds() {
    checkFields(TimeSpan.fromNanoseconds(0L), 0, 0, 0, 0, 0, 0, 0, 0L);
    checkFields(TimeSpan.fromNanoseconds(1L), 0, 0, 0, 0, 0, 0, 1, 1L);
    checkFields(TimeSpan.fromNanoseconds(181200000000000L), 2, 2, 20, 0, 0, 0,
        0, 181200000000000L);
    checkFields(TimeSpan.fromNanoseconds(3783000000000L), 0, 1, 3, 3, 0, 0, 0,
        3783000000000L);
    checkFields(TimeSpan.fromNanoseconds(123L), 0, 0, 0, 0, 0, 0, 123, 123L);
    checkFields(TimeSpan.fromNanoseconds(1234L), 0, 0, 0, 0, 0, 1, 234, 1234L);
    checkFields(TimeSpan.fromNanoseconds(123456L), 0, 0, 0, 0, 0, 123, 456,
        123456L);
  }

  /** Tests the {@link TimeSpan#getTotalDays()} method. */
  public void testGetTotalDays() {
    double[] values =
        new double[] { 2657d, 1.5, 1.2534, 26.26357, 1.2352128764 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromDays(d).getTotalDays());
    }
  }

  /** Tests the {@link TimeSpan#getTotalHours()} method. */
  public void testGetTotalHours() {
    double[] values = new double[] { 0, 5, 5.5, 5.75, 25, 28, 5.29823523512 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromHours(d).getTotalHours());
    }
  }

  /** Tests the {@link TimeSpan#getTotalMinutes()} method. */
  public void testGetTotalMinutes() {
    double[] values =
        new double[] { 0, 15, 15.2, 15.2346, 15.2123235456, 150, 8000 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromMinutes(d).getTotalMinutes());
    }
  }

  /** Tests the {@link TimeSpan#getTotalSeconds()} method. */
  public void testGetTotalSeconds() {
    double[] values =
        new double[] { 0, 10, 10.5, 10.879, 3.234985, 3.234985123, 300, 1450,
            145000, 145000 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromSeconds(d).getTotalSeconds());
    }
  }

  /** Tests the {@link TimeSpan#getTotalMilliseconds()} method. */
  public void testGetTotalMilliseconds() {
    double[] values =
        new double[] { 0, 1, 125, 1.5, 1.25, 1.263, 1.26315, 1.263157, 1256,
            81457 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromMilliseconds(d).getTotalMilliseconds());
    }
  }

  /** Tests the {@link TimeSpan#getTotalMicroseconds()} method. */
  public void testGetTotalMicroseconds() {
    double[] values = new double[] { 0, 15, 731, 1.153, 1.1, 1153, 1216153 };
    for (double d : values) {
      assertEquals(d, TimeSpan.fromMicroseconds(d).getTotalMicroseconds());
    }
  }

  /** Tests the {@link TimeSpan#getTotalNanoseconds()} method. */
  public void testGetTotalNanoseconds() {
    long[] values =
        new long[] { 0L, 1L, 181200000000000L, 3783000000000L, 123L, 1234L,
            123456L };
    for (long v : values) {
      assertEquals(v, TimeSpan.fromNanoseconds(v).getTotalNanoseconds());
    }
  }

  /** Tests the {@link TimeSpan#compareTo(TimeSpan)} method. */
  public void testCompareTo() {
    assertEquals(-1,
        (int) Math.signum(new TimeSpan(123).compareTo(new TimeSpan(456))));
    assertEquals(1,
        (int) Math.signum(new TimeSpan(456).compareTo(new TimeSpan(123))));
    assertEquals(0, new TimeSpan(123).compareTo(new TimeSpan(123)));
  }

  /** Tests the {@link TimeSpan#equals(Object)} method. */
  public void testEquals() {
    assertEquals(new TimeSpan(123), new TimeSpan(123));
    assertFalse(new TimeSpan(123).equals(new TimeSpan(124)));
    assertFalse(new TimeSpan(123).equals("123"));
    assertFalse("abc".equals(new TimeSpan(123)));
    assertFalse(new TimeSpan(0).equals(null));
  }

  /** Tests the {@link TimeSpan#hashCode()} method. */
  public void testHashCode() {
    Object[] o =
        new Object[] { new TimeSpan(235), new TimeSpan(28735),
            new TimeSpan(23498934867394L), new TimeSpan(358923), "abc", "def",
            Integer.valueOf(153) };

    for (Object a : o) {
      for (Object b : o) {
        if (a.equals(b)) {
          assertEquals(a.hashCode(), b.hashCode());
        }

        if (a.hashCode() != b.hashCode()) {
          assertFalse(a.equals(b));
        }
      }
    }
  }

  /**
   * Tests the {@link TimeSpan#toString()} method.
   */
  public void testToString() {
    assertEquals("00:00:00", new TimeSpan(0, 0, 0).toString());
    assertEquals("01:02:03", new TimeSpan(1, 2, 3).toString());
    assertEquals("12:23:45", new TimeSpan(12, 23, 45).toString());
    assertEquals("23:59:00", new TimeSpan(0, 1439, 0).toString());

    // days
    assertEquals("1.00:00:00", new TimeSpan(24, 0, 0).toString());
    assertEquals("1.01:01:02", new TimeSpan(25, 1, 2).toString());
    assertEquals("1.00:00:00", new TimeSpan(0, 1440, 0).toString());

    // fractional seconds
    assertEquals("00:00:00.123000000",
        new TimeSpan(0, 0, 0, 0, 123, 0, 0).toString());
    assertEquals("00:00:00.123456000",
        new TimeSpan(0, 0, 0, 0, 123, 456, 0).toString());
    assertEquals("00:00:00.123125765",
        new TimeSpan(0, 0, 0, 0, 123, 125, 765).toString());
    assertEquals("00:00:00.123125000",
        new TimeSpan(0, 0, 0, 0, 123, 125, 0).toString());
    assertEquals("00:00:00.123000000",
        new TimeSpan(0, 0, 0, 0, 123, 0, 0).toString());
    assertEquals("00:00:00.000007823", new TimeSpan(7823L).toString());
    assertEquals("00:00:00.008723568", new TimeSpan(8723568L).toString());
    assertEquals("00:00:00.000982432", new TimeSpan(982432L).toString());

    // both
    assertEquals("25.13:45:01.841893342", new TimeSpan(25, 13, 45, 1, 841, 893,
        342).toString());
  }
}
