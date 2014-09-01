/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Collections;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import org.jamesii.gui.application.IProgressListener;
import org.jamesii.gui.application.james.JamesGUI;
import org.jamesii.gui.application.preferences.AbstractPreferencesPage;
import org.jamesii.gui.application.preferences.Preferences;
import org.jamesii.gui.application.resource.iconset.IIconSet;
import org.jamesii.gui.application.resource.iconset.IconSetManager;
import org.jamesii.gui.perspective.preset.PerspectivePresetComparator;
import org.jamesii.gui.utils.CheckBoxGroup;
import org.jamesii.gui.utils.SimpleFormLayout;
import org.jamesii.gui.utils.SimpleFormLayout.FormConstraint;

/**
 * Represents the preferences page for the {@link DefaultPerspective} presenting
 * the main GUI options.
 * 
 * @author Stefan Rybacki
 * 
 */
class DefaultPreferences extends AbstractPreferencesPage {
  /**
   * Panel that provides GUI elements
   */
  private JPanel optionPanel;

  /**
   * flag indicating whether panel was loaded
   */
  private boolean loaded = false;

  /**
   * list of presets available
   */
  private List<PerspectivePreset> presets;

  /**
   * model representing entire options handled by this preferences page
   */
  private DefaultGUIPreferencesModel model;

  /**
   * The confirm exit check box.
   */
  private JCheckBox confirmExitCheckBox;

  @Override
  protected synchronized JComponent getPreferencesPageContent() {
    loaded = true;
    return new JScrollPane(optionPanel);
  }

  @Override
  public synchronized void applyPreferences(final IProgressListener l) {
    if (loaded) {
      l.taskInfo(this, "Applying Perspective Changes...");

      // get the perspective changes
      List<IPerspective> selectedPerspectives = model.getSelectedPerspectives();
      List<IPerspective> perspectives =
          PerspectivesManager.getAvailablePerspectives();
      for (IPerspective persp : perspectives) {
        if (PerspectivesManager.isOpen(persp)
            && !selectedPerspectives.contains(persp)) {
          PerspectivesManager.closePerspective(persp);
          continue;
        }

        if (!PerspectivesManager.isOpen(persp)
            && (selectedPerspectives.contains(persp) || persp.isMandatory())) {
          PerspectivesManager.openPerspective(persp);
        }
      }

      l.taskInfo(this, "Applying Presets...");

      Preferences.put(JamesGUI.CONFIRM_EXIT, confirmExitCheckBox.isSelected());

      PerspectivePresetManager.setPresets(presets);

      // get the selected icon set (if it is different than the stored icon set
      // show message saying that
      // a restart is required
      IIconSet set = model.getIconSet();
      if (set != null) {
        // TODO sr137: use constant for "org.jamesii.iconset" or move
        // functionality
        // into
        // iconsetmanager
        // String old = Preferences.get("org.jamesii.iconset");
        Preferences.put("org.jamesii.iconset", set.getClass().getName());
        IconSetManager.setDefaultIconSet(set);

        // due to the use of IconProxy and ImageProxy this is not
        // needed anymore?!

        /*
         * if (old == null || !old.equals(set.getClass().getName())) {
         * JOptionPane.showMessageDialog(WindowManagerManager.getWindowManager()
         * .getMainWindow(), "The icon set was changed, to have those " +
         * "changes take affect you need to restart!"); }
         */
      }
    }
  }

  @Override
  public String getLocation() {
    return "GUI Options";
  }

  @Override
  public void init() {
    model = new DefaultGUIPreferencesModel();

    // create content
    optionPanel = new JPanel(new BorderLayout());

    // start perspectives setup

    JPanel vBox = new JPanel(new SimpleFormLayout());
    vBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

    int y = 0;

    vBox.add(new JLabel("Perspectives"), FormConstraint.cellXY(0, y));
    vBox.add(new JSeparator(SwingConstants.HORIZONTAL), FormConstraint.cellXY(
        1, y++, FormConstraint.CENTER, FormConstraint.HORIZONTAL, 3, 1));

    final CheckBoxGroup<IPerspective> group =
        new CheckBoxGroup<>(model.getPerspectives());
    group.setSelectionModel(model.getPerspectives().getSelectionModel());

    // start perspectives preset setup
    presets = PerspectivePresetManager.getAvailablePresets();

    final JComboBox presetCombo = new JComboBox();
    List<IPerspective> selectedPerspectives =
        PerspectivesManager.getOpenPerspectives();

    PerspectivePreset prst =
        new PerspectivePreset("[ Current ]",
            selectedPerspectives.toArray(new IPerspective[selectedPerspectives
                .size()]));
    presetCombo.addItem(prst);

    Collections.sort(presets, new PerspectivePresetComparator());
    for (PerspectivePreset p : presets) {
      presetCombo.addItem(p);
    }

    presetCombo.setSelectedItem(prst);

    final JButton deleteButton = new JButton("Delete...");
    deleteButton.setEnabled(false);
    // attach action listener
    deleteButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // check whether a preset is actually selected in the preset combo
        Object o = presetCombo.getSelectedItem();
        if (o == null || !(o instanceof PerspectivePreset)
            || presetCombo.getSelectedIndex() <= 0) {
          return;
        }

        PerspectivePreset preset = (PerspectivePreset) o;

        // ask for permission to delete the preset
        if (JOptionPane.showConfirmDialog(
            deleteButton,
            String.format("Do you really want to delete \"%s\"?",
                preset.getName())) == JOptionPane.YES_OPTION) {
          presets.remove(preset);
          presetCombo.removeItem(preset);
          // TODO sr137: put all this preset functionality in own model
        }
      }

    });

    presetCombo.addItemListener(new ItemListener() {

      @Override
      public void itemStateChanged(ItemEvent e) {
        // get the selected item of combobox and select perspective comboboxes
        // accordingly
        deleteButton.setEnabled(presetCombo.getSelectedIndex() > 0);

        Object o = presetCombo.getSelectedItem();
        if (o == null || !(o instanceof PerspectivePreset)) {
          return;
        }

        // notify model of preset change
        model.setPreset((PerspectivePreset) o);
      }

    });

    vBox.add(new JLabel("Presets: "),
        FormConstraint.cellXY(0, y, FormConstraint.EAST));
    vBox.add(presetCombo, FormConstraint.cellXY(1, y, FormConstraint.WEST,
        FormConstraint.HORIZONTAL));

    final JButton saveButton = new JButton("Save...");
    // attach listener
    saveButton.addActionListener(new ActionListener() {

      @Override
      public void actionPerformed(ActionEvent e) {
        // ask for a name for the preset
        String name =
            JOptionPane.showInputDialog(saveButton,
                "Please enter a name for the preset!");

        if (name == null) {
          return;
        }

        // create preset and add it to the combo box as well as the presets list
        PerspectivePreset preset =
            new PerspectivePreset(name, model.getSelectedPerspectives()
                .toArray(
                    new IPerspective[model.getSelectedPerspectives().size()]));

        presets.add(preset);
        presetCombo.addItem(preset);
        presetCombo.setSelectedItem(preset);
      }

    });

    vBox.add(deleteButton, FormConstraint.cellXY(2, y++));
    vBox.add(saveButton, FormConstraint.cellXY(2, y, FormConstraint.NORTH_WEST,
        FormConstraint.HORIZONTAL, 1, 2));

    // model.setSelectedPerspectives(PerspectivesManager.getOpenPerspectives());

    vBox.add(group, FormConstraint.cellXY(1, y++));

    vBox.add(Box.createRigidArea(new Dimension(15, 15)),
        FormConstraint.cellXY(0, y++));

    // start icon sets setup
    vBox.add(new JLabel("Icon Set"), FormConstraint.cellXY(0, y));
    vBox.add(new JSeparator(SwingConstants.HORIZONTAL), FormConstraint.cellXY(
        1, y++, FormConstraint.CENTER, FormConstraint.HORIZONTAL, 3, 1));

    JComboBox isCombo = new JComboBox(model.getIconSets());
    model.setIconSet(IconSetManager.getIconSet());

    isCombo.setRenderer(IconSetListCellRenderer.getInstance());

    vBox.add(isCombo, FormConstraint.cellXY(1, y++, FormConstraint.WEST,
        FormConstraint.HORIZONTAL));

    // start shutdown options
    vBox.add(new JLabel("Shutdown Options"), FormConstraint.cellXY(0, y));
    vBox.add(new JSeparator(SwingConstants.HORIZONTAL), FormConstraint.cellXY(
        1, y++, FormConstraint.CENTER, FormConstraint.HORIZONTAL, 3, 1));

    confirmExitCheckBox = new JCheckBox("Confirm when exiting James II");

    Boolean b = Preferences.get(JamesGUI.CONFIRM_EXIT);
    if (b == null) {
      b = Boolean.TRUE;
    }

    confirmExitCheckBox.setSelected(b);

    vBox.add(confirmExitCheckBox, FormConstraint.cellXY(1, y++,
        FormConstraint.WEST, FormConstraint.HORIZONTAL));

    optionPanel.add(vBox, BorderLayout.CENTER);
  }

  @Override
  public String getTitle() {
    return "GUI Options";
  }

}