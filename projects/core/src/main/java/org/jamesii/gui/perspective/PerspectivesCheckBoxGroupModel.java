/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.perspective;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.ListSelectionModel;

import org.jamesii.gui.utils.DefaultCheckBoxGroupModel;
import org.jamesii.gui.utils.ICheckBoxGroupModel;

/**
 * Provides all available perspectives wrapped in a {@link ICheckBoxGroupModel}.
 * It also provides a selection model that should be used to have
 * {@link #getSelectedPerspectives()} and {@link #setSelectedPerspectives(List)}
 * working properly.
 * 
 * @author Stefan Rybacki
 * 
 */
final class PerspectivesCheckBoxGroupModel extends
    DefaultCheckBoxGroupModel<IPerspective> {
  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 8322438071288056904L;

  /**
   * Cached list of available perspectives
   */
  private final transient List<IPerspective> availablePerspectives =
      PerspectivesManager.getAvailablePerspectives();

  /**
   * selection model supposed to be used in GUI elements
   */
  private final DefaultListSelectionModel selectionModel =
      new DefaultListSelectionModel();

  /**
   * Creates an instance of this model
   */
  public PerspectivesCheckBoxGroupModel() {
    super();
    setItemsImpl(availablePerspectives);
    for (int i = 0; i < availablePerspectives.size(); i++) {
      if (PerspectivesManager.isOpen(availablePerspectives.get(i))) {
        selectionModel.addSelectionInterval(i, i);
      }
    }
  }

  @Override
  public void setItems(List<IPerspective> data) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public void addItem(IPerspective item) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public void addItem(int index, IPerspective item) {
    throw new UnsupportedOperationException(
        "Items are automatically set via PerspectivesManager");
  }

  @Override
  public JComponent getComponentAt(int index) {
    return new JLabel(availablePerspectives.get(index).getName());
  }

  @Override
  public boolean isEditable(int index) {
    return !availablePerspectives.get(index).isMandatory();
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
  public void setSelectedPerspectives(List<IPerspective> ps) {
    selectionModel.clearSelection();
    selectionModel.setValueIsAdjusting(true);
    if (ps == null) {
      return;
    }
    for (IPerspective p : ps) {
      int i = availablePerspectives.indexOf(p);
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
  public List<IPerspective> getSelectedPerspectives() {
    List<IPerspective> out = new ArrayList<>();
    for (int i = 0; i < availablePerspectives.size(); i++) {
      if (selectionModel.isSelectedIndex(i)) {
        out.add(availablePerspectives.get(i));
      }
    }

    return out;
  }
}
