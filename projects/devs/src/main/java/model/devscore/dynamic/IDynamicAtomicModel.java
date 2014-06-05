/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package model.devscore.dynamic;


import java.util.List;

import org.jamesii.core.model.IDynamicModel;

/**
 * The Interface IDynamicAtomicModel.
 * 
 * @author Jan Himmelspach
 */
public interface IDynamicAtomicModel extends IDynamicModel {

  /**
   * Returns a vector of all changerequsts.
   * 
   * @return vector of change requests
   */
  List<ChangeRequest<?>> getChangeRequests();

  /**
   * Returns true if there are any change requests.
   * 
   * @return true if any changerequests
   */
  boolean hasChangeRequest();

}
