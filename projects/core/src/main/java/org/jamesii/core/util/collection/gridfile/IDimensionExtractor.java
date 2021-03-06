/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.gridfile;

import java.util.List;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;

/**
 * To use objects with the grid file they must implement this interface.
 * 
 * @author Tobias Helms
 * 
 */
public interface IDimensionExtractor<T> {

  /**
   * Extract the relevant data from an object. It's necessary to find the
   * position of an element in the grid file.
   * 
   * @param dimension
   * @return value of the dimension
   */
  double getData(T element, int dimension);

  /**
   * Return the array of all data from all dimensions.
   */
  double[] getAllData(T element);

  /**
   * Return the number of dimensions this dimension extractor supports for the
   * given object.
   */
  int getNumberOfDimensions(T element);

  /**
   * Get as much dimension information as possible from the given parameter
   * block. This method is useful if you want to prepare a partial search and
   * you do not have a concrete element with dimension information, but some
   * other data which can be used for some dimensions. If the extractor cannot
   * find an information for a specific dimension, this dimension is added to
   * the list of integers which is returned. These dimensions could be ignored
   * during the next partial match execution.
   */
  Pair<double[], List<Integer>> getData(ParameterBlock params);

}
