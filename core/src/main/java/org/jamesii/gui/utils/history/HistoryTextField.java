/*
 * The general modelling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.history;

import org.jamesii.gui.utils.AutoCompletionTextField;

/**
 * The class HistoryTextField. The class extends the class
 * {@link AutoCompletionTextField} by an id which makes it possible to get/store
 * items in the history.
 * 
 * @author Enrico Seib
 */
public class HistoryTextField extends AutoCompletionTextField {

  /**
   * Serialization ID
   */
  private static final long serialVersionUID = 1941888248007353055L;

  /**
   * The id where to store/get items out of the JAMES History
   */
  private String historyId;

  /**
   * Constructor.
   * 
   * @param historyId
   *          id where to store/get items in history
   * @param subKeys
   *          true if items of subkeys should be included as well, else false
   * @param maxValues
   *          maximal number of values managed in the internal model
   * @param sortingOption
   *          {@link History #LATEST}, {@link History #MOST_USED} or
   *          {@link History #UNSORTED}
   * @param columns
   *          the columns for the textfield's width
   */
  public HistoryTextField(int columns, String historyId, boolean subKeys,
      int maxValues, int sortingOption) {
    super(columns, new HistoryComboBoxModel(historyId, subKeys, maxValues,
        sortingOption), true);
    this.historyId = historyId;
  }

  /**
   * Constructor.
   * 
   * @param historyId
   *          id where to store/get items in history
   * @param subKeys
   *          true if items of subkeys should be included as well, else false
   * @param maxValues
   *          maximal number of values managed in the internal model
   * @param sortingOption
   *          {@link History #LATEST}, {@link History #MOST_USED} or
   *          {@link History #UNSORTED}
   */
  public HistoryTextField(String historyId, boolean subKeys, int maxValues,
      int sortingOption) {
    super(
        new HistoryComboBoxModel(historyId, subKeys, maxValues, sortingOption),
        true);
    this.historyId = historyId;
  }

  /**
   * Puts value into the history.
   */
  public void commit() {
    History.putValueIntoHistory(historyId, getText());
  }

}
