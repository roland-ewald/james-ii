/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.services.management;

import java.util.List;

/**
 * A more general interface which can be reused whenever a list of something
 * which can be "registered" has to be maintained.
 * 
 * @param <I>
 *          Specifies the type which has to be managed
 * 
 * @author Jan Himmelspach
 */
public interface IManagement<I> {

  /**
   * Add an element to the internal management list.
   * 
   * @param element
   *          the element
   */
  void register(I element);

  /**
   * Remove an element from the internal management list.
   * 
   * @param element
   *          the element
   */
  void unregister(I element);

  /**
   * Check whether the given element is managed by this management class.
   * 
   * @param element
   *          return true if the element is managed by this class.
   */
  void contains(I element);

  /**
   * Get the managed entry with the given index.
   * 
   * @param index
   *          the index
   * 
   * @return the I
   */
  I get(int index);

  /**
   * Get one out of the managed ones.
   * 
   * @return the I
   */
  I get();

  /**
   * Get up to max managed entries.
   * 
   * @param max
   *          the max
   * 
   * @return the some
   */
  List<I> getSome(int max);

  /**
   * Retrieve a list of ALL entries.
   * 
   * @return the list
   */
  List<I> getList();

  /**
   * Return the number of entries.
   * 
   * @return the int
   */
  int size();

}
