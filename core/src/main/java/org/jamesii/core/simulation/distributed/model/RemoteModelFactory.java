/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed.model;

import org.jamesii.core.model.IModel;
import org.jamesii.core.model.ModelInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.simulation.distributed.RemoteFactory;

/**
 * The RemoteModelFactory supports the creation of an unknown remote
 * communication scheme for model classes. It extends the RemoteFactory and sets
 * its L and I parameters to IModel and ModelInformation respectively. The R
 * parameter is still undefined - a factory for a concrete remote communication
 * scheme has to set this parameter to the interface it is going to use for
 * this.
 * 
 * @author Jan Himmelspach
 * 
 * @param <R>
 *          the remote interface parameter
 */
public abstract class RemoteModelFactory<R> extends
    RemoteFactory<R, IModel, ModelInformation> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = 2330708945175124592L;

  @Override
  public abstract ModelInformation createRemoteInformation(IModel localRef,
      ParameterBlock params);

  @Override
  public abstract ModelInformation getInformation(IModel localRef, R remoteRef);

  @Override
  public abstract IModel getProxy(R remoteRef);

  @Override
  public abstract IModel getProxyFromInfo(ModelInformation information);

  @Override
  public abstract R getRemote(IModel localRef);

}
