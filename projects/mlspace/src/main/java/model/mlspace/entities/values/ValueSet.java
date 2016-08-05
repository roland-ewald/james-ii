/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package model.mlspace.entities.values;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import org.jamesii.core.math.random.generators.IRandom;
import org.jamesii.core.serialization.IConstructorParameterProvider;
import org.jamesii.core.serialization.SerialisationUtils;

class ValueSet<T> extends AbstractValueRange<T> {

  /** persistence delegate for XML-Bean serialization */
  static {
    SerialisationUtils.addDelegateForConstructor(ValueSet.class,
        new IConstructorParameterProvider<ValueSet<?>>() {
          @Override
          public Object[] getParameters(ValueSet<?> vs) {
            return new Object[] { vs.internalSet };
          }
        });
  }

  private static final long serialVersionUID = -5543247979819554344L;

  private final Set<T> internalSet;

  ValueSet(T t) {
    internalSet = new LinkedHashSet<>(1);
    internalSet.add(t);
  }

  ValueSet(Set<T> set) {
    internalSet = set;
  }

  @Override
  public T getRandomValue(IRandom rand) {
    if (rand == null) {
      if (size() == 1) {
        return internalSet.iterator().next();
      }
      throw new IllegalArgumentException("Not a singleton value:"
          + "Random number generator must be given.");
    }
    int index = rand.nextInt(internalSet.size());
    Iterator<T> it = internalSet.iterator();
    for (int i = 0; i < index; i++) {
      it.next(); // TODO: slow! use array internally?!
    }
    return it.next();
  }

  @Override
  public double size() {
    return internalSet.size();
  }

  @Override
  public boolean contains(Object o) {
    return internalSet.contains(o);
  }

  @Override
  public Iterator<T> iterator() {
    return internalSet.iterator();
  }

  @Override
  public boolean containsAll(AbstractValueRange<?> c) {
    if (c instanceof ValueInterval && c.size() != 1) {
      // then iterating over values in c won't work
      return c.size() == 0;
    }
    return super.containsAll(c);
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result =
        prime * result + (internalSet == null ? 0 : internalSet.hashCode());
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
    ValueSet<?> other = (ValueSet<?>) obj;
    if (internalSet == null) {
      if (other.internalSet != null) {
        return false;
      }
    } else if (!internalSet.equals(other.internalSet)) {
      return false;
    }
    return true;
  }

  @Override
  public String toString() {
    StringBuilder str = new StringBuilder("[");
    for (T v : internalSet) {
      if (v instanceof double[]) {
        str.append(Arrays.toString((double[]) v));
      } else {
        str.append(v.toString());
      }
      str.append(",");
    }
    str.replace(str.length() - 1, str.length(), "]");
    return str.toString();
    // return internalSet.toString();
  }
}