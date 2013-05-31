/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils;

import javax.swing.ComboBoxModel;
import javax.swing.ListModel;

/**
 * The Class FilteredComboBoxModel.
 * 
 * @author Stefan Rybacki
 * @param <E>
 *          the generic type of the used filter value
 */
public class FilteredComboBoxModel<E> extends FilteredListModel<E> implements
    ComboBoxModel {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = -2945308834861876303L;

  /**
   * Instantiates a new filtered combo box model.
   * 
   * @param wrappedModel
   *          the wrapped model
   * @param filter
   *          the filter
   */
  public FilteredComboBoxModel(ListModel wrappedModel, IFilter<E> filter) {
    super(wrappedModel, filter);
  }

  /**
   * Instantiates a new filtered combo box model.
   * 
   * @param wrappedModel
   *          the wrapped model
   */
  public FilteredComboBoxModel(ListModel wrappedModel) {
    super(wrappedModel);
  }

  /**
   * The selected item.
   */
  private Object selectedItem;

  /**
   * Gets the selected item.
   * 
   * @return the selected item
   */
  @Override
  public Object getSelectedItem() {
    return selectedItem;
  }

  /**
   * Sets the selected item.
   * 
   * @param anItem
   *          the new selected item
   */
  @Override
  public void setSelectedItem(Object anItem) {
    selectedItem = anItem;
  }

}
