/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.utils.history;

import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractListModel;
import javax.swing.ComboBoxModel;

/**
 * ComboBox Model which implements {@link ComboBoxModel} used by
 * HistoryTextField.
 * 
 * @author Enrico Seib
 */

public class HistoryComboBoxModel extends AbstractListModel implements
    ComboBoxModel, IHistoryItemListener {

  /**
   * The Serialization ID.
   */
  private static final long serialVersionUID = -3736972701347647552L;

  /**
   * Quantity of the item list.
   */
  private int number;

  /**
   * ID of the history items belonging to this ComboBoxModel
   */
  private String historyId;

  /**
   * Internal list to store items
   */
  private List<String> itemList = null;

  /**
   * Sorting by latest element first ({@link History #LATEST}) or most used
   * element first ({@link History #MOST_USED})
   */
  private int sortingOption;

  /**
   * The currently selected item
   */
  private Object selectedItem;

  /**
   * Indicates whether sub keys should be retrieved as well
   */
  private boolean subKeys;

  /**
   * Constructor.
   * 
   * @param historyId
   *          the history id to use
   * @param subKeys
   *          sub keys yes or no
   * @param number
   *          maximum number of items returned
   * @param sortingOption
   *          the sorting option
   * @see History#getValues(String, boolean, int, int)
   */
  public HistoryComboBoxModel(String historyId, boolean subKeys, int number,
      int sortingOption) {
    this.historyId = historyId;
    this.number = number;
    this.sortingOption = sortingOption;
    this.subKeys = subKeys;
    History.addListener(this);
    itemList = History.getValues(historyId, subKeys, sortingOption, number);
  }

  @Override
  public Object getSelectedItem() {
    return selectedItem;
  }

  @Override
  public void setSelectedItem(Object anItem) {
    selectedItem = anItem;
  }

  @Override
  public Object getElementAt(int index) {
    return itemList.get(index);
  }

  @Override
  public int getSize() {
    if (itemList == null || itemList.isEmpty()) {
      return 0;
    }
    return itemList.size();
  }

  @Override
  public void cleaned(HistoryItemEvent event) {
    itemList = new ArrayList<>();
    fireContentsChanged(this, 0, itemList.size());

  }

  @Override
  public void idRemoved(HistoryItemEvent event) {
    if (historyId.equals(event.getId())) {
      itemList = new ArrayList<>();
      fireContentsChanged(this, 0, itemList.size());
    }
  }

  @Override
  public void valueAdded(HistoryItemEvent event) {
    if (historyId.equals(event.getId())) {
      itemList =
          History.getValues(this.historyId, subKeys, sortingOption, number);
      fireContentsChanged(this, 0, itemList.size());
    }

  }

  @Override
  public void valueChanged(HistoryItemEvent event) {
    if (historyId.equals(event.getId())) {
      itemList = History.getValues(historyId, subKeys, sortingOption, number);
      fireContentsChanged(this, 0, itemList.size());
    }
  }

  @Override
  public void valueRemoved(HistoryItemEvent event) {
    if (historyId.equals(event.getId())) {
      itemList = History.getValues(historyId, subKeys, sortingOption, number);
      fireContentsChanged(this, 0, itemList.size());
    }

  }

  /**
   * Sets the maximal number of elements
   * 
   * @param number
   *          maximal number of elements ((@link #number))
   */
  public void setMaxValues(int number) {
    if (this.number != number) {
      if (this.number > number) {
        this.number = number;
        for (int i = itemList.size() - 1; i > number; i--) {
          itemList.remove(i);
        }
        fireIntervalRemoved(this, itemList.size() - 1, number);

      } else {
        this.number = number;
        itemList = History.getValues(historyId, subKeys, sortingOption, number);
        fireContentsChanged(this, 0, itemList.size());
      }
    }

  }

  /**
   * Gets the maximal number of elements
   * 
   * @return maximal number of elements ((@link #number))
   */
  public int getMaxValues() {
    return number;
  }

  /**
   * Returns the {@link #historyId}
   * 
   * @return ID of the historyItems
   */
  public String getId() {
    return historyId;
  }

  /**
   * Sets the {@link #sortingOption}
   * 
   * @param sOption
   *          Sorting by latest element first ({@link History #LATEST}) or most
   *          used element first ({@link History #MOST_USED})
   */
  public void setSortingOption(int sOption) {
    sortingOption = sOption;
  }

}