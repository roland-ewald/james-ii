/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.edit;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;

import org.jamesii.SimSystem;
import org.jamesii.core.data.experiment.IExperimentWriter;
import org.jamesii.core.data.experiment.write.plugintype.ExperimentWriterFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.misc.Pair;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.experiment.ExperimentPerspective;
import org.jamesii.gui.experiment.actions.RunExpAction;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.factories.SelectFactoryParamDialog;

/**
 * Dialog to set up a base experiment.
 * 
 * @author Roland Ewald
 * 
 *         Date: 25.05.2007
 */
public class ExperimentEditor extends AbstractWindow {

  // For experiment execution:

  /** Reference to an experiment. */
  private final BaseExperiment experiment;

  // For experiment storage

  /** Currently used experiment writer. */
  private IExperimentWriter lastWriter = null;

  /** Parameters for the last experiment writer. */
  private ParameterBlock lastWriterParam = null;

  // OLD:

  /** Check box to decide whether to cancel experiment on error. */
  private JCheckBox cancelOnErorCheckBox = new JCheckBox("Cancel on Error");
  {
    cancelOnErorCheckBox
        .setToolTipText("If checked, the experiment will stop if errors occur.");
  }

  /** List with all experiment editing panels. */
  private List<EditExperimentPanel> experimentPanels = new ArrayList<>();

  /**
   * Tabbed pane to part the different sub-dialogs for aspects of the
   * experiment.
   */
  private JTabbedPane tabbedPane = new JTabbedPane(SwingConstants.LEFT,
      JTabbedPane.SCROLL_TAB_LAYOUT);

  /** Panel to hold all content. */
  private JPanel content = new JPanel(new BorderLayout());

  /**
   * Default constructor.
   * 
   * @param exp
   *          the base experiment to be edited
   * @param param
   *          the param
   */
  public ExperimentEditor(BaseExperiment exp, ParameterBlock param) {
    this(exp, null, param);

  }

  /**
   * Default constructor.
   * 
   * @param exp
   *          the base experiment to be edited
   * @param expWriter
   *          the exp writer
   * @param param
   *          the param
   */
  public ExperimentEditor(BaseExperiment exp, IExperimentWriter expWriter,
      ParameterBlock param) {
    super("Edit Experiment '" + exp.getName() + "'", null, Contribution.EDITOR);
    this.experiment = exp;
    ExperimentPerspective.configureExperimentWithUIDefaults(exp);

    lastWriter = expWriter;
    lastWriterParam = param;

    JPanel buttonPanel = new JPanel(new BorderLayout());
    buttonPanel.add(cancelOnErorCheckBox, BorderLayout.WEST);

    experimentPanels.add(new EditModelParameter(experiment));
    experimentPanels.add(new EditExperimentStructure(experiment));
    experimentPanels.add(new EditSimulationParameter(experiment));
    experimentPanels.add(new EditReplications(experiment));
    experimentPanels.add(new EditInstrumentation(experiment));
    experimentPanels.add(new EditDataStorage(experiment));
    experimentPanels.add(new EditSimulationRunner(experiment));

    cancelOnErorCheckBox.setSelected(experiment.isCancelOnError());

    for (EditExperimentPanel editPanel : experimentPanels) {
      tabbedPane.addTab(editPanel.getName(), editPanel);
    }

    content.add(tabbedPane, BorderLayout.CENTER);
    content.add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Close dialog.
   */
  protected void closeDialog() {
    saveToObject();
    getWindowManager().closeWindow(this);
  }

  /**
   * Save all information provided by the UI to the current experiment.
   */
  public void saveToObject() {
    experiment.setCancelOnError(cancelOnErorCheckBox.isSelected());

    for (EditExperimentPanel editPanel : experimentPanels) {
      editPanel.closeDialog();
    }
  }

  @Override
  public boolean canClose() {
    return true;
  }

  @Override
  public Contribution getContribution() {
    return Contribution.EDITOR;
  }

  /** The run exp action. */
  private RunExpAction runExpAction = null;

  @Override
  protected IAction[] generateActions() {
    if (runExpAction == null) {
      runExpAction =
          new RunExpAction(experiment, getWindowManager(),
              ExperimentPerspective.getExperimentObservationManager(),
              new IExperimentExecutionListener[] {}, this);
    }
    return new IAction[] { new ActionIAction(runExpAction,
        "org.jamesii.experiments.runexp", new String[] { "" }, this) };
  }

  @Override
  public boolean isSaveable() {
    return true;
  }

  @Override
  public void save() {
    if (lastWriter == null) {
      saveAs();
      return;
    }
    saveToObject();
    try {
      lastWriter.writeExperiment(lastWriterParam, experiment);
      // TODO: recently saved menu
      // this.recentExpMenu.updateItemDisplayString(experiment.getName(),
      // lastExperimentWriterParam);
      SimSystem.report(Level.INFO, "Experiment saved to: "
          + ExperimentPerspective.getExpLocation(lastWriterParam));
    } catch (IOException ex) {
      SimSystem.report(Level.SEVERE, "Error saving experiment located at '"
          + ExperimentPerspective.getExpLocation(lastWriterParam)
          + "' with writer + '" + lastWriter.getClass().toString() + "'", ex);
    }
  }

  @Override
  public void saveAs() {
    saveToObject();
    SelectFactoryParamDialog<ExperimentWriterFactory> selFacParamDialog =
        new SelectFactoryParamDialog<>("Save Experiment to:",
            ExperimentWriterFactory.class);
    selFacParamDialog.setVisible(true);
    Pair<ExperimentWriterFactory, IFactoryParameterDialog<?>> selectedDialog =
        selFacParamDialog.getSelectedFactory();
    if (selectedDialog == null) {
      SimSystem.report(Level.SEVERE, "Could not open Save-As dialog.");
      return;
    }

    Pair<ParameterBlock, ? extends Factory> parameters =
        selectedDialog.getSecondValue().getFactoryParameter(null);

    if (parameters == null) {
      return;
    }

    IExperimentWriter writer =
        ((ExperimentWriterFactory) parameters.getSecondValue())
            .create(parameters.getFirstValue());

    if (writer == null) {
      SimSystem.report(Level.SEVERE, "Could not initialise experiment writer.");
      return;
    }

    lastWriter = writer;
    lastWriterParam = parameters.getFirstValue();
    save();
  }

  /**
   * Gets the experiment.
   * 
   * @return the experiment
   */
  public BaseExperiment getExperiment() {
    return experiment;
  }

  /**
   * Gets the run exp action.
   * 
   * @return the run exp action
   */
  public RunExpAction getRunExpAction() {
    return runExpAction;
  }

  @Override
  protected JComponent createContent() {
    return content;
  }

}
