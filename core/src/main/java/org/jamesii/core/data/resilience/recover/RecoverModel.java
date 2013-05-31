/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.resilience.recover;

import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.resilience.IDataResilience;
import org.jamesii.core.model.IModel;

/**
 * Implementation of the IRecoverModel interface, which provides some useful
 * functionalities.
 * 
 * @author Thomas Noesinger
 */
public abstract class RecoverModel implements IRecoverModel {

  /**
   * The method gets the necessary checkpoint information from the storage an
   * calls the recoverModel(data)-method of the IRecoverModel.
   * 
   * @param storage
   *          the storage of the data (where the model can be recovered from)
   * @param server
   *          the server's name the snapshot has been made for
   * @param dataid
   *          the ID of the data necessary to recover the model
   * 
   * @return the recovered model
   */
  @Override
  public IModel recoverModel(IDataResilience storage, String server, long dataid) {
    IModel result = null;

    if (storage == null) {
      SimSystem.report(Level.SEVERE,
          "No storage given. Cannot recover the element identified by "
              + dataid + " from no source.");
    } else {
      result = this.recoverModel(storage.getLastCheckpoint(server, dataid));
    }
    return result;
  }

}
