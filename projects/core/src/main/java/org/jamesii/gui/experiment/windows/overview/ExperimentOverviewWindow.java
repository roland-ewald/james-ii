/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment.windows.overview;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jamesii.SimSystem;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ComputationRuntimeState;
import org.jamesii.core.experiments.ComputationTaskRuntimeInformation;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.experiments.RunInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.experiments.taskrunner.ITaskRunner;
import org.jamesii.core.math.Calc;
import org.jamesii.core.util.StopWatch;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.IWindow;
import org.jamesii.gui.application.action.AbstractAction;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.gui.experiment.actions.AbstractSimAction;
import org.jamesii.gui.experiment.actions.ModelStructureViewAction;
import org.jamesii.gui.experiment.actions.NStepsSimAction;
import org.jamesii.gui.experiment.actions.NextStepSimAction;
import org.jamesii.gui.experiment.actions.RunExpAction;
import org.jamesii.gui.experiment.actions.RunSimAction;
import org.jamesii.gui.experiment.actions.SliderSimAction;
import org.jamesii.gui.experiment.actions.StopSimAction;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.SimpleGUIFileExporter;

/**
 * Window to show the overall progress of an experiment. For example, which
 * simulation runs have been done, which are still running, which are
 * initialized, ...
 * 
 * @author Roland Ewald
 */
@SuppressWarnings("deprecation")
public class ExperimentOverviewWindow extends AbstractWindow implements
    IExperimentExecutionListener, IWindow {

  /** Content of the window. */
  private JPanel content = new JPanel(new BorderLayout());

  /** Stop watch to take the time for the last experiment. */
  private StopWatch stopWatch = new StopWatch();

  /**
   * This is the action that triggered the experiment. Will be displayed in the
   * toolbar as well.
   */
  private final RunExpAction runExpAction;

  /** Button to clear the table. */
  // TODO: Convert this to an action
  private JButton clearButton = new JButton("Clear");
  {
    clearButton.setToolTipText("Clears table with simulation run information.");
    clearButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (BasicUtilities.printQuestion(null, "Clear execution list?",
            "Do you really want to clear the list of simulation runs?") == JOptionPane.YES_OPTION) {
          simRunTableModel.clear();
        }
        expDurationLabel.setText("");
      }
    });
  }

  /** Button to export simulation run information to CSV. */
  // TODO: Convert this to an action
  private JButton exportButton = new JButton("Export to file");
  {
    exportButton.setToolTipText("Export contents of this view to a file.");
    exportButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        export();
      }
    });
  }

  /** Panel to display the buttons. */
  private JPanel buttonPanel = new JPanel();

  /** Table model for simulation run table. */
  private SimRunTableModel simRunTableModel = new SimRunTableModel();

  /** Table to display status of simulation runs. */
  private JTable simRunTable = new JTable(simRunTableModel);
  {
    simRunTable.setDoubleBuffered(true);
  }

  /** ScrollPane for the table. */
  private JScrollPane tableScrollPane = new JScrollPane(simRunTable);

  /** Label to display duration of last experiment. */
  private JLabel expDurationLabel = new JLabel("");

  /** String to prefix the time the last experiment took. */
  private String expDurationPrefix = "Duration of last exp:";

  /** Check-box to fix the size of the table. */
  private JCheckBox fixSizeCheckBox = new JCheckBox("Fix size of table");

  /** Central action for running the simulation. */
  private AbstractSimAction stopSimAction = new StopSimAction();

  /** Central action for running the simulation. */
  private AbstractSimAction runSimAction = new RunSimAction(simRunTableModel);

  /** Central action for running the next step. */
  private AbstractSimAction nextStepSimAction = new NextStepSimAction();

  /** Central action for running the next n steps. */
  private AbstractSimAction nStepsSimAction = new NStepsSimAction();

  /** The action for adjusting simulation speed with a slider. */
  private AbstractSimAction sliderSimAction = new SliderSimAction();

  /** The action for adjusting simulation speed with a slider. */
  private AbstractSimAction strucModelViewAction =
      new ModelStructureViewAction();

  /** The content pane. */
  private JPanel contentPane;
  {
    fixSizeCheckBox.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        if (fixSizeCheckBox.isEnabled()) {
          simRunTableModel.setMaxSize(simRunTableModel.getRowCount());
        } else {
          simRunTableModel.setMaxSize(Integer.MAX_VALUE);
        }
      }
    });
  }

  /**
   * Default constructor.
   * 
   * @param expAction
   *          the action that executes the experiment
   */
  public ExperimentOverviewWindow(RunExpAction expAction) {
    super("Simulation Runs", null, Contribution.EDITOR);
    runExpAction = expAction;
  }

  /**
   * Default constructor.
   * 
   * @param expName
   *          the experiment name
   * @param expAction
   *          the action that executes the experiment
   */
  public ExperimentOverviewWindow(String expName, RunExpAction expAction) {
    this(expAction);
    setTitle(getTitle() + " for '" + expName + "'");
  }

  /**
   * Gets the simulation runtime information of all displayed runs.
   * 
   * @return the list of simulation runtime informations
   */
  public List<ComputationTaskRuntimeInformation> getSRTIs() {
    List<ComputationTaskRuntimeInformation> srtiList = new ArrayList<>();
    for (SimulationTableInformation simTabInfo : simRunTableModel
        .getSimTableInfos()) {
      if (simTabInfo.getSrti() != null) {
        srtiList.add(simTabInfo.getSrti());
      }
    }
    return srtiList;
  }

  /**
   * Exports table information to file specified by user.
   */
  protected void export() {
    SimpleGUIFileExporter fileExporter = new SimpleGUIFileExporter() {
      @Override
      public void export(BufferedWriter bufferedWriter) throws IOException {
        List<SimulationTableInformation> simTableInfos =
            simRunTableModel.getSimTableInfos();
        for (SimulationTableInformation simTableInfo : simTableInfos) {
          if (simTableInfo.getSimState() != ComputationRuntimeState.FINISHED) {
            continue;
          }

          // 1: store the simConfig's model parameters
          TaskConfiguration simConfig =
              simTableInfo.getSrti().getComputationTaskConfiguration();
          Map<String, ?> parameters = simConfig.getParameters();
          for (Entry<String, ?> parameter : parameters.entrySet()) {
            bufferedWriter.append(parameter.getKey() + "="
                + parameter.getValue() + sepSequence);
          }

          // 2: store some of the simConfig's simulation parameters
          bufferedWriter.append(simConfig.getSimStartTime() + sepSequence);
          bufferedWriter.append(simConfig.getSimStopFactory() + sepSequence);

          // 3: store the run information
          RunInformation runInfo = simTableInfo.getRunInfo();
          bufferedWriter.append(runInfo.getTotalRuntime() + sepSequence);
          bufferedWriter.append(runInfo.getModelCreationTime() + sepSequence);
          bufferedWriter.append(runInfo.getObserverConfigurationTime()
              + sepSequence);
          bufferedWriter.append(runInfo.getComputationTaskCreationTime()
              + sepSequence);
          bufferedWriter.append(runInfo.getComputationTaskRunTime()
              + sepSequence);
          bufferedWriter.append('\n');
        }
      }
    };
    fileExporter.export(getContent());
  }

  /**
   * Selects cell renderer for given column.
   * 
   * @param colIndex
   *          index of the column
   * @return appropriate cell renderer.
   */
  protected TableCellRenderer getCellRenderer(int colIndex) {
    switch (colIndex) {
    case 1:
      return new SimConfigCellRenderer();
    case 2:
      return new SimJobStatusCellRenderer();
    case 3:
      return new RunInfoCellRenderer();
    default:
      return new DefaultTableCellRenderer();
    }
  }

  /**
   * Inits the ui.
   */
  protected void initUI() {
    contentPane = new JPanel(new BorderLayout());
    contentPane.add(content, BorderLayout.CENTER);

    content.add(tableScrollPane, BorderLayout.CENTER);
    buttonPanel.add(clearButton);
    buttonPanel.add(exportButton);
    buttonPanel.add(expDurationLabel);
    buttonPanel.add(fixSizeCheckBox);
    contentPane.add(buttonPanel, BorderLayout.SOUTH);

    // Add renderer
    for (int i = 0; i < simRunTable.getColumnCount(); i++) {
      TableColumn column = simRunTable.getColumnModel().getColumn(i);
      TableCellRenderer tcRenderer = getCellRenderer(i);
      column.setCellRenderer(tcRenderer);
    }

    // First column holds the simulation run number and is therefore
    // not so
    // wide
    simRunTable.getColumnModel().getColumn(0).setPreferredWidth(40);
    simRunTable.getColumnModel().getColumn(0).setMaxWidth(80);
    simRunTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    simRunTable.getSelectionModel()
        .addListSelectionListener(
            new SelectionListener(simRunTableModel, simRunTable
                .getSelectionModel()));
  }

  @Override
  public void simulationExecuted(ITaskRunner simRunner,
      final ComputationTaskRuntimeInformation crti, final boolean jobDone) {
    try {
      SwingUtilities.invokeLater(new Runnable() {
        @Override
        public void run() {

          SimulationTableInformation simTabInfo =
              simRunTableModel.getSimTableInformation(crti);

          if (simTabInfo == null) {
            return;
          }

          simTabInfo.setRunInfo(crti.getRunInformation());
          simTabInfo.getSrti().computationTaskFinished();
          simRunTableModel.propagateUpdate(crti);

          if (jobDone && crti.getSimulationRunConfiguration() != null) {
            SimSystem.report(Level.INFO, "The simulation of configuration #"
                + crti.getSimulationRunConfiguration().getNumber()
                + " has been finished.");
          }
        }
      });
    } catch (Exception e) {
      SimSystem.report(e);
    }
  }

  @Override
  public void simulationInitialized(ITaskRunner simRunner,
      final ComputationTaskRuntimeInformation crti) {
    SwingUtilities.invokeLater(new Runnable() {
      @Override
      public void run() {

        simRunTableModel.addSimulationRun(crti);
        // If we added the very first sim run, and it is paused from
        // the
        // beginning on we automatically select it
        if ((simRunTableModel.getRowCount() == 1)
            && crti.getSimulationRunConfiguration() != null
            && crti.getSimulationRunConfiguration().isStartPaused()) {
          simRunTable.selectAll();
        }
      }
    });
  }

  @Override
  public void experimentExecutionStarted(BaseExperiment experiment) {
    stopWatch.reset();
    stopWatch.start();
  }

  @Override
  public void experimentExecutionStopped(BaseExperiment experiment) {
    stopWatch.stop();
    expDurationLabel.setText(expDurationPrefix + " "
        + stopWatch.elapsedMilliseconds() + " ms");
  }

  @Override
  public boolean canClose() {
    // check whether there are still simulations running and ask for
    // permission
    // to stop all simulation runs in order to close window or to
    // cancel
    if (!runExpAction.getExpExecControl().getExperiment().isFinished()
        && !runExpAction.getExpExecControl().getExperiment().isStopping()) {
      int res =
          JOptionPane
              .showConfirmDialog(
                  getWindowManager().getMainWindow(),
                  "The Experiment is still running.\nIf the window is closed the experiment will be stopped.\nAre you sure you want to do this?",
                  "Stop Experiment", JOptionPane.YES_NO_OPTION);
      if (res == JOptionPane.YES_OPTION) {
        runExpAction.getExpExecControl().getExperiment().stop(true);
        return true;
      }
      return false;
    }

    return true;
  }

  @Override
  public Contribution getContribution() {
    return Contribution.BOTTOM_VIEW;
  }

  @Override
  protected IAction[] generateActions() {

    IAction clearAction =
        new AbstractAction("expoverview.clear", "Clear", "Clears table",
            "Clears table with simulation run information.",
            IconManager.getIcon(IconIdentifier.DELETE_SMALL, "Clear"),
            new String[] { "" }, null, null, this) {
          @Override
          public void execute() {
            synchronized (this) {
              if (JOptionPane.showConfirmDialog(null, "Clear execution list?",
                  "Do you really want to clear the list of simulation runs?",
                  JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                simRunTableModel.clear();
              }
              expDurationLabel.setText("");
            }
          }
        };

    IAction exportAction =
        new AbstractAction("expoverview.export", "Export", "Exports table",
            "Exports table with simulation run information to text file.",
            IconManager.getIcon(IconIdentifier.COPY_SMALL, null),
            new String[] { "" }, null, null, this) {
          @Override
          public void execute() {
            synchronized (this) {
              export();
            }
          }
        };

    IAction runExpAct =
        new ActionIAction(runExpAction, "expoverview.runexpaction",
            new String[] { "" }, this);

    IAction stopSimAct =
        new ActionIAction(stopSimAction, "expoverview.stopsimaction",
            new String[] { "" }, this);

    IAction runSimAct =
        new ActionIAction(runSimAction, "expoverview.runsimaction",
            new String[] { "" }, this);

    IAction nextStepSimAct =
        new ActionIAction(nextStepSimAction, "expoverview.nextstepsimaction",
            new String[] { "" }, this);

    IAction nStepsSimAct =
        new ActionIAction(nStepsSimAction, "expoverview.nstepssimaction",
            new String[] { "" }, this);

    IAction sliderAction =
        new ActionIAction(sliderSimAction, "expoverview.slideraction",
            new String[] { "" }, this);

    IAction strucViewAction =
        new ActionIAction(strucModelViewAction, "expoverview.strucviewaction",
            new String[] { "" }, this);

    return new IAction[] { clearAction, exportAction,
        SeparatorAction.getSeparatorFor("", this), runExpAct,
        SeparatorAction.getSeparatorFor("", this), stopSimAct, runSimAct,
        nextStepSimAct, nStepsSimAct, sliderAction,
        SeparatorAction.getSeparatorFor("", this), strucViewAction };
  }

  @Override
  public String getWindowID() {
    return "org.jamesii.experiment.overview";
  }

  @Override
  protected JComponent createContent() {
    initUI();
    return content;
  }

  private class ButtonBar extends JPanel {

  }

  /**
   * Listens to selections of the table and initialises simulation actions
   * accordingly.
   * 
   * @author Roland Ewald
   */
  private class SelectionListener implements ListSelectionListener {

    /**
     * Model of the table containing the {@link SimulationTableInformation}.
     */
    private final SimRunTableModel tableModel;

    /** List selection model. */
    private final ListSelectionModel listSelModel;

    /** Stores last selection index (to avoid duplicate calls). */
    private int lastIndex = -1;

    /**
     * Default constructor.
     * 
     * @param tabModel
     *          the table model
     * @param listSelMod
     *          the list selection model
     */
    public SelectionListener(SimRunTableModel tabModel,
        ListSelectionModel listSelMod) {
      tableModel = tabModel;
      listSelModel = listSelMod;
    }

    @Override
    public void valueChanged(ListSelectionEvent e) {
      int index = listSelModel.getMaxSelectionIndex();
      if (lastIndex == index) {
        return;
      }
      if (index < 0 || index >= tableModel.getSimTableInfos().size()) {
        runSimAction.init(null);
        nextStepSimAction.init(null);
        nStepsSimAction.init(null);
        sliderSimAction.init(null);
        stopSimAction.init(null);
        strucModelViewAction.init(null);
        return;
      }
      SimulationTableInformation simTableInfos =
          tableModel.getSimTableInfos().get(index);
      runSimAction.init(simTableInfos.getSrti());
      nextStepSimAction.init(simTableInfos.getSrti());
      nStepsSimAction.init(simTableInfos.getSrti());
      sliderSimAction.init(simTableInfos.getSrti());
      stopSimAction.init(simTableInfos.getSrti());
      strucModelViewAction.init(simTableInfos.getSrti());
      lastIndex = index;
    }
  }

  @Override
  public void windowClosed() {
    super.windowClosed();
    runExpAction.removeExpExecListener(this);
  }

}

/**
 * Renders the cell containing the {@link RunInformation} of a (finished)
 * simulation run.
 * 
 * @author Roland Ewald
 */
class RunInfoCellRenderer extends SimCellRenderer {

  /** Serialization ID. */
  private static final long serialVersionUID = 1L;

  @Override
  void renderSimTableInfo(SimulationTableInformation sti) {
    if (sti.getRunInfo() == null) {
      this.setText("Not available.");
      return;
    }

    String perfText =
        "Total:" + Calc.round(sti.getRunInfo().getTotalRuntime(), 2)
            + "- Model:"
            + Calc.round(sti.getRunInfo().getModelCreationTime(), 2)
            + ", Observers:"
            + Calc.round(sti.getRunInfo().getObserverConfigurationTime(), 2)
            + ", Sim:"
            + Calc.round(sti.getRunInfo().getComputationTaskCreationTime(), 2)
            + ", Processing:"
            + Calc.round(sti.getRunInfo().getComputationTaskRunTime(), 2);

    this.setText(perfText);
  }

}

/**
 * Super class for all other cell renderer.
 * 
 * @author Roland Ewald
 */
abstract class SimCellRenderer extends DefaultTableCellRenderer {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -8926002988010907858L;

  @Override
  public Component getTableCellRendererComponent(JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column) {
    super.getTableCellRendererComponent(table, value, isSelected, hasFocus,
        row, column);

    if (!(value instanceof SimulationTableInformation)) {
      return this;
    }

    renderSimTableInfo((SimulationTableInformation) value);
    return this;
  }

  /**
   * Render sim table info.
   * 
   * @param sti
   *          the sti
   */
  abstract void renderSimTableInfo(SimulationTableInformation sti);
}

/**
 * The Class SimConfigCellRenderer.
 */
class SimConfigCellRenderer extends SimCellRenderer {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = -8926002988010907858L;

  @Override
  void renderSimTableInfo(SimulationTableInformation sti) {
    TaskConfiguration simConfig =
        sti.getSrti().getComputationTaskConfiguration();
    this.setText("URI:"
        + simConfig.getModelReaderParams().getSubBlockValue("URI"));
  }
}

/**
 * The Class SimJobStatusCellRenderer.
 */
class SimJobStatusCellRenderer extends SimCellRenderer {

  /**
   * Serialization ID.
   */
  private static final long serialVersionUID = 5003899269567187326L;

  @Override
  void renderSimTableInfo(SimulationTableInformation sti) {

    switch (sti.getSimState()) {
    case INITIALIZED:
      this.setText("Initialized");
      this.setForeground(this.getForeground());
      break;
    case PAUSED:
      this.setText("Paused");
      this.setForeground(Color.GRAY);
      break;
    case RUNNING:
      this.setText("Running...");
      this.setForeground(Color.BLUE);
      break;
    case FINISHED:
      this.setText("Finished");
      this.setForeground(Color.GREEN);
      break;
    case CANCELLED:
      this.setText("Cancelled");
      this.setForeground(Color.RED);
      break;
    }
  }

}
