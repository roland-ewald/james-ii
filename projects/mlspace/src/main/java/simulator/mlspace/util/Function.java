/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package simulator.mlspace.util;

/**
 * Interface mimicking functionality of guava's Function. Replace after
 * migration.
 *
 * @author Arne Bittig
 * @date 18.01.2013
 * @param <I>
 *          Input type
 * @param <O>
 *          Output type
 */
public interface Function<I, O> {

  /**
   * @param input
   *          Input object
   * @return Result of this function applied to input
   */
  O apply(I input);

  /**
   * Interface for functions returning a string
   * 
   * @author Arne Bittig
   * @date 23.01.2013
   * @param <I>
   *          Input type
   */
  interface ToString<I> extends Function<I, String> {
    /* marker / type-parameter-narrowing interface */
  }

}
