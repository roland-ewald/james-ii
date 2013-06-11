/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.AbstractComputationTaskStopPolicyFactory;
import org.jamesii.core.experiments.tasks.stoppolicy.plugintype.ComputationTaskStopPolicyFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * @author Stefan Rybacki
 */
public class SimRunStopPolicySetup extends
    AbstractFactorySelectionPanel<ComputationTaskStopPolicyFactory> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -3881223295078025026L;

  public SimRunStopPolicySetup() {
    super("Select Stop Policy: ", false);
    List<ComputationTaskStopPolicyFactory> factories = getFactories();
    if (factories != null && factories.size() > 0) {
      setFactories(factories, factories.get(0), null);
    }
  }

  protected final List<ComputationTaskStopPolicyFactory> getFactories() {
    ParameterBlock block = new ParameterBlock();
    try {
      return SimSystem.getRegistry().getFactoryList(
          AbstractComputationTaskStopPolicyFactory.class, block);
    } catch (Exception e) {
      SimSystem.report(e);
    }
    return null;
  }

  public ComputationTaskStopPolicyFactory getSimRunStopPolicy() {
    return getSelectedFactory();
  }

  /**
   * @return
   */
  public ParameterBlock getSimRunStopPolicyParameters() {
    return getSelectedParameters();
  }

  /**
   * @param computationTaskStopFactory
   */
  public void setSimRunStopPolicy(
      ParameterizedFactory<ComputationTaskStopPolicyFactory> computationTaskStopFactory) {
    List<ComputationTaskStopPolicyFactory> factories = getFactories();
    setFactories(factories, computationTaskStopFactory.getFactory(),
        computationTaskStopFactory.getParameters());
  }

}
