/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.visualization.offline;

import java.util.List;

import org.jamesii.core.data.storage.IDataStorage;
import org.jamesii.core.processor.IRunnable;
import org.jamesii.gui.application.IWindow;

/**
 * Defines the interface a component for off-line visualisation has to
 * implement.
 * 
 * @author Roland Ewald
 * 
 */
public interface IOfflineVisualizer extends IRunnable {

  /**
   * Set data storage from which the data is to be read.
   * 
   * @param dataStorage
   *          the data storage
   */
  void setDataStorage(IDataStorage dataStorage);

  /**
   * Used by the GUI to retrieve all windows that ought to be displayed for the
   * visualisation.
   * 
   * @return the list of windows
   */
  List<IWindow> getDisplayWindows();

}
