/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.values;

import java.util.Iterator;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;
import org.jamesii.core.util.misc.Generics;

class ValueRange<T extends Number & Comparable<T>> extends
    AbstractValueRange.ValueRangeOrInterval<T> {

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(ValueRange.class,
        new IConstructorParameterProvider<ValueRange<?>>() {
          @Override
          public Object[] getParameters(ValueRange<?> vr) {
            return new Object[] { vr.getLower(), vr.step, vr.getUpper() };
          }
        });
  }

  private static final long serialVersionUID = 5214279579084712263L;

  private final double step;

  ValueRange(T lower, T step, T upper) {
    super(lower.doubleValue(), upper.doubleValue());
    this.step = step.doubleValue();
  }

  T castToT(double n) {
    return Generics.autoCast(Double.valueOf(n)); // TODO: check
  }

  @Override
  public T getRandomValue(IRandom rand) {
    int s = (int) size();
    if (s == 0) {
      throw new IllegalStateException("Value collection is empty");
    }
    return castToT(getLower() + step * rand.nextInt(s));
  }

  @Override
  public double size() {
    int numOfPossibleSteps = (int) ((getUpper() - getLower()) / step) + 1;
    return numOfPossibleSteps > 0 ? (double) numOfPossibleSteps : 0.;
  }

  @Override
  public boolean contains(Object o) {
    if (!(o instanceof Number)) {
      return false;
    }
    double oVal = ((Number) o).doubleValue();
    return isInt((oVal - getLower()) / step)
        && (step > 0 ? oVal >= getLower() && oVal <= getUpper()
            : oVal <= getLower() && oVal >= getUpper());
  }

  @Override
  public boolean containsAll(AbstractValueRange<?> c) {
    if (c instanceof ValueInterval && c.size() != 1) {
      return c.size() == 0;
      // true case also covered in #containsAll(Coll)
    }
    return super.containsAll(c);
  }

  @Override
  public Iterator<T> iterator() {
    return new RangeIterator();
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder();
    str.append('{');
    for (Object obj : this) {
      str.append(obj.toString());
      str.append(',');
    }
    str.setCharAt(str.length() - 1, '}');
    return str.toString();
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = super.hashCode();
    long temp;
    temp = Double.doubleToLongBits(step);
    final int intSize = 32;
    result = prime * result + (int) (temp ^ temp >>> intSize);
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (!super.equals(obj)) {
      return false;
    }
    if (getClass() != obj.getClass()) {
      return false;
    }
    ValueRange<?> other = (ValueRange<?>) obj;
    if (Double.doubleToLongBits(step) != Double.doubleToLongBits(other.step)) {
      return false;
    }
    return true;
  }

  final class RangeIterator implements Iterator<T> {
    private double curVal = getLower();

    @Override
    public boolean hasNext() {
      return step > 0 ? curVal <= getUpper() : curVal >= getUpper();
    }

    @Override
    public T next() {
      curVal += step;
      return castToT(curVal - step);
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }
  }

}