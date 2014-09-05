/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.distributed.partition;

import java.io.Serializable;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.simulationserver.ISimulationServer;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.Model;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.simulationrun.SimulationRun;

// TODO: Auto-generated Javadoc
/**
 * A partition is a part of model which shall be run on a particular set of
 * resources. I.e. it contains a part of a model which shall be executed on the
 * given host as well as it may contain further sub partitions which have to be
 * executed on differents hosts.
 * 
 * @author Jan Himmelspach
 */
public class Partition implements Serializable {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = 6838237466909573514L;

  /** The simulation host the simulation shall be created on. */
  private ISimulationServer host;

  /** The model which shall be simulated within this partition. */
  private IModel model;

  /**
   * A reference to the parent processor. The parent processor is the processor
   * of the parent of the model. This value will be set by a ProcessorFactory.
   * Otherwise it is null -> no parent available
   */
  private ProcessorInformation parentProcessor = new ProcessorInformation();

  /**
   * A reference to the processor of the given model. This reference is set by a
   * ProcessorFactory during the simulation creation process.
   */
  private ProcessorInformation processor = new ProcessorInformation();

  /**
   * The processor factory which shall be used for creating the processor
   * structure for simulating the model.
   */
  private ProcessorFactory processorFactory;

  /**
   * The sub partitions. A sub partition represents a part of the model to be
   * simulated which shall not simulated on the same host as the rest of this
   * partition
   */
  private Partitions subPartitions;

  /**
   * Instantiates a new partition.
   */
  public Partition() {
    super();
  }

  /**
   * Creates a new instance of partition, the processor factory will not be
   * set!.
   * 
   * @param model
   *          the model
   * @param host
   *          the host
   * @param subPartitions
   *          the sub partitions
   */
  public Partition(IModel model, ISimulationServer host,
      Partitions subPartitions) {
    super();
    this.model = model;
    this.host = host;
    this.subPartitions = subPartitions;
    this.processorFactory = null;
  }

  /**
   * Creates a new instance of Partition.
   * 
   * @param model
   *          the model
   * @param host
   *          the host
   * @param subPartitions
   *          the sub partitions
   * @param processorFactory
   *          the processor factory
   */
  public Partition(IModel model, ISimulationServer host,
      Partitions subPartitions, ProcessorFactory processorFactory) {
    super();
    this.model = model;
    this.host = host;
    this.subPartitions = subPartitions;
    this.processorFactory = processorFactory;
  }

  /**
   * Adds a sub partition to the list of available partitions.
   * 
   * @param partition
   *          the partition
   */
  public void addSubPartition(Partition partition) {
    subPartitions.addPartition(partition);
  }

  /**
   * This methods checks whether there is a partition for the given model.
   * 
   * @param forModel
   *          the for model
   * 
   * @return true, if contains partition for model
   */
  public boolean containsPartitionForModel(Model forModel) {
    if (subPartitions == null) {
      return false;
    }
    return subPartitions.containsPartitionForModel(forModel);
  }

  /**
   * Creates a processor for the given model and returns a reference to it.
   * 
   * @param simulation
   *          the simulation
   * @param parameter
   *          The parameter block for the processor.
   * 
   * @return the simulation run
   */
  public SimulationRun createProcessor(SimulationRun simulation,
      ParameterBlock parameter) {

    IProcessor proc;

    proc = processorFactory.create(getModel(), simulation, this, parameter, SimSystem.getRegistry().createContext());
    proc.setComputationTask(simulation);
    simulation.setProcessorInfo(new ProcessorInformation(proc));

    try {
      // System.out.println ("after creating the processor
      // "+this.getRemoteModel().getName());
    } catch (Exception e) {
    }
    return simulation;
  }

  /**
   * Gets the host.
   * 
   * @return the host
   */
  public ISimulationServer getHost() {
    return host;
  }

  /**
   * Gets the model.
   * 
   * @return the model
   */
  public IModel getModel() {
    return model;
  }

  /**
   * Gets the parent processor info.
   * 
   * @return the parent processor info
   */
  public ProcessorInformation getParentProcessorInfo() {
    return parentProcessor;
  }

  /**
   * Gets the partition for model.
   * 
   * @param givenModel
   *          the model for which the partition shall be retrieved
   * 
   * @return the partition for model
   */
  public Partition getPartitionForModel(IModel givenModel) {
    if (givenModel.equals(model)) {
      return this;
    }
    if (subPartitions != null) {
      return subPartitions.getPartitionForModel(givenModel);
    }
    return null;
  }

  /**
   * Return the currently set processor factory for this partition. Most likely
   * this is the factory which will be used by succ algs for creating the
   * simulator for this partition. The return value may be null!
   * 
   * @return the processor to be used for creasting the simulator or null
   */
  public ProcessorFactory getProcessorFactory() {
    return processorFactory;
  }

  /**
   * Returns the processor of the model which is computed by this partition.
   * 
   * @return the processor info
   */
  public ProcessorInformation getProcessorInfo() {
    return processor;
  }

  /**
   * Returns a sub partition.
   * 
   * @param index
   *          the index
   * 
   * @return the sub partition
   */
  public Partition getSubPartition(int index) {
    return subPartitions.getPartition(index);
  }

  /**
   * Returns the number of sub partitions.
   * 
   * @return the sub partition count
   */
  public int getSubPartitionCount() {
    if (subPartitions == null) {
      return 0;
    }
    return subPartitions.getPartitionCount();
  }

  /**
   * Return true if there is a sub partition.
   * 
   * @return true, if checks for sub partitions
   */
  public boolean hasSubPartitions() {
    return (!(subPartitions == null)) || (getSubPartitionCount() > 0);
  }

  /**
   * Inits the.
   * 
   * @param theModel
   *          the the model
   * @param theHost
   *          the the host
   * @param theSubPartitions
   *          the the sub partitions
   */
  public void init(IModel theModel, ISimulationServer theHost,
      Partitions theSubPartitions) {
    model = theModel;
    host = theHost;
    subPartitions = theSubPartitions;
  }

  /**
   * Inits the.
   * 
   * @param theModel
   *          the the model
   * @param theHost
   *          the the host
   * @param theSubPartitions
   *          the the sub partitions
   * @param theProcessorFactory
   *          the the processor factory
   */
  public void init(IModel theModel, ISimulationServer theHost,
      Partitions theSubPartitions, ProcessorFactory theProcessorFactory) {
    model = theModel;
    host = theHost;
    subPartitions = theSubPartitions;
    processorFactory = theProcessorFactory;
  }

  /**
   * Adds a sub partition to the list of available partitions.
   * 
   * @param index
   *          the index
   * 
   * @return true, if removes the sub partition
   */
  public boolean removeSubPartition(int index) {
    return subPartitions.removePartition(index);
  }

  /**
   * Sets the null model.
   */
  public void setNullModel() {
    model = null;
  }

  /**
   * Set the parent processor of the processor to be created for the given
   * model.
   * 
   * @param parentProcessor
   *          the parent processor
   */
  public void setParentProcessor(IProcessor parentProcessor) {
    this.parentProcessor.setLocal(parentProcessor);
  }

  /**
   * Sets the parent processor info.
   * 
   * @param parentProcessor
   *          the parent processor
   */
  public void setParentProcessorInfo(ProcessorInformation parentProcessor) {
    this.parentProcessor = parentProcessor;
  }

  /**
   * Set a processor factory to be used for creating the processor for this
   * partition.
   * 
   * @param pf
   *          the pf
   */
  public void setProcessorFactory(ProcessorFactory pf) {
    processorFactory = pf;
  }

  /**
   * Set a processor factory for this and all contained partitions.
   * 
   * @param pf
   *          the pf
   */
  public void setProcessorFactoryRecursively(ProcessorFactory pf) {
    setProcessorFactory(pf);
    if (hasSubPartitions()) {
      for (Partition p : subPartitions.getPartitions()) {
        p.setProcessorFactoryRecursively(pf);
      }
    }
  }

  /**
   * Sets the processor (usually after creation :-) ) of the given model.
   * 
   * @param processor
   *          the processor
   */
  public void setProcessorInfo(ProcessorInformation processor) {
    this.processor = processor;
  }

  @Override
  public String toString() {
    String result = super.toString();
    result +=
        " - Model: " + getModel() + " - SubParts: " + getSubPartitionCount()
            + " - Host: " + getHost();
    return result;
  }

}
