/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.resilience.recover;

import java.util.List;

import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.model.IModel;

/**
 * General interface for the recovery of a model.
 * 
 * @author Thomas Noesinger
 */
public interface IRecoverModel {

  /**
   * This method recovers the model after a server failed.
   * 
   * @param <D>
   *          the type of the data
   * 
   * @param data
   *          the data to recover the model
   * 
   * @return the recovered model
   */
  <D> IModel recoverModel(List<D> data);

  /**
   * This method recovers the model after a server failed.
   * 
   * @param storage
   *          the storage containing the actual data to recover the model
   * @param server
   *          the server
   * @param dataid
   *          the the id of the data necessary to recover the model
   * 
   * @return the recovered model
   */
  IModel recoverModel(IDataResilience storage, String server, long dataid);

}
