/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.model.variables;

import java.util.List;

/**
 * This small interface is for qualitative variables. If this interface is
 * implemented the list of allowed values can be requested.
 * 
 * Examples: - nominal: gender, confession - ordinal: passed schools, ...
 * 
 * @author Jan,ps013
 * 
 * @param <T>
 */
public interface IQualitativeVariable<T> extends IVariable<T> {

  /**
   * Returns the list of possible categories for the qualitative variable. Such
   * a variable may either be nominal scaled or ordinal scaled. In the latter
   * case the returned list must be correctly ordered such that the least
   * ordinal value is in the 0 position of the list.
   * 
   * @return A list of values usable with this variable
   */
  List<T> getCategories();

  /**
   * Returns the category value stored at positition num
   * 
   * @param num
   *          ordering index of scala values (valid for nominal values as well)
   * @return value stored at the position num
   */
  T getCategory(int num);

  /**
   * Returns the index of the given category in the list of allowed categories
   * 
   * @param value
   *          The value of which the index shall be retrieved
   * @return the index, -1 if not in the list
   */
  int getIndexOf(T value);

  /**
   * Can be used for checking whether this qualitative variable is ordinally or
   * nominally scaled
   * 
   * @return returns true if the variable is ordinally scaled
   */
  boolean isOrdinal();

  /**
   * Sets list of categories
   * 
   * @param categories
   */
  void setCategories(List<T> categories);

  /**
   * Defines whether this variable's values are ordinal or not
   * 
   * @param ordinal
   */
  void setOrdinal(boolean ordinal);

  /**
   * Sets a random value with uniform distributed probabilities over all
   * possible value.
   */
  void setRandomValue();

}
