/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jamesii.gui.base.IPropertyChangeSupport;

/**
 * This component shows a check box for each added object and returns a list of
 * selected objects according to the selection state of the check boxes.
 * 
 * @author Stefan Rybacki
 * @param <T>
 *          type parameter specifying the type of the items of the
 *          {@link CheckBoxGroup}
 * 
 */
public class CheckBoxGroup<T> extends JComponent implements
    IPropertyChangeSupport, ListDataListener, ActionListener,
    ListSelectionListener {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 1809735033191384595L;

  /**
   * Model representing the items displayed in check box group
   */
  private ICheckBoxGroupModel<T> model;

  /**
   * Stores the generated check boxes
   */
  private final List<JCheckBox> boxes = new ArrayList<>();

  /**
   * Stores the generated swing components associated to each box
   */
  private final List<JComponent> boxComponents = new ArrayList<>();

  /**
   * represents the selection model (selection model also defines the selection
   * mode)
   */
  private ListSelectionModel selectionModel;

  /**
   * Creates a check box group
   */
  public CheckBoxGroup() {
    this(new DefaultCheckBoxGroupModel<T>());
  }

  /**
   * Create a CheckBoxGroup with the given model
   * 
   * @param model
   *          the model to apply
   */
  public CheckBoxGroup(ICheckBoxGroupModel<T> model) {
    this(model, new DefaultListSelectionModel());
  }

  /**
   * Creates a CheckBoxGroup with the given model using the given selection
   * model
   * 
   * @param model
   *          the model to apply
   * @param selectionModel
   *          the selection model to use
   */
  public CheckBoxGroup(ICheckBoxGroupModel<T> model,
      ListSelectionModel selectionModel) {
    super();
    setSelectionModel(selectionModel);
    setModel(model);
    setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
  }

  @Override
  protected void addImpl(Component comp, Object constraints, int index) {
    throw new UnsupportedOperationException("Use model to add components");
  }

  /**
   * Sets the model that provides the items for this check box group
   * 
   * @param model
   *          the model to set
   */
  public void setModel(ICheckBoxGroupModel<T> model) {
    if (this.model != null) {
      this.model.removeListDataListener(this);
    }
    Object old = this.model;
    this.model = model;
    model.addListDataListener(this);
    // call contents Changed
    contentsChanged(null);
    firePropertyChange("model", old, model);
  }

  /**
   * @return the model used by check box group
   */
  public ICheckBoxGroupModel<T> getModel() {
    return model;
  }

  @Override
  public void contentsChanged(ListDataEvent e) {
    for (JCheckBox b : boxes) {
      b.removeActionListener(this);
    }

    if (selectionModel != null) {
      selectionModel.clearSelection();
    }
    boxes.clear();
    boxComponents.clear();
    removeAll();
    // add new checkboxes
    for (int i = 0; i < model.getSize(); i++) {
      JCheckBox box = new JCheckBox();
      JComponent component = model.getComponentAt(i);
      box.setEnabled(model.isEditable(i));
      box.addActionListener(this);
      boxes.add(box);
      boxComponents.add(component);

      Box hBox = Box.createHorizontalBox();
      hBox.add(box);
      hBox.add(component);
      hBox.add(Box.createHorizontalGlue());

      super.addImpl(hBox, null, getComponentCount());
    }
  }

  @Override
  public void intervalAdded(final ListDataEvent e) {
    int from = e.getIndex0();
    int to = e.getIndex1();
    for (int i = from; i <= to; i++) {
      JCheckBox box = new JCheckBox();
      JComponent component = model.getComponentAt(i);
      box.setEnabled(model.isEditable(i));
      box.addActionListener(CheckBoxGroup.this);
      boxes.add(i, box);
      boxComponents.add(component);

      Box hBox = Box.createHorizontalBox();
      hBox.add(box);
      hBox.add(component);
      hBox.add(Box.createHorizontalGlue());

      CheckBoxGroup.super.addImpl(hBox, null, i);
    }
    selectionModel.insertIndexInterval(from, to - from + 1, true);
    selectionModel.removeSelectionInterval(from, to);

    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        revalidate();
        repaint();
      }
    });
  }

  @Override
  public void intervalRemoved(final ListDataEvent e) {
    int from = e.getIndex0();
    int to = e.getIndex1();
    for (int i = to; i >= from; i--) {
      remove(i);
      boxes.get(i).removeActionListener(CheckBoxGroup.this);
      boxes.remove(i);
      boxComponents.remove(i);
    }
    selectionModel.removeIndexInterval(from, to);

    BasicUtilities.invokeLaterOnEDT(new Runnable() {

      @Override
      public void run() {
        revalidate();
        repaint();
      }
    });
  }

  @Override
  public synchronized void actionPerformed(ActionEvent e) {
    if (boxes.contains(e.getSource())) {
      selectionModel.setValueIsAdjusting(true);
      try {
        int i = boxes.indexOf(e.getSource());
        if (boxes.get(i).isSelected()) {
          selectionModel.addSelectionInterval(i, i);
          boxComponents.get(i).setEnabled(true);
        } else {
          selectionModel.removeSelectionInterval(i, i);
          boxComponents.get(i).setEnabled(false);
        }
      } finally {
        selectionModel.setValueIsAdjusting(false);
      }
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    // update selection in checkboxes
    for (int i = e.getFirstIndex(); i <= e.getLastIndex(); i++) {
      if (i < boxes.size()) {
        boxes.get(i).setSelected(selectionModel.isSelectedIndex(i));
      }
    }
  }

  /**
   * Convinience method to provide the selected items.
   * 
   * @return a list of selected items
   */
  public List<T> getSelectedItems() {
    List<T> out = new ArrayList<>();

    for (int i = 0; i < model.getSize(); i++) {
      if (selectionModel.isSelectedIndex(i)) {
        out.add(model.getItemAt(i));
      }
    }

    return out;
  }

  /**
   * @return the selection model
   */
  public ListSelectionModel getSelectionModel() {
    return selectionModel;
  }

  /**
   * Sets the selection model to use for selection
   * 
   * @param selectionModel
   *          the selection model to use
   */
  public void setSelectionModel(ListSelectionModel selectionModel) {
    if (selectionModel == null) {
      return;
    }
    if (this.selectionModel != null) {
      this.selectionModel.removeListSelectionListener(this);
    }
    Object old = this.selectionModel;
    this.selectionModel = selectionModel;
    this.selectionModel.addListSelectionListener(this);
    valueChanged(new ListSelectionEvent(selectionModel, 0, boxes.size(), false));
    firePropertyChange("selectionModel", old, selectionModel);
  }

}
