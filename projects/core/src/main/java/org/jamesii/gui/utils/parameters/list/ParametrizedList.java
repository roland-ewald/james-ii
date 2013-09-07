/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.parameters.list;

import javax.swing.JList;

import org.jamesii.core.parameters.ParameterBlock;

/**
 * A {@link JList} that stores factories and associated parameter blocks. It
 * also uses a special ListCellRenderer by default which illustrates the chosen
 * parameters.
 * 
 * @author Johannes Rössel
 */
public class ParametrizedList extends JList {

  /** Serialisation ID. */
  private static final long serialVersionUID = 7878667843990773548L;

  /**
   * Initialises a new instance of the {@link ParametrizedList} class using the
   * given {@link javax.swing.ListModel}.
   * 
   * @param model
   *          The {@link javax.swing.ListModel} to use.
   */
  public ParametrizedList(ConfigurationListModel model) {
    this.setCellRenderer(new ParameterListCellRenderer());
    this.setModel(model);
  }

  /**
   * Convenience method to get the current list model as an instance of
   * {@link ConfigurationListModel}.
   * 
   * @return The current list model as {@link ConfigurationListModel}.
   */
  public ConfigurationListModel getListModel() {
    return (ConfigurationListModel) getModel();
  }

  /**
   * Returns the selected element. This is essentially the same as
   * {@link #getSelectedValue()}, just with an extra cast.
   * 
   * @return The currently selected item as a pair of {@link String} and
   *         {@link ParameterBlock}.
   */
  public Entry getSelectedElement() {
    if (getSelectedIndex() == -1) {
      return null;
    }

    return (Entry) getModel().getElementAt(getSelectedIndex());
  }

  /**
   * Adds an element to the list.
   * 
   * @param value
   *          The factory class name.
   * @param parameters
   *          The ParameterBlock.
   */
  public void addElement(String value, ParameterBlock parameters) {
    getListModel().addElement(value, parameters);
  }

  /**
   * Removes an element from the list.
   * 
   * @param index
   *          The index of the element to remove.
   */
  public void removeElement(int index) {
    getListModel().removeElement(index);
  }

  /**
   * Edits an element in the list. This will set a new factory name and a new
   * parameter block for the item at the specified index.
   * 
   * @param index
   *          The index of the element to edit.
   * @param newFactoryName
   *          The new factory name.
   * @param newParameters
   *          The new parameter block.
   */
  public void editElement(int index, String newFactoryName,
      ParameterBlock newParameters) {
    getListModel().editElement(index, newFactoryName, newParameters);
  }

  /**
   * Moved the selected entries one up (if possible). Updates the selection and
   * the visual output.
   */
  public void moveSelectedEntriesUp() {
    // move selected entries up

    int[] selection = getSelectedIndices();
    for (int i = 0; i < selection.length; i++) {
      int sel = selection[i];

      if (sel > 0) {

        selection[i]--;

        getListModel().swap(sel - 1, sel);
      }

    }

    setSelectedIndices(selection);
    if (selection.length > 0) {
      ensureIndexIsVisible(selection[0]);
    }
    // enforce an update of the view on the items - needed as not all items will
    // require the same amount of space
    setCellRenderer(new ParameterListCellRenderer());

  }

  /**
   * Moved the selected entries one down (if possible). Updates the selection
   * and the visual output.
   */
  public void moveSelectedEntriesDown() {
    // move selected entries down

    int[] selection = getSelectedIndices();
    for (int i = selection.length - 1; i >= 0; i--) {
      int sel = selection[i];
      if (sel < getListModel().getSize() - 1) {

        getListModel().swap(sel, sel + 1);

        selection[i]++;

      }

    }

    setSelectedIndices(selection);
    if (selection.length > 0) {
      ensureIndexIsVisible(selection[selection.length - 1]);
    }
    // enforce an update of the view on the items - needed as not all items will
    // require the same amount of space
    setCellRenderer(new ParameterListCellRenderer());
  }

  /**
   * Delete all selected entries.
   */
  public void deleteSelectedEntries() {
    int[] selection = getSelectedIndices();
    for (int i = selection.length - 1; i >= 0; i--) {
      getListModel().removeElement(selection[i]);
    }
  }

}
