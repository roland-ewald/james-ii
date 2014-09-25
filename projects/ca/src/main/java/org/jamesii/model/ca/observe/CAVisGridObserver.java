/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.model.ca.observe;

import java.io.PrintStream;

import org.jamesii.core.base.IEntity;
import org.jamesii.core.observe.Observer;

public class CAVisGridObserver extends Observer<IEntity> {

  static final long serialVersionUID = -9045056787135025471L;

  /**
   * The out stream can be redirected, thus printing can be done on any print
   * stream
   */
  private final transient PrintStream out;

  public CAVisGridObserver() {
    this(System.out);
  }

  /**
   * 
   * @param stream
   */
  public CAVisGridObserver(PrintStream stream) {
    super();
    out = stream;
  }

  @Override
  public void update(IEntity entity) {

  }

}
