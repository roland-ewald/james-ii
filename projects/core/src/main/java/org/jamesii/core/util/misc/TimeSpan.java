/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.misc;

/**
 * Class for representing time ranges. This class keeps its internal duration as
 * a single counter of nanoseconds. It is primarily intended for representing
 * time measurements due to the inability to represent more than 292 years.
 * <p>
 * Instances of this class are immutable.
 * <p>
 * TODO: Support for negative time spans.
 * 
 * @author Johannes Rössel
 * 
 * @see System#nanoTime()
 */
public class TimeSpan implements Comparable<TimeSpan> {

  /**
   * Hours per day.
   */
  private static final long HOURSPERDAY = 24L;

  /**
   * Minutes per hour.
   */
  private static final long MINUTESPERHOUR = 60L;

  /**
   * Seconds per minute.
   */
  private static final long SECONDSPERMINUTE = 60L;

  /**
   * Nanoseconds per day.
   */
  private static final long DAY = 86400000000000L;

  /**
   * Nanoseconds per hour.
   */
  private static final long HOUR = 3600000000000L;

  /**
   * Nanoseconds per minute.
   */
  private static final long MINUTE = 60000000000L;

  /**
   * Nanoseconds per second.
   */
  private static final long SECOND = 1000000000L;

  /**
   * Nanoseconds per millisecond.
   */
  private static final long MILLISECOND = 1000000L;

  /**
   * Nanoseconds per microsecond.
   */
  private static final long MICROSECOND = 1000L;

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of days.
   * 
   * @param days
   *          The number of days. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of days.
   */
  public static TimeSpan fromDays(double days) {
    return new TimeSpan((long) (days * DAY));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of hours.
   * 
   * @param hours
   *          The number of hours. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of hours.
   */
  public static TimeSpan fromHours(double hours) {
    return new TimeSpan((long) (hours * HOUR));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of minutes.
   * 
   * @param minutes
   *          The number of minutes. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of minutes.
   */
  public static TimeSpan fromMinutes(double minutes) {
    return new TimeSpan((long) (minutes * MINUTE));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of seconds.
   * 
   * @param seconds
   *          The number of seconds. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of seconds.
   */
  public static TimeSpan fromSeconds(double seconds) {
    return new TimeSpan((long) (seconds * SECOND));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of milliseconds.
   * 
   * @param milliseconds
   *          The number of milliseconds. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of milliseconds.
   */
  public static TimeSpan fromMilliseconds(double milliseconds) {
    return new TimeSpan((long) (milliseconds * MILLISECOND));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of microseconds.
   * 
   * @param microseconds
   *          The number of microseconds. Can be fractional.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of microseconds.
   */
  public static TimeSpan fromMicroseconds(double microseconds) {
    return new TimeSpan((long) (microseconds * MICROSECOND));
  }

  /**
   * Creates a new {@link TimeSpan} instance representing the specified number
   * of nanoseconds.
   * 
   * @param nanoseconds
   *          The number of nanoseconds.
   * @return A new {@link TimeSpan} instance, representing the specified number
   *         of nanoseconds.
   */
  public static TimeSpan fromNanoseconds(long nanoseconds) {
    return new TimeSpan(nanoseconds);
  }

  /** Internal counter of nanoseconds representing this time span. */
  private long nanoseconds;

  /**
   * Initialises a new instance of the {@link TimeSpan} class using the
   * specified number of hours, minutes and seconds.
   * 
   * @param hours
   *          The number of hours.
   * @param minutes
   *          The number of minutes.
   * @param seconds
   *          The number of seconds.
   */
  public TimeSpan(int hours, int minutes, int seconds) {
    this(0, hours, minutes, seconds, 0, 0, 0);
  }

  /**
   * Initialises a new instance of the {@link TimeSpan} class using the
   * specified number of days, hours, minutes, seconds, milliseconds,
   * microseconds and nanoseconds.
   * 
   * @param days
   *          The number of days.
   * @param hours
   *          The number of hours.
   * @param minutes
   *          The number of minutes.
   * @param seconds
   *          The number of seconds.
   * @param milliseconds
   *          The number of milliseconds.
   * @param microseconds
   *          The number of microseconds.
   * @param nanoseconds
   *          The number of nanoseconds.
   */
  public TimeSpan(int days, int hours, int minutes, int seconds,
      int milliseconds, int microseconds, int nanoseconds) {
    this.nanoseconds =
        (((((days * HOURSPERDAY + hours) * MINUTESPERHOUR + minutes)
            * SECONDSPERMINUTE + seconds) * 1000L + milliseconds) * 1000L + microseconds)
            * 1000L + nanoseconds;
  }

  /**
   * Initialises a new instance of the {@link TimeSpan} class, using the
   * specified number of nanoseconds as the interval to represent.
   * 
   * @param nanoseconds
   *          The number of nanoseconds.
   */
  public TimeSpan(long nanoseconds) {
    this.nanoseconds = nanoseconds;
  }

  /**
   * Gets the number of days in this time span. This only considers complete
   * days.
   * 
   * @return The number of days.
   * 
   * @see #getTotalDays()
   */
  public int getDays() {
    return (int) (nanoseconds / DAY);
  }

  /**
   * Gets the number of hours in this time span. This only considers complete
   * hours up to a day.
   * 
   * @return The number of hours.
   * 
   * @see #getTotalHours()
   */
  public int getHours() {
    return (int) (nanoseconds / HOUR % HOURSPERDAY);
  }

  /**
   * Gets the number of minutes in this time span. This only considers complete
   * minutes up to an hour.
   * 
   * @return The number of minutes.
   * 
   * @see #getTotalMinutes()
   */
  public int getMinutes() {
    return (int) (nanoseconds / MINUTE % MINUTESPERHOUR);
  }

  /**
   * Gets the number of seconds in this time span. This only considers complete
   * seconds, up to a minute.
   * 
   * @return The number of seconds.
   * 
   * @see #getTotalSeconds()
   */
  public int getSeconds() {
    return (int) (nanoseconds / SECOND % SECONDSPERMINUTE);
  }

  /**
   * Gets the number of milliseconds in this time span. This only considers
   * complete milliseconds, up to a second.
   * 
   * @return The number of milliseconds.
   * 
   * @see #getTotalMilliseconds()
   */
  public int getMilliseconds() {
    return (int) (nanoseconds / MILLISECOND % 1000);
  }

  /**
   * Gets the number of microseconds in this time span. This only considers
   * complete microseconds, up to a millisecond.
   * 
   * @return The number of microseconds.
   * 
   * @see #getTotalMicroseconds()
   */
  public int getMicroseconds() {
    return (int) (nanoseconds / MICROSECOND % 1000);
  }

  /**
   * Gets the number of nanoseconds in this time span. This only considers
   * complete nanoseconds, up to a microsecond.
   * 
   * @return The number of nanoseconds.
   * 
   * @see #getTotalNanoseconds()
   */
  public int getNanoseconds() {
    return (int) (nanoseconds % 1000);
  }

  /**
   * Gets the total number of days represented by this {@link TimeSpan}. This
   * includes the fractional part.
   * 
   * @return The total number of days.
   */
  public double getTotalDays() {
    return nanoseconds / Double.valueOf(DAY);
  }

  /**
   * Gets the total number of hours represented by this {@link TimeSpan}. This
   * includes the fractional part.
   * 
   * @return The total number of hours.
   */
  public double getTotalHours() {
    return nanoseconds / Double.valueOf(HOUR);
  }

  /**
   * Gets the total number of minutes represented by this {@link TimeSpan}. This
   * includes the fractional part.
   * 
   * @return The total number of minutes.
   */
  public double getTotalMinutes() {
    return nanoseconds / Double.valueOf(MINUTE);
  }

  /**
   * Gets the total number of seconds represented by this {@link TimeSpan}. This
   * includes the fractional part.
   * 
   * @return The total number of seconds.
   */
  public double getTotalSeconds() {
    return nanoseconds / Double.valueOf(SECOND);
  }

  /**
   * Gets the total number of milliseconds represented by this {@link TimeSpan}.
   * This includes the fractional part.
   * 
   * @return The total number of milliseconds.
   */
  public double getTotalMilliseconds() {
    return nanoseconds / Double.valueOf(MILLISECOND);
  }

  /**
   * Gets the total number of microseconds represented by this {@link TimeSpan}.
   * This includes the fractional part.
   * 
   * @return The total number of microseconds.
   */
  public double getTotalMicroseconds() {
    return nanoseconds / Double.valueOf(MICROSECOND);
  }

  /**
   * Gets the total number of nanoseconds represented by this {@link TimeSpan}.
   * 
   * @return The total number of nanoseconds.
   */
  public long getTotalNanoseconds() {
    return nanoseconds;
  }

  @Override
  public int compareTo(TimeSpan o) {
    long result = this.nanoseconds - o.getTotalNanoseconds();
    return result < 0 ? -1 : result > 0 ? 1 : 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null) {
      return false;
    }

    if (!(obj instanceof TimeSpan)) {
      return false;
    }

    return ((TimeSpan) obj).getTotalNanoseconds() == this.nanoseconds;
  }

  @Override
  public int hashCode() {
    return (int) ((int) getTotalNanoseconds() + getTotalMicroseconds()
        + getTotalMilliseconds() + getTotalSeconds() + getTotalMinutes()
        + getTotalHours() + getTotalDays());
  }

  /**
   * Zero-pads an number to a given number of digits.
   * 
   * @param num
   *          The number to pad.
   * @param width
   *          The total width.
   * @return A string, representing the given number. If the string
   *         representation of the integer needs less digits than {@code width},
   *         then it will be padded with zeroes on the left.
   */
  String intToStr(long num, int width) {
    StringBuilder sb = new StringBuilder(width);

    String s = Long.toString(num);

    while (sb.length() < width - s.length()) {
      sb.append('0');
    }

    sb.append(num);

    return sb.toString();
  }

  /**
   * Converts this {@link TimeSpan} into a readable string representation. This
   * representation isn't based on a specific locale and won't change when the
   * locale changes.
   * 
   * @return A string representation of this {@link TimeSpan}.
   */
  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();

    int d = getDays();
    long ns =
        (getMilliseconds() * 1000 + getMicroseconds()) * 1000
            + getNanoseconds();

    if (d > 0) {
      sb.append(d);
      sb.append('.');
    }

    sb.append(intToStr(getHours(), 2));
    sb.append(':');
    sb.append(intToStr(getMinutes(), 2));
    sb.append(':');
    sb.append(intToStr(getSeconds(), 2));

    if (ns > 0) {
      sb.append('.');
      sb.append(intToStr(ns, 9));
    }

    return sb.toString();
  }

}
