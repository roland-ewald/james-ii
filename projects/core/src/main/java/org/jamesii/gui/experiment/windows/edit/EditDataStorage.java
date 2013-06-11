/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import org.jamesii.SimSystem;
import org.jamesii.core.data.storage.plugintype.AbstractDataStorageFactory;
import org.jamesii.core.data.storage.plugintype.DataStorageFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.gui.utils.factories.ConfigureFactoryPanel;

/**
 * GUI to select a data storage for the current experiment.
 * 
 * @author Roland Ewald
 */
public class EditDataStorage extends EditExperimentPanel {

  /** Serialization ID. */
  private static final long serialVersionUID = -1993656809018598187L;

  /** Panel to configure data storage factories. */
  private ConfigureFactoryPanel<DataStorageFactory> confDSPanel;

  /**
   * Default constructor.
   * 
   * @param exp
   *          reference to experiment
   */
  public EditDataStorage(BaseExperiment exp) {
    super(exp);
    confDSPanel =
        new ConfigureFactoryPanel<>(SimSystem.getRegistry()
            .getFactoryOrEmptyList(AbstractDataStorageFactory.class, null),
            "Select data sink to be used:", this.getExperiment()
                .getDataStorageFactory(), true);

    add(confDSPanel);
  }

  @Override
  public void closeDialog() {
    getExperiment().setDataStorageFactory(
        confDSPanel.getSelectedFactoryAndParameter());
  }

  @Override
  public String getName() {
    return "Data Storage";
  }

}
