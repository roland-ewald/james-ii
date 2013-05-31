/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * This class represents a labelled edge.
 * 
 * @param <V>
 *          type of the vertices
 * @param <L>
 *          type of the edge labels
 * 
 * @author Roland Ewald
 */
public class LabeledEdge<V, L> extends Edge<V> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = 2677442209225188831L;

  /** Label of the edge (e.g., weight) */
  private L label = null;

  /**
   * Creates edge with null label.
   * 
   * @param first
   *          first node
   * @param second
   *          second node
   */
  public LabeledEdge(V first, V second) {
    super(first, second);
  }

  /**
   * Default constructor. The label can be anything, e.g. a length, weight, ...
   * 
   * @param first
   *          first node
   * @param second
   *          second node
   * @param label
   *          label object
   */
  public LabeledEdge(V first, V second, L label) {
    super(first, second);
    this.label = label;
  }

  /**
   * Get label.
   * 
   * @return label
   */
  public L getLabel() {
    return label;
  }

  /**
   * Sets the label.
   * 
   * @param label
   *          the new label
   */
  public void setLabel(L label) {
    this.label = label;
  }

}
