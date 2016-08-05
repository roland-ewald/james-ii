/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.values;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;

import model.mlspace.entities.values.AbstractValueRange.ValueRangeOrInterval;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

class ValueInterval extends ValueRangeOrInterval<Double> {

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(ValueInterval.class,
        new IConstructorParameterProvider<ValueInterval>() {
          @Override
          public Object[] getParameters(ValueInterval vi) {
            return new Object[] { vi.getLower(), vi.getUpper() };
          }
        });
  }

  private static final long serialVersionUID = 6434617245236904935L;

  private final boolean includeLower, includeUpper;

  ValueInterval(Double lower, Double upper, boolean includeLower,
      boolean includeUpper) {
    super(lower, upper);
    this.includeLower = includeLower;
    this.includeUpper = includeUpper;
  }

  @Override
  public Double getRandomValue(IRandom rand) {
    if (isEmpty()) {
      throw new IllegalStateException("Value collection is empty");
    }
    return getLower() + (getUpper() - getLower()) * rand.nextDouble();
  }

  @Override
  public double size() {
    if (isEmpty()) {
      return 0.;
    }
    if (getLower() == getUpper()) {
      return 1.;
    }
    return Double.POSITIVE_INFINITY;
  }

  @Override
  public boolean contains(Object o) {
    double val;
    try {
      val = ((Number) o).doubleValue();
    } catch (ClassCastException e) {
      return false;
    }
    boolean lowerEndOK = includeLower ? getLower() <= val : getLower() < val;
    if (!lowerEndOK) {
      return false;
    }
    /* boolean upperEndOK = */
    return includeUpper ? val <= getUpper() : val < getUpper();
  }

  @Override
  public boolean containsAll(AbstractValueRange<?> c) {

    if (c instanceof ValueRangeOrInterval<?>) {
      ValueRangeOrInterval<?> vroi = (ValueRangeOrInterval<?>) c;
      return this.getLower() <= vroi.getLower()
          && vroi.getUpper() <= this.getUpper();
    }
    return super.containsAll(c);
  }

  @SuppressWarnings("unchecked")
  @Override
  public Iterator<Double> iterator() {
    if (isEmpty()) {
      return (Iterator<Double>) EMPTY_ITERATOR;
      // Collections.emptyIterator() Java >= 1.7
    }
    if (getLower() == getUpper()) {
      return Collections.singleton(getLower()).iterator();
    }
    throw new IllegalStateException("Interval has infinite number"
        + " of elements. Cannot iterate over them.");
  }

  @Override
  public String toString() {
    if (getLower() == Double.NEGATIVE_INFINITY) {
      if (getUpper() == -Double.MIN_NORMAL) {
        return "<0";
      } else {
        return "<" + (includeUpper ? "=" : "") + getUpper();
      }
    }
    if (getUpper() == Double.POSITIVE_INFINITY) {
      if (getLower() == Double.MIN_NORMAL) {
        return ">0";
      } else {
        return ">" + (includeLower ? "=" : "") + getLower();
      }
    }
    return (includeLower ? "[" : "(") + getLower() + ".." + getUpper()
        + (includeUpper ? "]" : ")");
  }

  private static final Iterator<?> EMPTY_ITERATOR = new EmptyIterator<>();

  static final class EmptyIterator<T> implements Iterator<T> {
    @Override
    public boolean hasNext() {
      return false;
    }

    @Override
    public T next() {
      throw new NoSuchElementException();
    }

    @Override
    public void remove() {
      throw new NoSuchElementException();
    }
  }
}