/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.simulationrun.stoppolicy;

import java.io.Serializable;
import java.util.List;

import org.jamesii.core.base.INamedEntity;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.SimulationRunConfiguration;
import org.jamesii.core.experiments.tasks.ComputationTaskIDObject;
import org.jamesii.core.experiments.tasks.stoppolicy.IComputationTaskStopPolicy;
import org.jamesii.core.factories.Context;
import org.jamesii.core.factories.IContext;
import org.jamesii.core.model.IModel;
import org.jamesii.core.observe.IMediator;
import org.jamesii.core.observe.IObserver;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.simulationrun.ISimulationRun;

/**
 * A dummy simulation run. For testing (dependency injection).
 * 
 * @author Roland Ewald
 */
public class DummySimRun implements ISimulationRun, Serializable {

  private static final long serialVersionUID = 7814518458480633254L;

  @Override
  public void freeRessources() {
  }

  @Override
  public SimulationRunConfiguration getConfig() {
    return null;
  }

  @Override
  public IModel getModel() {
    return null;
  }

  @Override
  public Partition getPartition() {
    return null;
  }

  @Override
  public ProcessorInformation getProcessorInfo() {
    return null;
  }

  @Override
  public <D> D getProperty(String property) {
    return null;
  }

  @Override
  public Double getStartTime() {
    return null;
  }

  @Override
  public IComputationTaskStopPolicy getStopPolicy() {
    return null;
  }

  @Override
  public Double getTime() {
    return null;
  }

  @Override
  public long getSimpleId() {
    return 0;
  }

  @Override
  public ComputationTaskIDObject getUniqueIdentifier() {
    return null;
  }

  @Override
  public Long getWCStartTime() {
    return null;
  }

  @Override
  public boolean isPausing() {
    return false;
  }

  @Override
  public boolean isProcessorRunnable() {
    return false;
  }

  @Override
  public void pauseProcessor() {
  }

  @Override
  public void start() {
  }

  @Override
  public void stopProcessor() {
  }

  @Override
  public int compareTo(INamedEntity o) {
    return 0;
  }

  @Override
  public String getName() {
    return "Dummy";
  }

  @Override
  public void setName(String name) {
  }

  @Override
  public String getCompleteInfoString() {
    return null;
  }

  @Override
  public void changed() {
  }

  @Override
  public IMediator getMediator() {
    return null;
  }

  @Override
  public void registerObserver(IObserver observer) {
  }

  @Override
  public void setMediator(IMediator mediator) {
  }

  @Override
  public void unregisterObserver(IObserver observer) {
  }

  @Override
  public void unregisterObservers() {
  }

  @Override
  public void setProcessorInfo(ProcessorInformation processor) {
  }

  @Override
  public void changed(Object hint) {
  }

  @Override
  public void setContext(IContext context) {
  }

  @Override
  public IContext getContext() {
    return null;
  }

  @Override
  public void registerContext(IContext context) {
    // TODO Auto-generated method stub

  }

  @Override
  public List<IContext> getChildContexts() {
    // TODO Auto-generated method stub
    return null;
  }
  
  @Override
  public <O> O create(String pluginType, ParameterBlock block) {    
    return Context.createInstance (pluginType, block, this);
  }
}
