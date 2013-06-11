/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulation.distributed.processor;

import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.simulation.distributed.RemoteFactory;

/**
 * The RemoteProcessorFactory supports the creation of an unknown remote
 * communication scheme for processor classes. It extends the RemoteFactory and
 * sets its L and I parameters to IProcessor and ProcessorInformation
 * respectively. The R parameter is still undefined - a factory for a concrete
 * remote communication scheme has to set this parameter to the interface it is
 * going to use.
 * 
 * @author Jan Himmelspach
 * 
 * @param <R>
 *          the remote interface parameter
 */
public abstract class RemoteProcessorFactory<R> extends
    RemoteFactory<R, IProcessor, ProcessorInformation> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -7050724961887928489L;

  @Override
  public abstract ProcessorInformation createRemoteInformation(
      IProcessor localRef, ParameterBlock params);

  @Override
  public abstract ProcessorInformation getInformation(IProcessor processor,
      R processorRef);

  @Override
  public abstract IProcessor getProxy(R processorRef);

  @Override
  public abstract IProcessor getProxyFromInfo(ProcessorInformation information);

  @Override
  public abstract R getRemote(IProcessor processor);

}
