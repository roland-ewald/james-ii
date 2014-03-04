/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.actions;


import java.awt.event.ActionEvent;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.action.DefaultSwingAction;
import org.jamesii.gui.application.resource.IconManager;
import org.jamesii.gui.application.resource.iconset.IconIdentifier;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.simspex.gui.SimSpExPerspective;


/**
 * Action to clear the current {@link IPerformanceDatabase}.
 * 
 * @author Roland Ewald
 * 
 */
public class ClearPerfDBAction extends DefaultSwingAction {

  /** Serialisation ID. */
  private static final long serialVersionUID = -6249855664551204437L;

  /**
   * Instantiates a new 'clear performance database' action.
   */
  public ClearPerfDBAction() {
    super("Clear database");
    putValue(SMALL_ICON,
        IconManager.getIcon(IconIdentifier.DELETE_SMALL, "Clear database"));
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    if (JOptionPane
        .showConfirmDialog(
            null,
            "Clear database?",
            "Are you sure that you want to clear the performance database? All data will be LOST!",
            JOptionPane.YES_NO_OPTION) != JOptionPane.YES_OPTION) {
      return;
    }

    try {
      IPerformanceDatabase perfDB = SimSpExPerspective.getPerformanceDataBase();
      perfDB.clear();
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, "Error while clearing the database", ex);
    }

    SimSystem.report(Level.INFO, "The performance database has been cleared.");
  }
}
