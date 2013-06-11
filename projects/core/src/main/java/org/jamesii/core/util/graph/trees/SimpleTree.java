/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.trees;

import java.util.ArrayList;

import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.ISimpleGraph;

/**
 * This class merely defines a 'simple' tree that has {@link Integer} as
 * vertices, {@link Double} as vertex and edge labels, and both vertices and
 * labels can be annotated by any {@link Object}.
 * 
 * Creation date: 12.12.2006
 * 
 * @author Roland Ewald
 */
public class SimpleTree
    extends
    AnnotatedTree<Integer, AnnotatedEdge<Integer, Double, Object>, Double, Double, Object, Object>
    implements ISimpleGraph {

  /** Serialisation ID. */
  private static final long serialVersionUID = -1338228768690146289L;

  /**
   * Default constructor.
   * 
   * @param numOfVertices
   *          the initial number of vertices
   */
  public SimpleTree(int numOfVertices) {
    super(new ArrayList<Integer>());
    for (int i = 0; i < numOfVertices; i++) {
      addVertex(i);
    }
  }

}
