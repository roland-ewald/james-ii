/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;


import java.awt.BorderLayout;
import java.util.List;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.steering.ExperimentSteererVariable;
import org.jamesii.core.experiments.variables.ExperimentVariables;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.IWindowListener;
import org.jamesii.gui.application.IWindowManager;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;
import org.jamesii.gui.utils.JTreeView;
import org.jamesii.perfdb.recording.selectiontrees.FactoryVertex;
import org.jamesii.perfdb.recording.selectiontrees.ParameterVertex;
import org.jamesii.perfdb.recording.selectiontrees.SelTreeSetVertex;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.simspex.exploration.ISimSpaceExplorer;
import org.jamesii.simspex.gui.actions.ConfigureExperimentAction;
import org.jamesii.simspex.gui.actions.ExecExplExperiment;
import org.jamesii.simspex.util.SelTreeSetCreation;


/**
 * Window to display and configure SimSpEx-settings for a given experiment.
 * 
 * @author Roland Ewald
 * 
 */
public class ConfigureSimSpExWindow extends AbstractWindow implements
    IWindowListener {

  /** The experiment editor window holding the experiment to be used. */
  private final ExperimentEditor editorWindow;

  /** The experiment on which the simulation space exploration bases. */
  private final BaseExperiment experiment;

  /** The window manager. */
  private final IWindowManager winManager;

  /** Content of this window. */
  private JPanel content = new JPanel(new BorderLayout());

  /** Panel to display vertex description. */
  private JPanel vertexDescription = new JPanel(new BorderLayout());

  /** The panel displaying the constraints for the currently selected vertex. */
  private VertexConstraintsPanel<?> currentConstraintsPanel = null;

  /** The selection tree set to be edited. */
  private SelectionTreeSet selectionTreeSet = null;

  /**
   * Default constructor.
   * 
   * @param expEditor
   *          the experiment editor window
   * @param windowManager
   *          the window manager
   */
  public ConfigureSimSpExWindow(ExperimentEditor expEditor,
      IWindowManager windowManager) {
    super("Exploration with experiment '" + expEditor.getExperiment().getName()
        + "'", null, Contribution.EDITOR);
    editorWindow = expEditor;
    experiment = editorWindow.getExperiment();
    winManager = windowManager;
    winManager.addWindowListener(this);
    initUI();
  }

  /**
   * Initialises the user interface.
   */
  private void initUI() {

    List<ExperimentSteererVariable<?>> steererVars =
        ExperimentVariables.getExperimentSteererVariables(experiment
            .getExperimentVariables());

    // Check whether this experiment is already configured for simulation space
    // exploration
    if (steererVars.size() > 0) {
      for (ExperimentSteererVariable<?> variable : steererVars) {
        if (ISimSpaceExplorer.class.isAssignableFrom(variable.getValue()
            .getClass())) {
          selectionTreeSet =
              ((ISimSpaceExplorer) variable.getValue()).getSelectionTreeSet();
        }
      }
    }

    if (selectionTreeSet == null) {

      // TODO this only allows to explore the simulation space below the
      // processor factory, not partitioning etc.
      try {
        selectionTreeSet =
            SelTreeSetCreation.createSelectionTreeSet(
                experiment.getModelRWParameters(),
                experiment.getModelLocation(),
                experiment.getFixedModelParameters());
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE, null,
            "Constrcution of selection tree set failed.", null, ex);
      }
    }
    if (selectionTreeSet == null) {
      return;
    }

    final JTreeView<SelTreeSetVertex> stSetTree =
        new JTreeView<>(selectionTreeSet.getTree());
    stSetTree.setCellRenderer(new STSetTreeRenderer());
    stSetTree.getSelectionModel().addTreeSelectionListener(
        new TreeSelectionListener() {
          @Override
          public void valueChanged(TreeSelectionEvent e) {
            TreePath treePath = e.getNewLeadSelectionPath();
            if (treePath != null) {
              currentVertexUpdate((SelTreeSetVertex) ((DefaultMutableTreeNode) treePath
                  .getLastPathComponent()).getUserObject());
            } else {
              saveCurrentSettings();
            }
          }
        });

    JScrollPane scrollPane = new JScrollPane(stSetTree);
    JSplitPane splitPane =
        new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane,
            vertexDescription);
    content.add(splitPane, BorderLayout.CENTER);
  }

  /**
   * Updates the user interface for editing another vertex.
   * 
   * @param vertex
   *          the vertex to be edited.
   */
  private void currentVertexUpdate(SelTreeSetVertex vertex) {
    saveCurrentSettings();
    if (currentConstraintsPanel != null) {
      currentConstraintsPanel.save();
      vertexDescription.removeAll();
    }

    if (vertex instanceof FactoryVertex<?>) {
      updateCurrentConstraintsPanel(new FactoryVertexConstraintsPanel(
          (FactoryVertex<?>) vertex));
      return;
    }

    if (vertex instanceof ParameterVertex) {
      updateCurrentConstraintsPanel(new ParamVertexConstraintsPanel(
          (ParameterVertex) vertex));
      return;
    }
    currentConstraintsPanel = null;
    vertexDescription.validate();
  }

  /**
   * Updates vertex description panel.
   * 
   * @param panel
   *          the new panel for constraint editing
   */
  private void updateCurrentConstraintsPanel(VertexConstraintsPanel<?> panel) {
    currentConstraintsPanel = panel;
    vertexDescription.add(currentConstraintsPanel);
    vertexDescription.validate();
  }

  /**
   * Saves the currently selected vertex constraints.
   */
  private void saveCurrentSettings() {
    if (currentConstraintsPanel != null) {
      currentConstraintsPanel.save();
    }
  }

  @Override
  protected IAction[] generateActions() {
    return new IAction[] { new ExecExplExperiment(editorWindow, winManager),
        new ConfigureExperimentAction(editorWindow.getExperiment(), this) };
  }

  @Override
  public JComponent createContent() {
    return content;
  }

  @Override
  public JamesUndoManager getUndoManager() {
    return null;
  }

  @Override
  public boolean isUndoRedoSupported() {
    return false;
  }

  // Window listener methods

  @Override
  public void windowClosed(IWindow window) {
    if (this.editorWindow == window) {
      winManager.closeWindow(this);
    }
  }

  @Override
  public void windowActivated(IWindow window) {
  }

  @Override
  public void windowDeactivated(IWindow window) {
  }

  @Override
  public void windowOpened(IWindow window) {
  }

  public final SelectionTreeSet getSelectionTreeSet() {
    saveCurrentSettings();
    return selectionTreeSet;
  }

}
