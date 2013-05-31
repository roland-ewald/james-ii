/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.instrumentation.computation.IComputationInstrumenter;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.AbstractComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.computation.plugintype.ComputationInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.IModelInstrumenter;
import org.jamesii.core.experiments.instrumentation.model.plugintype.AbstractModelInstrumenterFactory;
import org.jamesii.core.experiments.instrumentation.model.plugintype.ModelInstrumenterFactory;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.utils.FactoryListCellRenderer;

/**
 * Panel to edit the instrumenters for a given experiment.
 * 
 * @author Roland Ewald
 */
public class EditInstrumentation extends EditExperimentPanel {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1271509174072944402L;

  /** List of suitable model instrumenter factories. */
  private List<ModelInstrumenterFactory> modelInstrumenters = null;

  /** Selected model instrumenter factory. */
  private ModelInstrumenterFactory selectedModInstrFac = null;

  /** List of suitable simulation instrumenter factories. */
  private List<ComputationInstrumenterFactory> simulationInstrumenters = null;

  /** Selected simulation instrumenter factory. */
  private ComputationInstrumenterFactory selectedSimInstrFac = null;

  /** Panel to edit model instrumenter factory. */
  private JPanel editModelInstrFactoryPanel = new JPanel();

  /** List to display model instrumenters. */
  private JList modelInstrList = null;

  /** Panel to edit simulation instrumenter factory. */
  private JPanel editSimInstrFactoryPanel = new JPanel();

  /** List to display simulation instrumenters. */
  private JList simInstrList = null;

  /**
   * Default constructor.
   * 
   * @param exp
   *          the experiment to be edited
   */
  public EditInstrumentation(BaseExperiment exp) {
    super(exp);
    this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
    initModelInstrumenters();
    initSimulationInstrumenters();
  }

  /**
   * Initializes UI for model instrumenters.
   */
  protected void initModelInstrumenters() {
    JPanel modInstrPanel = new JPanel(new BorderLayout());
    try {
      modelInstrumenters =
          SimSystem.getRegistry().getFactoryList(
              AbstractModelInstrumenterFactory.class,
              new ParameterBlock(getExperiment().getModelLocation(),
                  AbstractModelInstrumenterFactory.MODELURI));
    } catch (RuntimeException ex) {
      modInstrPanel.add(new JLabel("No suitable model instrumenter found."),
          BorderLayout.CENTER);
    }
    if (modelInstrumenters != null) {
      modelInstrumenters.add(0, new NoModelInstrumentation());
      modelInstrList = new JList(modelInstrumenters.toArray());
      modelInstrList.setCellRenderer(new FactoryListCellRenderer());
      modelInstrList.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          selectedModInstrFac =
              modelInstrList.getSelectedIndex() == 0 ? null
                  : modelInstrumenters.get(modelInstrList.getSelectedIndex());
        }
      });
    }

    configurePanel(modInstrPanel, modelInstrList, getExperiment().getModelInstrumenterFactory().getFactoryInstance());
  }

  /**
   * Initializes UI for simulation instrumenters.
   */
  protected void initSimulationInstrumenters() {
    JPanel simInstrPanel = new JPanel(new BorderLayout());
    try {
      simulationInstrumenters =
          SimSystem.getRegistry().getFactoryList(
              AbstractComputationInstrumenterFactory.class,
              new ParameterBlock(getExperiment().getModelLocation(),
                  AbstractComputationInstrumenterFactory.MODELURI));
    } catch (RuntimeException ex) {
      simInstrPanel.add(
          new JLabel("No suitable simulation instrumenter found."),
          BorderLayout.CENTER);
    }
    if (simulationInstrumenters != null) {
      simulationInstrumenters.add(0, new NoComputationTaskInstrumentation());
      simInstrList = new JList(simulationInstrumenters.toArray());
      simInstrList.setCellRenderer(new FactoryListCellRenderer());
      simInstrList.addListSelectionListener(new ListSelectionListener() {
        @Override
        public void valueChanged(ListSelectionEvent e) {
          selectedSimInstrFac =
              simInstrList.getSelectedIndex() == 0 ? null
                  : simulationInstrumenters.get(simInstrList.getSelectedIndex());
        }
      });
    }

    configurePanel(simInstrPanel, simInstrList, getExperiment().getComputationInstrumenterFactory().getFactoryInstance());
  }

  /**
   * Configures panel for display.
   * 
   * @param panel
   *          the panel to be configured
   * @param instrumenterList
   *          the list with instrumenter factories
   * @param selectedInstrumenterFactory
   *          the selected instrumenter's factory
   */
  protected void configurePanel(JPanel panel, JList instrumenterList,
      Factory selectedInstrumenterFactory) {

    if (instrumenterList != null) {

      int selectionIndex = -1;
      if (selectedInstrumenterFactory != null) {
        for (int i = 1; i < instrumenterList.getModel().getSize(); i++) {
          if (instrumenterList
              .getModel()
              .getElementAt(i)
              .getClass()
              .getCanonicalName()
              .compareTo(
                  selectedInstrumenterFactory.getClass().getCanonicalName()) == 0) {
            selectionIndex = i;
          }
        }
      }

      instrumenterList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      instrumenterList
          .setSelectedIndex(selectionIndex < 0 ? 0 : selectionIndex);
      panel.add(new JScrollPane(instrumenterList), BorderLayout.CENTER);
      panel.add(editModelInstrFactoryPanel, BorderLayout.SOUTH);
    }
    this.add(panel);
  }

  @Override
  public void closeDialog() {
    getExperiment().setModelInstrumenterFactory(selectedModInstrFac);
    getExperiment().setComputationInstrumenterFactory(selectedSimInstrFac);
  }

  @Override
  public String getName() {
    return "Instrumentation";
  }

}

/**
 * Default element in factory list, means that no instrumenter shall be used.
 * This class is named in a way that makes it self-explanatory when displayed in
 * a list.
 * 
 * @see FactoryListCellRenderer
 */
final class NoComputationTaskInstrumentation extends
    ComputationInstrumenterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 1676485172133596192L;

  @Override
  public IComputationInstrumenter create(ParameterBlock parameter) {
    return null;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 0;
  }
}

/** See {@link NoComputationTaskInstrumentation}. */
final class NoModelInstrumentation extends ModelInstrumenterFactory {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -8967358197789143970L;

  @Override
  public IModelInstrumenter create(ParameterBlock parameter) {
    return null;
  }

  @Override
  public int supportsParameters(ParameterBlock params) {
    return 0;
  }
}
