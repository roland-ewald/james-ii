/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package simulator.devs.abstractsequential;


import java.util.ArrayList;
import java.util.List;

import model.devs.IAtomicModel;
import model.devs.ICoupledModel;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.model.AbstractState;
import org.jamesii.core.model.IModel;
import org.jamesii.core.model.InvalidModelException;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;
import org.jamesii.core.processor.ProcessorInformation;
import org.jamesii.core.processor.plugintype.JamesProcessorFactory;
import org.jamesii.core.simulationrun.ISimulationRun;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;

/**
 * A factory for creating AbstractSequentialProcessor objects.
 * 
 * @author Jan Himmelspach
 */
public class AbstractSequentialProcessorFactory extends JamesProcessorFactory {

  static final long serialVersionUID = -8064732048418332889L;

  @SuppressWarnings("unchecked")
  @Override
  public IProcessor create(IModel model, IComputationTask computationTask,
      Partition partition, ParameterBlock parameters) {

    ISimulationRun simulation = (ISimulationRun) computationTask;

    RootCoordinator p = new RootCoordinator(model);

    IAbstractSequentialProcessor aproc;

    // AbstractEventQueueFactory.getEventQueueFactory((IBasicDEVSModel)model,
    // simulation.getParameters()

    ParameterBlock aeqp = parameters.getSubBlock("eventqueue");

    if (model instanceof ICoupledModel) {
      aproc =
          new Coordinator((ICoupledModel) model, SimSystem.getRegistry()
              .getFactory(AbstractEventQueueFactory.class, aeqp));
    } else {
      if (model instanceof IAtomicModel) {
        aproc = new Simulator((IAtomicModel<? extends AbstractState>) model);
      } else {
        throw new InvalidModelException(
            "Cannot create an abstract sequential simulator for non-valid DEVS models!");
      }
    }
    aproc.init(simulation.getStartTime());

    p.init(aproc, simulation.getStartTime());

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
    return 0.6;
  }

  /**
   * Return the list of supported modelling formalism interfaces (e.g. of
   * supported IModel extending interfaces)
   * 
   * @return
   */
  @Override
  public List<Class<?>> getSupportedInterfaces() {
    List<Class<?>> supportedInterfaces = new ArrayList<>();
    supportedInterfaces.add(model.devs.ICoupledModel.class);
    supportedInterfaces.add(model.devs.IAtomicModel.class);
    return supportedInterfaces;
  }

  /**
   * Return true if this factory can deal with a distributed simulation setup
   * 
   * @return
   */
  @Override
  public boolean supportsSubPartitions() {
    return false;
  }

}
