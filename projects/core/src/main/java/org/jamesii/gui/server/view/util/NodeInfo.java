/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.gui.server.view.util;

/**
 * The Class NodeInfo holds information for the nodes in a tree model.
 * 
 * @author Stefan Leye
 * 
 * @param <I>
 *          type of the information
 */
public class NodeInfo<I> {

  /** The description. */
  private String description;

  /** The info. */
  private I info;

  /**
   * Instantiates a new node info.
   * 
   * @param d
   *          the d
   * @param si
   *          the si
   */
  public NodeInfo(String d, I si) {
    description = d;
    setInfo(si);
  }

  @Override
  public String toString() {
    return description;
  }

  /**
   * @return the info
   */
  public final I getInfo() {
    return info;
  }

  /**
   * @param info
   *          the info to set
   */
  protected final void setInfo(I info) {
    this.info = info;
  }

}
