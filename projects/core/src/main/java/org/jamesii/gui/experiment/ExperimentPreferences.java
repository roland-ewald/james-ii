/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.experiment;

import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.jamesii.SimSystem;
import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.preferences.AbstractPreferencesPage;
import org.jamesii.gui.application.preferences.Preferences;
import org.jamesii.gui.experiment.actions.SliderSimAction;
import org.jamesii.gui.utils.SimpleFormLayout;
import org.jamesii.gui.utils.SimpleFormLayout.FormConstraint;

/**
 * Preferences of the experimentation perspective.
 * 
 * @author Roland Ewald
 */
public class ExperimentPreferences extends AbstractPreferencesPage {

  private static final String SIMSTEP_DELAY =
      "experiment.perspective.simstepDelay";

  /** Content panel. */
  private JPanel content;

  /** Field to edit maximal parallel experiment executions. */
  private JTextField maxParExecs = new JTextField(5);

  /** Field to edit keep-alive time of waiting threads. */
  private JTextField keepAliveTime = new JTextField(5);

  /** Field to edit keep-alive time of waiting threads. */
  private JTextField adjustDelay = new JTextField(5);

  @Override
  protected JComponent getPreferencesPageContent() {
    return content;
  }

  @Override
  public void applyPreferences(IProgressListener l) {
    try {
      SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS =
          Long.parseLong(adjustDelay.getText());
      Preferences.put(SIMSTEP_DELAY, SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS);
    } catch (NumberFormatException e) {
    }
  }

  @Override
  public boolean isValid() {
    try {
      Long.parseLong(adjustDelay.getText());
    } catch (NumberFormatException e) {
      return false;
    }

    return true;
  }

  @Override
  public void closed() {
    // TODO Auto-generated method stub
  }

  @Override
  public String getLocation() {
    return "Experiments";
  }

  @Override
  public String getTitle() {
    return "General";
  }

  @Override
  public void init() {
    content = new JPanel(new SimpleFormLayout(10, 10));
    int rowCount = 0;
    content.add(new JLabel("Experiment Executor"),
        FormConstraint.cellXY(0, rowCount, FormConstraint.WEST));
    content.add(new JSeparator(SwingConstants.HORIZONTAL),
        FormConstraint.cellXY(1, rowCount++, FormConstraint.CENTER,
            FormConstraint.HORIZONTAL));
    content.add(new JLabel("Max. parallel experiment executions: "),
        FormConstraint.cellXY(0, rowCount, FormConstraint.EAST));
    content.add(maxParExecs, FormConstraint.cellXY(1, rowCount++));
    content.add(new JLabel("Keep-alive  time (in s): "),
        FormConstraint.cellXY(0, rowCount, FormConstraint.EAST));
    content.add(keepAliveTime, FormConstraint.cellXY(1, rowCount++));

    content.add(new JLabel("Default Delay between Simulation Steps in ms: "),
        FormConstraint.cellXY(0, rowCount, FormConstraint.EAST));
    content.add(adjustDelay, FormConstraint.cellXY(1, rowCount++));

    try {
      Long delay = Preferences.get(SIMSTEP_DELAY);
      if (delay != null) {
        SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS = delay;
      }
    } catch (ClassCastException e) {
      SimSystem.report(Level.WARNING, "Delay not set from preferences", e);
    }

    adjustDelay.setText("" + SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS);
    fireValidStateChange();
  }

  @Override
  public void restoreDefaults() {
    maxParExecs.setText("" + ExperimentPerspective.DEFAULT_MAX_POOL_SIZE);
    keepAliveTime.setText("" + ExperimentPerspective.DEFAULT_KEEP_ALIVE);
    adjustDelay.setText("" + SliderSimAction.DEFAULT_SIMSTEP_DELAY_MS);
  }

}
