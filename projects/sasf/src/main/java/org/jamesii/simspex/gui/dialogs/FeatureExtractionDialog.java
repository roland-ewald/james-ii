/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.gui.dialogs;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.logging.Level;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.AbstractWindow;
import org.jamesii.gui.application.Contribution;
import org.jamesii.gui.application.action.IAction;
import org.jamesii.gui.syntaxeditor.JamesUndoManager;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.recording.features.FeatureExtraction;
import org.jamesii.simspex.gui.SimSpExPerspective;


/**
 * Dialog to configure feature extraction.
 * 
 * TODO: Add means to configure the actual feature extraction
 * 
 * @author Roland Ewald
 * 
 */
public class FeatureExtractionDialog extends AbstractWindow {

  /** Label to show status of extraction. */
  private JLabel extractionStatus = new JLabel("Initialized.");

  /**
   * Button to trigger feature extraction.
   */
  private JButton startExtractionButton = new JButton("Start");
  {
    startExtractionButton.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        startExtraction();
      }
    });
  }

  /** The content panel. */
  private JPanel content = new JPanel();

  /**
   * Instantiates a new feature extraction dialog.
   */
  public FeatureExtractionDialog() {
    super("Configure feature extraction", null, Contribution.DIALOG);
    content.add(extractionStatus);
    content.add(startExtractionButton);
  }

  /**
   * Start extraction.
   */
  protected void startExtraction() {
    SimSystem.report(Level.INFO, "Starting extraction...");
    try {
      IPerformanceDatabase perfDB = SimSpExPerspective.getPerformanceDataBase();
      perfDB.open();
      FeatureExtraction fExtraction = new FeatureExtraction(perfDB);
      FeatureExtraction.refreshFeatureExtractors(perfDB);
      fExtraction.extractFeatures();
    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE, null, ex.getMessage(), null, ex);
    }
    SimSystem.report(Level.INFO, "Finished feature extraction.");
  }

  @Override
  protected IAction[] generateActions() {
    return null;
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
}
