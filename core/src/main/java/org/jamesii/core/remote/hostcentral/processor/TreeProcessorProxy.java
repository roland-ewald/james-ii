/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.remote.hostcentral.processor;

import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ITreeProcessor;
import org.jamesii.core.remote.hostcentral.IObjectId;
import org.jamesii.core.remote.hostcentral.IRemoteMethodCaller;

/**
 * The Class TreeProcessorProxy.
 * 
 * @author Simon Bartels
 */
public class TreeProcessorProxy extends ProcessorProxy<Double> implements
    ITreeProcessor<Double> {

  /**
   * Instantiates a new tree processor proxy.
   * 
   * @param remoteMethodCaller
   *          the remote method caller
   * @param objectId
   *          the object id
   */
  public TreeProcessorProxy(IRemoteMethodCaller remoteMethodCaller,
      IObjectId objectId) {
    super(remoteMethodCaller, objectId);
  }

  @Override
  public IProcessor getParent() {
    return (IProcessor) executeMethod("getParent");
  }

  @Override
  public void setParent(IProcessor parent) {
    executeMethod("setParent", new Object[] { parent });
  }

  @Override
  public void addChild(IProcessor proc) {
    executeMethod("addChild", new Object[] { proc });
  }

  @Override
  public void removeChild(IProcessor proc) {
    executeMethod("removeChild", new Object[] { proc });
  }
}
