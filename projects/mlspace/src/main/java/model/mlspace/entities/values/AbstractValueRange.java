/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.values;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.jamesii.core.math.random.generators.IRandom;

/**
 * In the species and entity definition of ML-Space models, attribute values
 * need not be singleton values (int, double, String) but may be given as
 * intervals (e.g. [0.5..2.0]), ranges (e.g. 0:1:10), or sets (e.g. {1,2,3} or
 * {on, off}). This class generalizes these and provides the useful operations
 * on them. Singleton values should can be represented as one-element sets.
 * 
 * @author Arne Bittig
 * 
 * @param <T>
 *          Type of values
 */
public abstract class AbstractValueRange<T> implements Iterable<T>,
    java.io.Serializable {

  /**
   * Create value set from given values
   * 
   * @param vals
   *          Values
   * @return Value set
   */
  public static <T> AbstractValueRange<T> newSingleValue(T vals) {
    return new ValueSet<>(vals);
  }

  /**
   * Use given set directly as value set (warning: ValueRangeSetOrIntervals
   * shall be immutable; if set is of a mutable type, later changes to it will
   * be reflected here!)
   * 
   * @param set
   *          Set of values
   * @return Wrapped value set
   */
  public static <T> AbstractValueRange<T> newSet(Set<T> set) {
    return new ValueSet<>(set);
  }

  /**
   * Value range from lower bound in steps towards upper bound (Note that the
   * upper bound is not necessarily included, e.g. newRange(0.5, 0.9, 3.0) would
   * contain 0.5, 1.4, and 2.3 only.)
   * 
   * @param lower
   *          Lower bound
   * @param step
   *          Step size between values
   * @param upper
   *          Upper bound
   * @return Value range
   */
  public static <T extends Number & Comparable<T>> AbstractValueRange<T> newRange(
      T lower, T step, T upper) {
    return new ValueRange<>(lower, step, upper);
  }

  /**
   * Interval of real numbers that "contains" all doubles between the given
   * lower and upper bounds (i.e. returns true for contains(x) if
   * lower<=x<=upper; iteration is not possible, however)
   * 
   * @param lower
   *          Lower bound
   * @param upper
   *          Upper bound
   * @return Interval from lower to upper (inclusive)
   */
  public static ValueInterval newInterval(double lower, double upper) {
    return new ValueInterval(lower, upper, true, true);
  }

  /**
   * Interval of real numbers that "contains" all doubles between the given
   * lower and upper bounds (i.e. returns true for contains(x) if
   * lower<=x<=upper; iteration is not possible, however)
   * 
   * @param lower
   *          Lower bound
   * @param upper
   *          Upper bound
   * @param incLower
   *          inclusive lower bound?
   * @param incUpper
   *          inclusive upper bound?
   * @return Interval from lower to upper
   */
  public static ValueInterval newInterval(double lower, double upper,
      boolean incLower, boolean incUpper) {
    return new ValueInterval(lower, upper, incLower, incUpper);
  }

  /**
   * @param range
   *          Range whose values to check
   * @return true iff range is non-empty and all values are numeric
   */
  public static boolean isNumeric(AbstractValueRange<?> range) {
    if (range instanceof ValueRangeOrInterval<?>) {
      return true;// ((ValueRangeOrInterval<?>) range).getLower() >= 0.;
    }
    for (Object obj : range.toList()) {
      if (!(obj instanceof Number) /* || !(((Number) obj).doubleValue() >= 0.) */) {
        return false;
      }
    }
    return range.size() > 0;
  }

  private static final long serialVersionUID = 6621464430045096953L;

  /**
   * @param obj
   *          Object
   * @return true iff obj is in given range
   */
  public abstract boolean contains(Object obj);

  /**
   * @return Number of members (int value or {@link Double#POSITIVE_INFINITY})
   */
  public abstract double size();

  /**
   * Get a value from this range, set or interval (uniformly distributed) - if
   * rand is null and there is only a single value, that one is returned (else
   * an {@link IllegalArgumentException} is thrown)
   * 
   * @param rand
   *          Random number generator
   * @return some value from the range, set or interval
   */
  public abstract T getRandomValue(IRandom rand);

  /**
   * @return List of all values (if possible, {@link IllegalStateException}
   *         otherwise)
   */
  @SuppressWarnings("unchecked")
  public List<T> toList() {
    double size = this.size();
    if (Double.isInfinite(size)) {
      throw new IllegalStateException("infinite size");
    }
    ArrayList<T> list = new ArrayList<>((int) size);
    for (Object obj : this) {
      list.add((T) obj);
    }
    return list;
  }

  /**
   * 
   * @param r
   *          Another value range
   * @return True if r has finitely many values and all are also in this range
   */
  public boolean containsAll(AbstractValueRange<?> r) {
    for (Object obj : r) {
      if (!this.contains(obj)) {
        return false;
      }
    }
    return true;
  }

  abstract static class ValueRangeOrInterval<T extends Number & Comparable<T>>
      extends AbstractValueRange<T> {

    private static final long serialVersionUID = 5625974055491491243L;

    /** Threshold at which to conclude a number is an int */
    private static final double DELTA = 1e-12;

    protected static boolean isInt(Number n) {
      double fracPart = n.doubleValue() - n.intValue();
      return fracPart < DELTA;
    }

    private final double lower;

    private final double upper;

    protected ValueRangeOrInterval(double lower, double upper) {
      this.lower = lower;
      this.upper = upper;
    }

    double getLower() {
      return lower;
    }

    double getUpper() {
      return upper;
    }

    public boolean isEmpty() {
      return lower > upper;
    }

    public boolean containsAll(Collection<?> c) {
      for (Object o : c) {
        if (!this.contains(o)) {
          return false;
        }
      }
      return true;
    }

    @Override
    public int hashCode() {
      final int prime = 31;
      int result = 1;
      long temp;
      temp = Double.doubleToLongBits(lower);
      final int shift = 32;
      result = prime * result + (int) (temp ^ temp >>> shift);
      temp = Double.doubleToLongBits(upper);
      result = prime * result + (int) (temp ^ temp >>> shift);
      return result;
    }

    @Override
    public boolean equals(Object obj) {
      if (this == obj) {
        return true;
      }
      if (obj == null) {
        return false;
      }
      if (getClass() != obj.getClass()) {
        return false;
      }
      ValueRangeOrInterval<?> other = (ValueRangeOrInterval<?>) obj;
      if (Double.doubleToLongBits(lower) != Double
          .doubleToLongBits(other.lower)) {
        return false;
      }
      return Double.doubleToLongBits(upper) == Double
          .doubleToLongBits(other.upper);
    }

  }
}