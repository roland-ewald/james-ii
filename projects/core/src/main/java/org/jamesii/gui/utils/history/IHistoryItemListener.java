/*
 * The general modeling and simulation framework JAMES II. Copyright
 * by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */

package org.jamesii.gui.utils.history;

/**
 * The interface IHistoryItemListener.
 * 
 * Interface for all listener for HistoryItem
 * 
 * @author Enrico Seib
 * 
 */
public interface IHistoryItemListener {

  /**
   * listens if HistoryItem with value value has been removed
   * 
   * @param event
   *          of type HistoryItemEvent
   */
  void valueAdded(HistoryItemEvent event);

  /**
   * listens if value of HistoryItem has changed
   * 
   * @param event
   *          of type HistoryItemEvent
   */
  void valueChanged(HistoryItemEvent event);

  /**
   * listens if HistoryItem with value has been removed
   * 
   * @param event
   *          of type HistoryItemEvent
   */
  void valueRemoved(HistoryItemEvent event);

  /**
   * listens if all HistoryItems of certain ID have been removed
   * 
   * @param event
   *          of type HistoryItemEvent
   */
  void idRemoved(HistoryItemEvent event);

  /**
   * listens if History is cleared completely
   * 
   * @param event
   *          of type HistoryItemEvent
   */
  void cleaned(HistoryItemEvent event);
}
