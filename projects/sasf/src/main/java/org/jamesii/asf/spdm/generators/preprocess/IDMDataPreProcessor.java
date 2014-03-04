/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.generators.preprocess;

import java.util.List;

import org.jamesii.asf.spdm.dataimport.PerformanceTuple;


/**
 * Interface for preprocessors on data-mining data.
 * 
 * @param <T>
 *          the type of the performance tuple to be pre-processed
 * @author Roland Ewald
 */
public interface IDMDataPreProcessor<T extends PerformanceTuple> {

  /**
   * Preprocesses input data for data mining.
   * 
   * @param input
   *          list of input tuples
   * @return list of output tuples (not necessarily of the same size, the
   *         preprocessor may filter)
   */
  List<T> preprocess(List<T> input);

}
