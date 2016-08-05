/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

/**
 * Interface mimicking the functionality of guava's predicate. Replace after
 * migrating to guava.
 *
 * @author Arne Bittig
 * @date 18.01.2013
 * @param <T>
 */
public interface Predicate<T> {

  /**
   * @param obj
   *          Object to check
   * @return true if this predicate applies to obj
   */
  boolean apply(T obj);

  /**
   * Always true predicate
   * 
   * @author Arne Bittig
   * @date 18.01.2013
   * @param <T>
   */
  final class True<T> implements Predicate<T> {
    @Override
    public boolean apply(T obj) {
      return true;
    }
  }
}