/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.recording.selectiontrees;

import java.io.Serializable;

/**
 * Vertex for selection tree set definition. It might hold a set of factories or
 * a set of parameters (see subclasses).
 * 
 * @author Roland Ewald
 * 
 */
public class SelTreeSetVertex implements Serializable {

  /** Serialisation ID. */
  private static final long serialVersionUID = -438502128379419764L;

  /** ID (number in the graph). */
  private int id;

  /**
   * Constructor for bean compliance. Do not use manually.
   */
  public SelTreeSetVertex() {
  }

  /**
   * Default constructor.
   * 
   * @param id
   *          the ID of this vertex
   */
  public SelTreeSetVertex(int id) {
    this.id = id;
  }

  @Override
  public String toString() {
    return id + " (root)";
  }

  public int getID() {
    return id;
  }

  public void setID(int id) {
    this.id = id;
  }
}