/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.utils.list.view;

import javax.swing.ListSelectionModel;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionListener;

/**
 * Interface for item views.
 * 
 * @author Jan Himmelspach
 * 
 */
public interface IItemView {

  /**
   * Add a listener to the list that's notified each time a change to the
   * selection occurs.
   */
  void addListSelectionListener(ListSelectionListener x);

  /**
   * Return the list of indices of the items selected in the view.
   * 
   * @return
   */
  int[] getSelectedIndices();

  /**
   * Return the list selection model used in the view.
   * 
   * @return
   */
  ListSelectionModel getSelectionModel();

  /**
   * Set the list selection model to be used in the view.
   * 
   * @param model
   */
  void setSelectionModel(ListSelectionModel model);

  /**
   * Set the selection mode usable in the view.
   * 
   * @param selectionMode
   */
  void setSelectionMode(int selectionMode);

  /**
   * Enabled / disable auto drag support in the view.
   * 
   * @param b
   */
  void setDragEnabled(boolean b);

  /**
   * Set the transfer handler (drag and drop support)
   * 
   * @param newHandler
   */
  void setTransferHandler(TransferHandler newHandler);

}
