/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective.laf;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;

import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.preferences.AbstractPreferencesPage;
import org.jamesii.gui.application.task.AbstractTask;
import org.jamesii.gui.application.task.ITask;
import org.jamesii.gui.application.task.TaskManager;
import org.jamesii.gui.perspective.LookAndFeelComboBoxModel;
import org.jamesii.gui.perspective.LookAndFeelListCellRenderer;
import org.jamesii.gui.utils.BasicUtilities;
import org.jamesii.gui.utils.SimpleFormLayout;
import org.jamesii.gui.utils.SimpleFormLayout.FormConstraint;

/**
 * Preferences page for setting and changing the look and feel of the JAMES II
 * GUI.
 * 
 * @author Stefan Rybacki
 * 
 */
class LafPreferences extends AbstractPreferencesPage {
  /**
   * Model containing the available look and feels
   */
  private final LookAndFeelComboBoxModel lafComboModel =
      new LookAndFeelComboBoxModel();

  /**
   * panel used to preview look and feels
   */
  private LafPreviewPanel previewPanel;

  @Override
  protected JComponent getPreferencesPageContent() {
    JPanel vBox = new JPanel(new SimpleFormLayout());

    JComboBox lfCombo = new JComboBox(lafComboModel);
    lfCombo.setRenderer(LookAndFeelListCellRenderer.getInstance());

    previewPanel = new LafPreviewPanel();
    previewPanel.setBorder(BorderFactory.createEtchedBorder());

    int y = 0;

    vBox.add(new JLabel("Look & Feel"),
        FormConstraint.cellXY(0, y, FormConstraint.WEST));
    vBox.add(new JSeparator(SwingConstants.HORIZONTAL), FormConstraint.cellXY(
        1, y++, FormConstraint.CENTER, FormConstraint.HORIZONTAL, 3, 1));

    vBox.add(new JLabel("choose: "),
        FormConstraint.cellXY(0, y, FormConstraint.EAST));
    vBox.add(lfCombo, FormConstraint.cellXY(1, y++, FormConstraint.WEST,
        FormConstraint.HORIZONTAL));

    vBox.add(Box.createRigidArea(new Dimension(15, 15)),
        FormConstraint.cellXY(0, y++));

    vBox.add(new JLabel("Preview"),
        FormConstraint.cellXY(0, y, FormConstraint.WEST));
    vBox.add(new JSeparator(SwingConstants.HORIZONTAL), FormConstraint.cellXY(
        1, y++, FormConstraint.CENTER, FormConstraint.HORIZONTAL, 3, 1));

    vBox.add(previewPanel, FormConstraint.cellXY(1, y++, FormConstraint.WEST,
        FormConstraint.HORIZONTAL, 3, 1));

    return new JScrollPane(vBox);
  }

  @Override
  public void applyPreferences(final IProgressListener l) {
    LookAndFeelInfo laf = lafComboModel.getSelectedLookAndFeelInfo();

    l.taskInfo(this, "Applying Look and Feel...");

    LafManager.setActiveLookAndFeel(laf);
  }

  @Override
  public String getLocation() {
    return "GUI Options";
  }

  @Override
  public String getTitle() {
    return "Look And Feel";
  }

  @Override
  public void init() {
    lafComboModel.setSelectedLookAndFeel(UIManager.getLookAndFeel());
    lafComboModel.addPropertyChangeListener(new PropertyChangeListener() {

      @Override
      public void propertyChange(PropertyChangeEvent evt) {
        final String laf =
            lafComboModel.getSelectedLookAndFeelInfo().getClassName();
        ITask task = new AbstractTask("Preview Look and Feel") {

          @Override
          protected void task() {
            BasicUtilities.invokeLaterOnEDT(new Runnable() {

              @Override
              public void run() {
                previewPanel.setLookAndFeel(laf);
              }

            });
          }

          @Override
          protected void cancelTask() {
          }

        };

        TaskManager.addTask(task);
      }
    });
  }

}
