/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.flatsequential;


import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import model.devs.IAtomicModel;
import model.devs.ICoupledModel;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;

import simulator.devs.flatsequential.eventforwarding.ExternalEventForwardingHandler;
import simulator.devs.flatsequential.eventforwarding.plugintype.AbstractExternalEventForwardingHandlerFactory;
import simulator.devs.flatsequential.eventforwarding.plugintype.ExternalEventForwardingHandlerFactory;

/**
 * A factory for creating FlatSequentialProcessor objects.
 */
public class FlatSequentialProcessorFactory extends JamesProcessorFactory {

  /** The Constant serialVersionUID. */
  static final long serialVersionUID = -8064732048418332889L;

  @Override
  public IProcessor create(IModel model, IComputationTask computationTask,
      Partition partition, ParameterBlock parameters) {

    ISimulationRun simulation = (ISimulationRun) computationTask;

    ParameterBlock ef = null;

    // new ParameterBlock(
    // simulation.getParameters()
    // .getEventforwarding());

    ExternalEventForwardingHandlerFactory f =
        SimSystem.getRegistry().getFactory(
            AbstractExternalEventForwardingHandlerFactory.class, ef);
    ExternalEventForwardingHandler eefh = f.create(null);

    SimSystem.report(Level.CONFIG, "Using " + eefh.getClass().getName()
        + " as external event forwarding mechanism.");

    ParameterBlock eqfp = parameters.getSubBlock("eventqueue");

    // (IEventQueue<IBasicDEVSModel>)
    FlatSequentialProcessor p =
        new FlatSequentialProcessor(model, SimSystem.getRegistry().getFactory(
            AbstractEventQueueFactory.class, eqfp), eefh);

    // ExternalEventForwardingHandlerFactory.getHandler(simulation
    // .getParameters(), ef));

    // IAbstractSequentialProcessor aproc;

    p.init(simulation.getStartTime());

    simulation.setProcessorInfo(new ProcessorInformation(p));

    return p;
  }

  /**
   * Return a value between 0 and 1 which represents the simulators efficiency.
   * A factory which returns a highly efficient simulator will be preferably
   * used.
   * 
   * @return 0 for a not efficient and 1 for a highly efficient one
   */
  @Override
  public double getEfficencyIndex() {
    return 0.1;
  }

  /**
   * Return the list of supported modelling formalism interfaces (e.g. of
   * supported IModel extending interfaces)
   * 
   * @return the supported interfaces
   */
  @Override
  public List<Class<?>> getSupportedInterfaces() {
    List<Class<?>> al = new ArrayList<>();
    al.add(ICoupledModel.class);
    al.add(IAtomicModel.class);
    return al;
  }

  /**
   * Return true if this factory can deal with a distributed simulation setup.
   * 
   * @return true, if supports sub partitions
   */
  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

}
