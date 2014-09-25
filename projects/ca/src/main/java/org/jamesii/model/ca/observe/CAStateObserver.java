/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
/**
 * Title:        CoSA: CAStateObserver
 * Description:
 * Copyright:    Copyright (c) 2004
 * Company:      University of Rostock, Faculty of Computer Science
 *               Modeling and Simulation group
 * Created on 09.06.2004
 * @author       Jan Himmelspach
 * @version      1.0
 */
package org.jamesii.model.ca.observe;

import java.sql.ResultSet;

import org.jamesii.core.base.Entity;
import org.jamesii.core.data.runtime.DataObserver;
import org.jamesii.model.cacore.CAState;

public class CAStateObserver<E extends CAState> extends DataObserver<E> {

  static final long serialVersionUID = -5764161817377863634L;

  /**
   * Remembers the "state", alive or not of the observed cell Problem: This
   * observer can only be used for ONE cell!!!!
   */
  private E updatedState;

  @Override
  @SuppressWarnings("unchecked")
  public void handleUpdate(E entity) {
    this.updatedState = entity;
  }

  /**
   * @param state
   * @param data
   * @param offset
   */
  protected void update(@SuppressWarnings("unused")
  Entity state, ResultSet data, int offset) {

  }

  /**
   * @return
   */
  @Override
  public int getColumnCount() {
    return 0;
  }

  @Override
  public String[] getColumnNames() {
    return null;
  }

  /**
   * @return
   */
  public E getState() {
    return updatedState;
  }

  @Override
  protected void store(ResultSet theData, int theOffset) {
    // TODO Auto-generated method stub

  }

}
