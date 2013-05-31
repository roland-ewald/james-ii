/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.collection.list;

import java.io.Serializable;
import java.util.List;

/**
 * The Interface ISelector.
 * 
 * This is a basic class for all selection mechanisms.
 * 
 * @author Christian Ober
 * @param <M>
 * 
 */
public interface ISelector<M> extends Serializable {

  /**
   * Returns a list of selected items. <b>Note: Due to performance-factors and
   * other reasons of implementation-issues it is needed to return a list of
   * models by executing the selection, but due to the formalism it is not
   * allowed to use the information included in the models of the list. It is
   * not allowed to change any of the information.</b>
   * 
   * @param elements
   *          the elements
   * 
   * @return the list< m>
   */
  List<M> executeSelection(List<M> elements);

}
