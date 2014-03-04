/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui;


import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.gui.application.action.ActionIAction;
import org.jamesii.gui.application.action.ActionSet;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.application.action.SeparatorAction;
import org.jamesii.gui.perspective.AbstractPerspective;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.plugintype.AbstractPerfDBFactory;
import org.jamesii.simspex.gui.actions.ClearPerfDBAction;
import org.jamesii.simspex.gui.actions.SimSpExExperiment;
import org.jamesii.simspex.gui.dialogs.FeatureExtractionDialog;


/**
 * Perspective for simulation space exploration.
 * 
 * @author Roland Ewald
 * 
 */
public class SimSpExPerspective extends AbstractPerspective {

  /** The performance database (singleton). */
  private static IPerformanceDatabase performanceDatabase = null;

  /** The performance data recorder (singleton). */
  private static PerfDBRecorder perfDBRecorder = null;

  /** The location of the main menu for the actions. */
  private static final String MENU_MAIN_SIMSPEX = "org.jamesii.menu.main/org.jamesii.simspex";

  /** The connection data for the performance database. */
  private static DBConnectionData dbConnectionData = new DBConnectionData(
      "jdbc:hsqldb:file:j2_perf_sel_db", "sa", "", "org.hsqldb.jdbcDriver");

  @Override
  protected List<IAction> generateActions() {

    List<IAction> actions = new ArrayList<>();
    actions.add(new ActionSet("org.jamesii.simspex", "SimSpace Exploration",
        "org.jamesii.menu.main?after=edit", null));

    actions.add(new SimSpExExperiment(getWindowManager(), null));

    actions.add(SeparatorAction.getSeparatorFor(
        "org.jamesii.menu.main/org.jamesii.simspex?after=org.jamesii.simspex.simspaceexp", null));
    actions.add(new ActionIAction(new ConfigurePerfDBAction(
        "Configure Performance DB..."), "org.jamesii.simspex.perfdb",
        new String[] { MENU_MAIN_SIMSPEX }, null));
    actions.add(SeparatorAction.getSeparatorFor(
        "org.jamesii.menu.main/org.jamesii.simspex?after=org.jamesii.simspex.perfdb", null));
    actions.add(new ActionIAction(new ConfigureSPDMAction("Choose ML..."),
        "org.jamesii.simspex.chooseml", new String[] { MENU_MAIN_SIMSPEX }, null));
    actions.add(new ActionIAction(new javax.swing.AbstractAction(
        "Extract Features...") {
      private static final long serialVersionUID = 1L;

      @Override
      public void actionPerformed(ActionEvent e) {
        getWindowManager().addWindow(new FeatureExtractionDialog());
      }
    }, "org.jamesii.simspex.extrfeatures", new String[] { MENU_MAIN_SIMSPEX }, null));
    actions.add(new ActionIAction(new ClearPerfDBAction(), "org.jamesii.simspex.cleardb",
        new String[] { "org.jamesii.toolbar.main?after=org.jamesii.simspex.startrec",
            "org.jamesii.menu.main/org.jamesii.simspex?after=org.jamesii.simspex.startrec" }, null));

    // TODO: return whole actions list to 're-activate' the UI
    return actions.subList(0, 1);
  }

  @Override
  public String getDescription() {
    return "Perspective for Simulation Space Exploration";
  }

  @Override
  public String getName() {
    return "SimSpEx Perspective";
  }

  /**
   * Gets the performance data base.
   * 
   * @return the performance data base
   */
  public static synchronized IPerformanceDatabase getPerformanceDataBase() {
    if (performanceDatabase == null) {
      performanceDatabase =
          AbstractPerfDBFactory
              .createDefaultPerformanceDatabase(dbConnectionData);
    }
    return performanceDatabase;
  }

  @Override
  public void perspectiveClosed() {
    super.perspectiveClosed();
    resetPerformanceDatabase();
  }

  /**
   * Resets performance database.
   */
  public static synchronized void resetPerformanceDatabase() {
    if (performanceDatabase != null) {
      try {
        performanceDatabase.close();
        performanceDatabase = null;
      } catch (Exception ex) {
        SimSystem.report(Level.SEVERE,
            "Error while closing performance database.", ex);
      }
    }
  }

  /**
   * Gets the perf db recorder.
   * 
   * @return the perf db recorder
   */
  public static synchronized PerfDBRecorder getPerfDBRecorder() {
    if (perfDBRecorder == null) {
      perfDBRecorder = new PerfDBRecorder();
    }
    return perfDBRecorder;
  }

  /**
   * Sets the db connection data.
   * 
   * @param dbConnectionData
   *          the dbConnectionData to set
   */
  public static void setDbConnectionData(DBConnectionData dbConnectionData) {
    SimSpExPerspective.dbConnectionData = dbConnectionData;
  }

  /**
   * The action to configure the SPDM.
   */
  private static final class ConfigureSPDMAction extends
      javax.swing.AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 772461159323722928L;

    private ConfigureSPDMAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // TODO(re027): add GUI for SPDM framework
    }
  }

  /**
   * The action to configure the performance database.
   */
  private static final class ConfigurePerfDBAction extends
      javax.swing.AbstractAction {

    /** The Constant serialVersionUID. */
    private static final long serialVersionUID = 772461159323722928L;

    private ConfigurePerfDBAction(String name) {
      super(name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
      // TODO(re027): add GUI to configure perfDB recorder
      getPerfDBRecorder().getSelectionHook().getIgnoreList();
    }
  }

}
