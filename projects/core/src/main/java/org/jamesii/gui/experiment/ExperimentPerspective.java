/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.experiment.ExperimentInfo;
import org.jamesii.core.data.experiment.IExperimentReader;
import org.jamesii.core.data.experiment.read.plugintype.AbstractExperimentReaderFactory;
import org.jamesii.core.data.experiment.read.plugintype.ExperimentReaderFactory;
import org.jamesii.core.data.experimentsuite.IExperimentSuiteReader;
import org.jamesii.core.data.experimentsuite.read.plugintype.ExperimentSuiteReaderFactory;
import org.jamesii.core.experiments.BaseExperiment;
import org.jamesii.core.experiments.ExperimentSuite;
import org.jamesii.core.experiments.IExperimentExecutionListener;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.gui.application.WindowManagerManager;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.preferences.IPreferencesPage;
import org.jamesii.gui.experiment.actions.ExperimentReadAction;
import org.jamesii.gui.experiment.actions.ExperimentSuiteReadAction;
import org.jamesii.gui.experiment.actions.NewExperimentAction;
import org.jamesii.gui.experiment.actions.RunExpAction;
import org.jamesii.gui.experiment.actions.SliderSimAction;
import org.jamesii.gui.experiment.execution.ExperimentObservationManager;
import org.jamesii.gui.experiment.windows.edit.ExperimentEditor;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.dialogs.IFactoryParameterDialog;
import org.jamesii.gui.utils.dialogs.plugintype.AbstractFactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogFactory;
import org.jamesii.gui.utils.dialogs.plugintype.FactoryParameterDialogParameter;

/**
 * Perspective that provides means to edit/run/manage experiments. (ported)
 * 
 * @author Stefan Rybacki
 */
public class ExperimentPerspective extends AbstractPerspective {
  /**
   * Reference to an object that manages the current observers.
   */
  private static ExperimentObservationManager observationManager =
      new ExperimentObservationManager();

  /**
   * Default keep-alive time for waiting experiment execution threads.
   */
  public static final int DEFAULT_KEEP_ALIVE = 60;

  /**
   * Default maximal pool size of experiment execution thread.
   */
  public static final int DEFAULT_MAX_POOL_SIZE = 5;

  @Override
  public String getDescription() {
    return "Perspective to setup, manage, and run experiments";
  }

  @Override
  public String getName() {
    return "Experiment perspective";
  }

  /**
   * Opens experiment.
   * 
   * @param expRWFac
   *          factory for reading/writing experiments
   * @param param
   *          parameter for experiment reader
   */
  public void openExperiment(ExperimentReaderFactory expRWFac,
      ParameterBlock param) {
    // IExperimentWriter writer = expRWFac.getWriter(param);
    IExperimentReader reader = expRWFac.create(param);
    try {
      BaseExperiment exp = reader.readExperiment(param);
      if (exp == null) {
        SimSystem.report(Level.SEVERE, null,
            "Error opening experiment located at '" + getExpLocation(param)
                + "' with reader + '" + reader.getClass().toString() + "'",
            null);
        return;
      }
      getWindowManager().addWindow(new ExperimentEditor(exp, param));
      SimSystem.report(Level.INFO, null, "Opened experiment '" + exp.getName()
          + "' from " + getExpLocation(param), null);
    } catch (IOException ex) {
      SimSystem.report(Level.SEVERE, null,
          "Error opening experiment located at '" + getExpLocation(param)
              + "' with reader + '" + reader.getClass().toString() + "'", null,
          ex);
    }
  }

  /**
   * Open experiment suite.
   * 
   * @param factory
   *          factory that creates the reader
   * @param param
   *          parameters for the reader
   */
  public void openExperimentSuite(ExperimentSuiteReaderFactory factory,
      ParameterBlock param) {
    try {
      IExperimentSuiteReader reader = factory.create(param);

      ExperimentSuite<BaseExperiment> expSuite =
          reader.readExperimentSuite(BaseExperiment.class, param);
      if (expSuite == null) {
        SimSystem.report(Level.SEVERE, null,
            "Error opening experiment suite located at '"
                + getExpLocation(param) + "' with reader + '"
                + reader.getClass().toString() + "'", null);
        return;
      }
      SimSystem.report(Level.INFO, null, "Opened experiment suite from '"
          + getExpLocation(param) + "'", null);
      // TODO: Integrate experiment suites
      // getWindowManager().addWindow(
      // new EditExperimentSuiteWindow(null, expSuite));
    } catch (IOException ex) {
      SimSystem.report(Level.SEVERE, null,
          "Error opening experiment suite located at '" + getExpLocation(param)
              + "' with factory + '" + factory.getClass().toString() + "'",
          null, ex);
    }
  }

  /**
   * Opens experiment.
   * 
   * @param expURI
   *          URI of the experiment definition
   * @throws IOException
   *           will be thrown if opening fails
   */
  public void openExperiment(URI expURI) throws IOException {
    ParameterBlock aerwfp =
        new ParameterBlock(new ExperimentInfo(expURI, null),
            AbstractExperimentReaderFactory.EXPERIMENT_INFO);
    ExperimentReaderFactory erwf =
        SimSystem.getRegistry().getFactory(
            AbstractExperimentReaderFactory.class, aerwfp);
    openExperiment(erwf, aerwfp);
  }

  @Override
  protected List<IAction> generateActions() {
    List<IAction> actions = new ArrayList<>();

    actions.add(new NewExperimentAction(getWindowManager(), null));
    // TODO: integrate experiment suites
    // actions.add(new NewExperimentSuiteAction(getWindowManager()));

    String[] openExpPaths =
        new String[] {
            "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open",
            "org.jamesii.toolbar.main/org.jamesii.open" };
    actions.add(new ActionSet("org.jamesii.open.experiment", "Experiment",
        openExpPaths, null));

    String[] openExpSuitePaths =
        new String[] {
            "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open",
            "org.jamesii.toolbar.main/org.jamesii.open" };
    actions.add(new ActionSet("org.jamesii.open.experimentsuite",
        "Experiment Suite", openExpSuitePaths, null));

    try {
      ParameterBlock paramBlock =
          FactoryParameterDialogParameter.getParameterBlock(
              ExperimentReaderFactory.class, null);

      List<FactoryParameterDialogFactory<?, ?, ?>> factories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFactoryParameterDialogFactory.class, paramBlock);

      for (FactoryParameterDialogFactory<?, ?, ?> f : factories) {
        try {
          IFactoryParameterDialog<?> dialog = f.create(paramBlock);

          ExperimentReadAction a =
              new ExperimentReadAction(this, dialog,
                  dialog.getMenuDescription());
          actions
              .add(new ActionIAction(
                  a,
                  "org.jamesii.experiment." + dialog.hashCode(),
                  new String[] {
                      "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/org.jamesii.open.experiment",
                      "org.jamesii.toolbar.main/org.jamesii.open/org.jamesii.open.experiment" },
                  null));
        } catch (Exception e) {
          SimSystem.report(e);
        }

      }

      // TODO: integrate experiment suites
      // actions
      // .add(new ActionSet(
      // "org.jamesii.experimentsuites.open",
      // "Experiment Suite",
      // new String[] {
      // "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open?after=org.jamesii.experiment.open",
      // "org.jamesii.toolbar.main/org.jamesii.open?after=org.jamesii.experiment.open"
      // }));
      // actions.addAll(initMenu("org.jamesii.experimentsuites.open", new
      // String[] {
      // "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open",
      // "org.jamesii.toolbar.main/org.jamesii.open" }, DialogTask.READ_SUITE));

      paramBlock =
          FactoryParameterDialogParameter.getParameterBlock(
              ExperimentSuiteReaderFactory.class, null);

      factories =
          SimSystem.getRegistry().getFactoryList(
              AbstractFactoryParameterDialogFactory.class, paramBlock);

      for (FactoryParameterDialogFactory<?, ?, ?> f : factories) {
        IFactoryParameterDialog<?> dialog = f.create(paramBlock);

        ExperimentSuiteReadAction a =
            new ExperimentSuiteReadAction(this, dialog,
                dialog.getMenuDescription());
        actions
            .add(new ActionIAction(
                a,
                "org.jamesii.experiment." + dialog.hashCode(),
                new String[] {
                    "org.jamesii.menu.main/org.jamesii.file/org.jamesii.open/org.jamesii.open.experimentsuite",
                    "org.jamesii.toolbar.main/org.jamesii.open/org.jamesii.open.experimentsuite" },
                null));
      }
    } catch (Exception e) {
      SimSystem.report(e);
    }

    return actions;
  }

  /**
   * Auxiliary function for displaying log messages related to experiment I/O.
   * 
   * @param param
   *          parameter block of experiment reader/writer factory
   * @return the location of the experiment
   */
  public static String getExpLocation(ParameterBlock param) {
    if (!param.hasSubBlock(AbstractExperimentReaderFactory.EXPERIMENT_INFO)
        || !(param
            .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO) instanceof ExperimentInfo)) {
      return "UNKNOWN";
    }
    return BasicUtilities.displayURI(((ExperimentInfo) param
        .getSubBlockValue(AbstractExperimentReaderFactory.EXPERIMENT_INFO))
        .getIdent());
  }

  @Override
  public List<IPreferencesPage> getPreferencesPages() {
    List<IPreferencesPage> pages = new ArrayList<>();
    pages.add(new ExperimentPreferences());
    return pages;
  }

  /**
   * Get manager for the current experiment's observers.
   * 
   * @return manager for the current experiment's observers
   */
  public static ExperimentObservationManager getExperimentObservationManager() {
    return observationManager;
  }

  /**
   * Schedule an experiment. If the flag is true the experiment editor shall is
   * shown and that's it. Otherwise, the experiment gets started (without adding
   * an experiment editor).
   * 
   * @param exp
   *          the experiment to be executed
   * @param showExpEditor
   *          if true, the experiment editor is shown
   */
  public static void scheduleExperiment(BaseExperiment exp,
      boolean showExpEditor) {
    if (showExpEditor) {
      WindowManagerManager.getWindowManager().addWindow(
          new ExperimentEditor(exp, null, null));
      return;
    }
    RunExpAction reAction =
        new RunExpAction(exp, WindowManagerManager.getWindowManager(),
            observationManager, new IExperimentExecutionListener[] {}, null);
    reAction.actionPerformed(null);
  }

  /**
   * Configure experiment with default settings for execution via the user
   * interface (such as sim-step delay).
   * 
   * @param experiment
   *          the experiment to be configured
   */
  public static void configureExperimentWithUIDefaults(BaseExperiment experiment) {
    experiment.setStartComputationTasksPaused(true);
    experiment
        .setComputationTaskInterStepDelay(SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS);
  }

}
