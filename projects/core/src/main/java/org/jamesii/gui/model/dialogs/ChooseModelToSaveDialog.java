/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.model.dialogs;

import java.awt.BorderLayout;
import java.awt.Checkbox;
import java.awt.FlowLayout;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jamesii.core.model.IModel;
import org.jamesii.gui.application.ApplicationDialog;

/**
 * Dialog to let the user select which model to save.
 * 
 * @author Gabriel Blum
 */
public class ChooseModelToSaveDialog extends ApplicationDialog {

  private static final int DEFAULT_SIZE = 400;

  /** Serialisation ID. */
  private static final long serialVersionUID = -8080798326536505627L;

  /** Storage for models that ought to be saved. */
  private List<IModel> results = new ArrayList<>();

  /** List of available models. */
  private List<IModel> availModels = null;

  /** Maps each model to the checkbox for deciding whether it should be saved. */
  private Map<IModel, Checkbox> modelCheckMap = new HashMap<>();

  /** Maps each model to a flag determining whether it shall be saved. */
  private Map<IModel, Boolean> modelSaveMap = new HashMap<>();

  /** Panel to hold the model that can be selected. */
  private JPanel selectionPanel = new JPanel();
  {
    selectionPanel.setLayout(new BoxLayout(selectionPanel, BoxLayout.Y_AXIS));
  }

  /** Button to cancel saving. */
  private JButton cancel = new JButton("Cancel");
  {
    cancel.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        dispose();
      }
    });
  }

  /** Button to proceed with saving. */
  private JButton save = new JButton("Save");
  {
    save.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        retrieveResults();
        setVisible(false);
      }
    });
  }

  /** Select all models. */
  private JButton selectAll = new JButton("Select All");
  {
    selectAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (IModel key : modelCheckMap.keySet()) {
          Checkbox tempBox = modelCheckMap.get(key);
          tempBox.setState(true);
          modelCheckMap.put(key, tempBox);

        }
      }
    });
  }

  /** De-select all models. */
  private JButton deselectAll = new JButton("Deselect All");
  {
    deselectAll.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        for (IModel key : modelCheckMap.keySet()) {
          Checkbox tempBox = modelCheckMap.get(key);
          tempBox.setState(false);
          modelCheckMap.put(key, tempBox);

        }
      }
    });
  }

  /**
   * Default constructor.
   * 
   * @param owner
   *          owner of the dialog
   * @param availableModels
   *          list of models that are currently being edited
   */
  public ChooseModelToSaveDialog(Window owner, List<IModel> availableModels) {
    super(owner);
    availModels = availableModels;

    for (IModel mw : availModels) {
      Checkbox newBox = new Checkbox(mw.getName(), null, true);
      modelCheckMap.put(mw, newBox);
      modelSaveMap.put(mw, true);
      selectionPanel.add(newBox);
    }

    setModal(true);
    setSize(DEFAULT_SIZE, DEFAULT_SIZE);
    setTitle("Choose which model to save");
    getContentPane().setLayout(new BorderLayout());

    JPanel buttonPanel = new JPanel();
    buttonPanel.setLayout(new FlowLayout());
    buttonPanel.add(cancel);
    buttonPanel.add(selectAll);
    buttonPanel.add(deselectAll);
    buttonPanel.add(save);
    getContentPane().add(buttonPanel, BorderLayout.SOUTH);

    getContentPane().add(new JScrollPane(selectionPanel), BorderLayout.CENTER);
    getContentPane().add(new JLabel("Currently open Model windows:"),
        BorderLayout.NORTH);

    setLocationRelativeTo(null);
  }

  /**
   * Tests which models have been checked by the user, writes results to
   * {@link ChooseModelToSaveDialog#results}.
   */
  private void retrieveResults() {
    for (IModel key : modelCheckMap.keySet()) {
      Checkbox tempBox = modelCheckMap.get(key);
      modelSaveMap.put(key, tempBox.getState());
    }

    Iterator<IModel> mwi = modelSaveMap.keySet().iterator();
    int i = 0;
    while (mwi.hasNext()) {
      IModel key = mwi.next();
      if (modelSaveMap.get(key)) {
        results.add(i, key);
        i++;
      }
    }
  }

  /**
   * Gets the selected windows.
   * 
   * @return the selected windows
   */
  public List<IModel> getSelectedWindows() {
    return results;
  }

}
