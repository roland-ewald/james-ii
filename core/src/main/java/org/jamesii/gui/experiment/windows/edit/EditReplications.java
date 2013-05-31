/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;

import javax.swing.BoxLayout;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.replication.plugintype.AbstractRepCriterionFactory;
import org.jamesii.core.experiments.replication.plugintype.RepCriterionFactory;
import org.jamesii.core.parameters.ParameterizedFactory;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

/**
 * Panel to edit the replication criteria of the experiment.
 * 
 * @author Roland Ewald
 */
public class EditReplications extends EditExperimentPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = 8927085099770409877L;

  /** Panel to configure creation of additional criteria. */
  private ConfigureFactoryPanel<RepCriterionFactory> rpcFactoryPanel =
      new ConfigureFactoryPanel<>(SimSystem.getRegistry()
          .getFactoryOrEmptyList(AbstractRepCriterionFactory.class, null),
          "Setup Replication Criterion", null, null, false);

  /**
   * Instantiates a new edits the replications.
   * 
   * @param exp
   *          the exp
   */
  public EditReplications(BaseExperiment exp) {
    super(exp);
    setLayout(new BorderLayout());
    ParameterizedFactory<RepCriterionFactory> fac =
        getExperiment().getReplicationCriterionFactory();
    if (fac.getFactory() != null) {
      rpcFactoryPanel.setSelectedFactory(fac.getFactory().getClass().getName(),
          fac.getParameters());
    }
    this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
    this.add(rpcFactoryPanel, BorderLayout.CENTER);
  }

  @Override
  public void closeDialog() {
    getExperiment().setReplicationCriterionFactory(
        rpcFactoryPanel.getSelectedFactoryAndParameter());
  }

  @Override
  public String getName() {
    return "Replications";
  }

}
