/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.experiments.tasks.setup.plugintype;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.factories.AbstractFactory;
import org.jamesii.core.factories.FactoryCriterion;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.ProcessorFactory;

/**
 * Abstract factory for task setup factories. A task setup factory creates the
 * object instance which initializes an object to hold the problem instance
 * (model) plus the computation algorithm used (e.g. a simulation run /
 * replication computation algorithm). <br/>
 * The framework provides support for internal simulation runs (this means model
 * and computation algorithm are realized as native framework parts). The
 * corresponding factory is
 * {@link org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunFactory}
 * . It creates instances of type
 * {@link org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunSetup}
 * . Instances of this object are then used to create an
 * {@link org.jamesii.core.experiments.tasks.IInitializedComputationTask} via
 * the method
 * {@link org.jamesii.core.experiments.tasks.setup.internalsimrun.SimulationRunSetup#initComputationTask}
 * <br/>
 * This allows to integrate external software to compute models by using the
 * framework for steering the experiment. <br/>
 * This abstract factory takes care of selecting the task setup factory (and
 * thus of selecting the type of the initialized computation task to be used
 * later on).
 * 
 * @author Jan Himmelspach
 */
public class AbstractTaskSetupFactory extends AbstractFactory<TaskSetupFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -6570194600840622247L;

  private static class ProcessorCriteria extends
      FactoryCriterion<TaskSetupFactory> {

    @SuppressWarnings("unchecked")
    @Override
    public List<TaskSetupFactory> filter(List<TaskSetupFactory> factories,
        ParameterBlock parameter) {
      Iterator<TaskSetupFactory> it = factories.iterator();
      List<Class<ProcessorFactory>> processors = new ArrayList<>();

      // FIXME run through all processors in parameter block and add all of them
      processors.add((Class<ProcessorFactory>) SimSystem.getRegistry()
          .getFactoryClass(
              (String) parameter.getSubBlockValue(ProcessorFactory.class
                  .getName())));

      while (it.hasNext()) {
        TaskSetupFactory factory = it.next();

        for (Class<ProcessorFactory> f : processors) {
          if (!factory.supportsProcessor(f)) {
            it.remove();
          }
        }
      }

      return factories;
    }

  }

  /**
   * Instantiates a new abstract task setup factory.
   */
  public AbstractTaskSetupFactory() {
    super();
    addCriteria(new ProcessorCriteria());
  }

}
