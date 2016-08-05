/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.observe.comparing;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.comparing.TimeInformationExtractor.TimeAndHintInfo;
import org.jamesii.core.util.ITime;

/**
 * Extract time from {@link IObservable observable} that also implements
 * {@link ITime} and store hint or a flag for hint presence (customizable).
 *
 * @author Arne Bittig
 * @param <O>
 *          Type of observable that implements {@link ITime}, e.g. a processor
 * @date 25.09.2012
 * @see EqualityObserver
 */
public class TimeInformationExtractor<O extends ITime<? extends Comparable<?>> & IObservable>
    implements EqualityObserver.InformationExtractor<O, TimeAndHintInfo>,
    java.io.Serializable {

  private static final long serialVersionUID = -1885254976485548546L;

  /**
   * Ways to deal with hints passed to {@link IObservable#changed(Object)}
   * 
   * @author Arne Bittig
   * @date 25.09.2012
   */
  public static enum HintPolicy {
    /**
     * Ignore, i.e. do not differentiate between calls to
     * {@link IObservable#changed()} and {@link IObservable#changed(Object)}
     */
    IGNORE,
    /**
     * Record whether a hint was present or not, but do not keep hint itself
     */
    CHECK_PRESENCE,
    /**
     * Call {@link Object#toString() toString} on hint, compare string
     * representations
     */
    USE_TOSTRING,
    /**
     * Keep (strong) reference to hint and use in {@link Object#equals(Object)
     * comparisons}
     */
    KEEP_AND_COMPARE
  }

  private final HintPolicy hintPolicy;

  /**
   * {@link TimeInformationExtractor} checking for hint absence or presence, but
   * not content
   */
  public TimeInformationExtractor() {
    this(HintPolicy.CHECK_PRESENCE);
  }

  /**
   * New {@link TimeInformationExtractor} with given policy towards
   * {@link IObservable#changed(Object) hints}
   * 
   * @param hintPolicy
   *          Specification how to deal with hints
   */
  public TimeInformationExtractor(HintPolicy hintPolicy) {
    if (hintPolicy == null) {
      throw new IllegalArgumentException("Use no-args constructor"
          + " for hint policy with default value, not null!");
    }
    this.hintPolicy = hintPolicy;
  }

  @Override
  public TimeAndHintInfo extractInformation(O observable) {
    return new TimeAndHintInfo(observable.getTime(), false);
  }

  @Override
  public TimeAndHintInfo extractInformation(O observable, Object hint) {
    switch (hintPolicy) {
    case IGNORE:
      return new TimeAndHintInfo(observable.getTime(), false);
    case CHECK_PRESENCE:
      return new TimeAndHintInfo(observable.getTime(), true);
    case KEEP_AND_COMPARE:
      return new TimeAndHintInfo(observable.getTime(), hint);
    case USE_TOSTRING:
      return new TimeAndHintInfo(observable.getTime(), hint.toString());
    default:
      throw new IllegalStateException();
    }
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + (hintPolicy == null ? 0 : hintPolicy.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!(obj instanceof TimeInformationExtractor)) {
      return false;
    }
    TimeInformationExtractor<?> other = (TimeInformationExtractor<?>) obj;
    if (hintPolicy != other.hintPolicy) {
      return false;
    }
    return true;
  }

  /**
   * Container for time and (optionally) hint. Also contains a flag whether a
   * hint was present at all. (Using a pair of time and hint and "null" for
   * "no hint" would not allow distinction between "no hint" and
   * "hint was present but null".)
   * 
   * Subclasses of {@link TimeInformationExtractor} can extract information from
   * the observable and/or the hint and use this class as information container,
   * but pass the extracted information to the constructor instead of the
   * original hint. Alternatively, they may use a subclass of this one and
   * override {@link #hintsEqual(Object,Object)} if there is a measure of
   * similarity for specific type of hints for the purposes of a
   * {@link org.jamesii.core.observe.comparing.EqualityObserver} that is not
   * captured by {@link Object#equals(Object)}.
   * 
   * @author Arne Bittig
   * @date 25.09.2012
   */
  protected static class TimeAndHintInfo {

    private final Comparable<?> time;

    private final boolean hintPresent;

    private final Object hint;

    protected TimeAndHintInfo(Comparable<?> time, boolean hintPresent) {
      this.time = time;
      this.hintPresent = hintPresent;
      this.hint = null;
    }

    /**
     * @param time
     * @param hint
     */
    public TimeAndHintInfo(Comparable<?> time, Object hint) {
      this.time = time;
      this.hintPresent = true;
      this.hint = hint;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (hint == null ? 0 : 1);
      // hint's equals may not be used in this class' equals
      result = prime * result + (hintPresent ? 1231 : 1237); // NOSONAR:
      // auto gen constants for hashing involving boolean value
      result = prime * result + (time == null ? 0 : time.hashCode());
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (!(obj instanceof TimeAndHintInfo)) {
        return false;
      }
      TimeAndHintInfo other = (TimeAndHintInfo) obj;
      if (((Comparable<Object>) this.time).compareTo(other.time) != 0) {
        return false;
      }
      if (this.hintPresent != other.hintPresent) {
        return false;
      }
      if (this.hint == null) {
        return other.hint == null;
      }
      return hintsEqual(this.hint, other.hint);
    }

    /**
     * Method to check whether hint in this {@link TimeAndHintInfo} equals that
     * of another. This method is called by {@link #equals(Object)}, but only if
     * the this' hint (i.e. the first argument) is not null.
     * 
     * @param thisHint
     *          internally stored hint (not null)
     * @param otherHint
     *          hint to compare to internally stored one (may be null)
     * @return true if hints are equal enough for the outer class' purpose
     */
    @SuppressWarnings("static-method")
    protected boolean hintsEqual(Object thisHint, Object otherHint) {
      return thisHint.equals(otherHint);
    }

    @Override
    public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append(this.getClass().getSimpleName());
      builder.append('@');
      builder.append(time);
      if (hintPresent) {
        builder.append(": ");
        builder.append(hint);
      } else {
        builder.append(" without hint");
      }
      return builder.toString();
    }
  }
}
