/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.processor;

import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorState;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;
import org.jamesii.core.remote.hostcentral.base.NamedEntityProxy;

/**
 * The Class ProcessorProxy.
 * 
 * Represents a remote processor, typically being located at a different
 * machine, but usually at least in a different JVM. This means that not all
 * methods of {@link org.jamesii.core.processor.IProcessor} are useful, and so
 * some might throw a
 * {@link org.jamesii.core.remote.RemoteCallForbiddenException}. Most likely
 * this will be the case if the return value does not represent the value it is
 * ought to be on a remote machine.
 * 
 * @author Jan Himmelspach
 */
public class ProcessorProxy<TimeBase extends Comparable<TimeBase>> extends
    NamedEntityProxy implements IProcessor<TimeBase> {

  /**
   * Instantiates a new processor proxy.
   * 
   * @param remoteMethodCaller
   *          the remote method caller
   * @param objectId
   *          the object id
   */
  public ProcessorProxy(IRemoteMethodCaller remoteMethodCaller,
      IObjectId objectId) {
    super(remoteMethodCaller, objectId);
  }

  @Override
  public void cleanUp() {
    executeMethod("cleanUp");
  }

  @Override
  public void executeNextStep() {
    executeMethod("executeNextStep");
  }

  @Override
  public String getClassName() {
    return (String) executeMethod("getClassName");
  }

  @SuppressWarnings("unchecked")
  @Override
  public <M extends IModel> M getModel() {
    return (M) throwDontCallRemotelyException("getModel");
  }

  @Override
  public ProcessorState getState() {
    return (ProcessorState) executeMethod("getState");
  }

  @SuppressWarnings("unchecked")
  @Override
  public TimeBase getTime() {
    return (TimeBase) executeMethod("getTime");
  }

  @Override
  public void setModel(IModel model) {
    throwDontCallRemotelyException("setModel");
  }

  @Override
  public void setComputationTask(IComputationTask simulation) {
    throwDontCallRemotelyException("setSimulationRun");
  }

}
