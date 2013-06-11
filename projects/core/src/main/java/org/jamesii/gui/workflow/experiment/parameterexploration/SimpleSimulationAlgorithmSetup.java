/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.net.URI;
import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.model.IModel;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;

/**
 * @author Stefan Rybacki
 */
public class SimpleSimulationAlgorithmSetup extends
    AbstractFactorySelectionPanel<ProcessorFactory> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3030590907614853176L;

  private URI modelURI;

  private List<ProcessorFactory> factories;

  public SimpleSimulationAlgorithmSetup(IModel model) {
    super("Select preferred Simulation Algorithm: ", false);

    // determine the processor factories for the model
    ParameterBlock block =
        new ParameterBlock(new AbstractExecutablePartition(model, null, null),
            AbstractProcessorFactory.PARTITION);

    try {
      factories =
          SimSystem.getRegistry().getFactoryList(
              AbstractProcessorFactory.class, block);
      if (factories.size() > 0) {
        setFactories(factories, factories.get(0), null);
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  public void setAlgoFactory(ProcessorFactory factory, ParameterBlock b) {
    setFactories(factories, factory, b);
  }

}
