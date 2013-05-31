/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

/**
 * A simple graph. It has {@link Integer} vertices, {@link Double} labels, and
 * arbitrary objects as annotations.
 * 
 * Date: 12.12.2006
 * 
 * @author Roland Ewald
 */
public class SimpleGraph
    extends
    AnnotatedGraph<Integer, AnnotatedEdge<Integer, Double, Object>, Double, Double, Object, Object>
    implements ISimpleGraph {

  /** Serialisation ID. */
  private static final long serialVersionUID = 4071167252666583961L;

  /**
   * Default constructor for empty graph.
   */
  public SimpleGraph() {
    super(new Integer[0]);
    this.setSimple(true);
  }

  /**
   * Default constructor.
   * 
   * @param numOfVertices
   *          the num of vertices
   */
  public SimpleGraph(int numOfVertices) {
    super(new Integer[0]);

    Integer[] vertices = new Integer[numOfVertices];
    for (int i = 0; i < vertices.length; i++) {
      vertices[i] = i;
    }

    this.addVertices(vertices);

    // we should be simple, shouldn't we?
    this.setSimple(true);
  }

  /**
   * Adds a number of new vertices to the graph.
   * 
   * @param numOfVertices
   *          the number of new vertices
   */
  public final void addVertices(int numOfVertices) {
    int vertexCount = getVertexCount();
    Integer[] vertices = new Integer[numOfVertices];
    for (int i = 0; i < vertices.length; i++) {
      vertices[i] = vertexCount + i;
    }
  }

}
