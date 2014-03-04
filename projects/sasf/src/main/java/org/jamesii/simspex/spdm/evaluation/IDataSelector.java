/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.evaluation;

import java.util.List;

/**
 * Interface for data selectors. They select data tuples for training and test
 * data.
 * 
 * @author Roland Ewald
 * 
 */
public interface IDataSelector {

  /**
   * Selects data; for example training data or test data.
   * 
   * @param input
   *          the complete set of (pre-processed) tuples
   * @return list of selected data
   */
  <X> List<X> selectData(List<X> input);

}
