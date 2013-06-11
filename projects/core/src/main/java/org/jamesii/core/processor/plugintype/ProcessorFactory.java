/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.processor.plugintype;

import java.util.List;

import org.jamesii.core.distributed.partition.Partition;
import org.jamesii.core.experiments.tasks.IComputationTask;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.IProcessor;

/**
 * The Class ProcessorFactory.
 * 
 * A processor factory creates an instance of a computation algorithm (short
 * processor) for computation tasks. If the create method is called it is very
 * likely that the processor can be used for the model passed.
 * 
 * @author Jan Himmelspach
 */
public abstract class ProcessorFactory extends Factory<IProcessor> {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6967650562039766056L;

  /**
   * Return an empty string (i.e. the abstract class ProcessorFactory) cannot be
   * parameterised.
   * 
   * @return empty string
   */
  public static String getParameterStrings() {
    return "";
  }

  /**
   * Creates a new instance of ProcessorFactory.
   */
  public ProcessorFactory() {
  }

  /**
   * Create a processor to compute a solution of the model passed as parameter
   * in the computation task passed.
   * 
   * @param model
   *          The model for which the processor has to be created
   * @param computationTask
   *          the computation task the processor belongs to
   * @param partition
   *          If the model shall be executed in a distributed manner this
   *          parameter contains the mapping
   * @param params
   *          Additional parameters (e.g. event queue), tuples of parameter
   *          ident and value object
   * 
   * @return the instance of the processor created
   */
  public abstract IProcessor create(IModel model,
      IComputationTask computationTask, Partition partition,
      ParameterBlock params);

  @Override
  public IProcessor create(ParameterBlock parameters) {
    return create((IModel) parameters.getSubBlockValue("MODEL"),
        (IComputationTask) parameters.getSubBlockValue("TASK"),
        (Partition) parameters.getSubBlockValue("PARTITION"), parameters);
  }

  /**
   * Return a value between 0 and 1 which represents the simulators efficiency.
   * A factory which returns a highly efficient simulator will be preferably
   * used.
   * 
   * @return 0 for a not efficient and 1 for a highly efficient one
   */
  public abstract double getEfficencyIndex();

  /**
   * Return the list of supported {@link IModel} extending interfaces
   * 
   * @return the supported interfaces (they have to be in fact interfaces,
   *         actual classes won't do)
   */
  public abstract List<Class<?>> getSupportedInterfaces();

  /**
   * Return true if this factory can deal with a distributed simulation setup.
   * 
   * @return true, if supports sub partitions
   */
  public abstract boolean supportsSubPartitions();

  /**
   * Returns true if the factory can deal with the provided model. For instance
   * it might be the case that even though the model interface is supported, the
   * actual model instance is not due to some parameter combination, size or
   * whatsoever. This method is supposed to be called after models are already
   * sorted out that don't fit the supported interfaces.
   * 
   * @param model
   *          the model to check
   * @return true, if model can be handled by this factory
   */
  public boolean supportsModel(IModel model) {
    return true;
  }

  @Override
  public String toString() {
    StringBuilder s = new StringBuilder(super.toString());
    s.append("\n Supported interfaces");
    if (getSupportedInterfaces() != null) {
      for (Class<?> c : getSupportedInterfaces()) {
        s.append("\n  - ");
        s.append(c.getName());
      }
    } else {
      s.append("\n  No supported interfaces");
    }

    if (supportsSubPartitions()) {
      s.append("\n Supports sub partitions");
    } else {
      s.append("\n Does not support sub partitions");
    }
    s.append("\n Ordering efficiency index: " + getEfficencyIndex());
    return s.toString();
  }

}
