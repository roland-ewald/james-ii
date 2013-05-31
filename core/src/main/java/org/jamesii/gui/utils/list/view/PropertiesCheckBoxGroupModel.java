/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import org.jamesii.gui.utils.DefaultCheckBoxGroupModel;
import org.jamesii.gui.utils.list.view.item.IProperty;

/**
 * 
 * @author Jan Himmelspach
 * 
 */
public class PropertiesCheckBoxGroupModel extends
    DefaultCheckBoxGroupModel<IProperty<?>> {

  private static final long serialVersionUID = 6621195985136936442L;

  /**
   * Cached list of available perspectives
   */
  private transient List<IProperty<?>> properties;

  /**
   * selection model supposed to be used in GUI elements
   */
  private final DefaultListSelectionModel selectionModel =
      new DefaultListSelectionModel();

  /**
   * Creates an instance of this model
   */
  public PropertiesCheckBoxGroupModel(List<IProperty<?>> newProperties) {
    super();
    properties = newProperties;
    setItemsImpl(newProperties);
  }

  @Override
  public void setItems(List<IProperty<?>> data) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public void addItem(IProperty<?> item) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public void addItem(int index, IProperty<?> item) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public JComponent getComponentAt(int index) {
    return new JLabel(properties.get(index).getName());
  }

  @Override
  public boolean isEditable(int index) {
    return true;// !properties.get(index).isMandatory();
  }

  /**
   * @return the selection model
   */
  public ListSelectionModel getSelectionModel() {
    return selectionModel;
  }

  /**
   * Sets the selected perspectives according to given list. This only works
   * properly if the integrated selection model is used.
   * 
   * @see #getSelectionModel()
   * @param ps
   *          list of perspectives
   */
  public void setSelectedProperties(List<IProperty<?>> ps) {
    selectionModel.clearSelection();
    selectionModel.setValueIsAdjusting(true);
    if (ps == null) {
      return;
    }
    for (IProperty<?> p : ps) {
      int i = properties.indexOf(p);
      if (i >= 0) {
        selectionModel.addSelectionInterval(i, i);
      }
    }
    selectionModel.setValueIsAdjusting(false);
  }

  /**
   * @return list of selected perspectives (only works if the integrated
   *         selection model is used)
   * @see #getSelectionModel()
   */
  public List<IProperty<?>> getSelectedProperties() {
    List<IProperty<?>> out = new ArrayList<>();
    for (int i = 0; i < properties.size(); i++) {
      if (selectionModel.isSelectedIndex(i)) {
        out.add(properties.get(i));
      }
    }

    return out;
  }

}
