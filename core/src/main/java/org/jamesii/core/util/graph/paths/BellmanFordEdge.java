/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import org.jamesii.core.util.graph.LabeledEdge;

/**
 * This type of edge is used in the Bellman-Ford algorithm.
 * 
 * @author Nico Eggert
 * @param <V>
 */
public class BellmanFordEdge<V> {

  /** The first vertex. */
  private V first;

  /** The second vertex. */
  private V second;

  /** The weight of the edge. */
  private double weight;

  /**
   * Creates a "blank" BellmanFordEdge.
   */
  public BellmanFordEdge() {
    first = null;
    second = null;
    weight = 0;
  }

  /**
   * Instantiates a new bellman ford edge.
   * 
   * @param edge
   *          the edge
   */
  public BellmanFordEdge(LabeledEdge<V, ?> edge) {
    // set the vertices
    first = edge.getFirstVertex();
    second = edge.getSecondVertex();

    // set the weight
    Object label = edge.getLabel();
    if (label instanceof Double) {
      weight = (Double) label;
    } else {
      throw new InternalError(
          "Invalid label type. This error occurs while transforming a LabeledEdge into a BellmanFordEdge.");
    }
  }

  /**
   * Creates BellmanFordEdge with initial values;.
   * 
   * @param f
   *          the first vertex
   * @param s
   *          the second vertex
   * @param w
   *          the weight
   */
  public BellmanFordEdge(V f, V s, double w) {
    first = f;
    second = s;
    weight = w;
  }

  /**
   * Gets the first.
   * 
   * @return the first
   */
  public V getFirst() {
    return first;
  }

  /**
   * Gets the second.
   * 
   * @return the second
   */
  public V getSecond() {
    return second;
  }

  /**
   * Gets the weight.
   * 
   * @return the weight
   */
  public double getWeight() {
    return weight;
  }

  /**
   * Sets the first.
   * 
   * @param f
   *          the new first
   */
  public void setFirst(V f) {
    first = f;
  }

  /**
   * Sets the second.
   * 
   * @param s
   *          the new second
   */
  public void setSecond(V s) {
    second = s;
  }

  /**
   * Sets the weight.
   * 
   * @param w
   *          the new weight
   */
  public void setWeight(double w) {
    weight = w;
  }

}
