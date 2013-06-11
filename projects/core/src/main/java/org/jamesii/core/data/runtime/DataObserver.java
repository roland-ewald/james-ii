/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.data.runtime;

import java.sql.ResultSet;

import org.jamesii.core.observe.IObservable;
import org.jamesii.core.observe.NotifyingObserver;

/**
 * Every implemented observer will directly access the ResultSet which is used
 * as the basis of simulation run data.
 * 
 * @author Mathias Röhl (mroehl), last changed by $Author: mroehl $
 * @version 0.$Rev: 2581 $<br>
 *          $Date: 2006-01-17 14:23:11 +0100 (Tue, 17 Jan 2006) $
 */
public abstract class DataObserver<E extends IObservable> extends
    NotifyingObserver<E> {

  /**
   * Serialisation ID.
   */
  private static final long serialVersionUID = -3498096571763872940L;

  /**
   * This method is called when information about an Data which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @return the column count
   */
  public abstract int getColumnCount();

  /**
   * This method is called when information about an Data which was previously
   * requested using an asynchronous interface becomes available.
   * 
   * @return the column names
   */
  public abstract String[] getColumnNames();

  /**
   * Store changes of a (model) state into simulation table.
   * 
   * @param theData
   *          tabular RunData of the simulation
   * @param theOffset
   *          column offset
   */
  protected abstract void store(ResultSet theData, int theOffset);
}
