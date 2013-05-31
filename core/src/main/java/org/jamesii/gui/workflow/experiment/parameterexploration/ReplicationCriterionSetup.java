/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment.parameterexploration;

import java.util.List;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.replication.plugintype.AbstractRepCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.parameters.ParameterizedFactory;

/**
 * @author Stefan Rybacki
 */
public class ReplicationCriterionSetup extends
    AbstractFactorySelectionPanel<RepCriterionFactory> {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 3667187760131846733L;

  public ReplicationCriterionSetup() {
    super("Select Replication Criterion: ", true);
    List<RepCriterionFactory> factories = getFactories();
    if (factories != null && factories.size() > 0) {
      setFactories(factories, factories.get(0), null);
    }
  }

  protected List<RepCriterionFactory> getFactories() {
    ParameterBlock block = new ParameterBlock();
    List<RepCriterionFactory> factoryList = null;
    try {
      factoryList =
          SimSystem.getRegistry().getFactoryList(
              AbstractRepCriterionFactory.class, block);
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return factoryList;
  }

  /**
   * Gets the replication criterion factory.
   * 
   * @return the replication criterion factory
   */
  public ParameterizedFactory<RepCriterionFactory> getReplicationCriterionFactory() {
    return new ParameterizedFactory<>(getSelectedFactory(),
        getSelectedParameters());
  }

  /**
   * @param repCriterionFactory
   */
  public void setReplicationCriteria(
      ParameterizedFactory<RepCriterionFactory> repCriterionFactory) {
    List<RepCriterionFactory> factories = getFactories();
    setFactories(factories, repCriterionFactory.getFactory(),
        repCriterionFactory.getParameters());
  }

}
