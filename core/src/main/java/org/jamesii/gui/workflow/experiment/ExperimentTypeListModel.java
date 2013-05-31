/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.workflow.experiment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.SimSystem;
import org.jamesii.core.Registry;
import org.jamesii.gui.utils.AbstractComboBoxModel;
import org.jamesii.gui.workflow.experiment.plugintype.AbstractExperimentSetupEditorFactory;
import org.jamesii.gui.workflow.experiment.plugintype.ExperimentSetupEditorFactory;

/**
 * Simple list model that provides access to all installed experiment types.
 * <p>
 * <b><font color="red">NOTE: the class is intended to be used internally only
 * and is very likely to change in future releases</font></b>
 * 
 * @author Stefan Rybacki
 * @author Jan Himmelspach
 */
class ExperimentTypeListModel extends
    AbstractComboBoxModel<ExperimentSetupEditorFactory> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 5204533862230016732L;

  /**
   * Constructor
   */
  public ExperimentTypeListModel() {
    Registry registry = SimSystem.getRegistry();

    List<ExperimentSetupEditorFactory> factories = null;

    factories =
        registry.getFactoryOrEmptyList(
            AbstractExperimentSetupEditorFactory.class, null);

    final Map<String, ExperimentSetupEditorFactory> experimentEditorFactories =
        new HashMap<>();

    for (ExperimentSetupEditorFactory factory : factories) {
      String fClass = factory.getClass().getName();
      if (experimentEditorFactories.containsKey(fClass)) {
        continue;
      }
      experimentEditorFactories.put(fClass, factory);
    }

    ArrayList<String> editors =
        new ArrayList<>(experimentEditorFactories.keySet());
    Collections.sort(editors);

    for (String f : editors) {
      addElement(experimentEditorFactories.get(f));
    }
  }
}
