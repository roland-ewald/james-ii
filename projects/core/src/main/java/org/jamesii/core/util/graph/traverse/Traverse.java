/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.traverse;

import org.jamesii.core.util.ICallBack;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;

/**
 * Abstract class for classes that traverse the tree.
 * 
 * @author Jan Himmelspach
 * @author Nico Eggert
 * 
 * @param <N>
 *          the type of the nodes
 */
public abstract class Traverse<N> {

  /**
   * Method to traverse a graph.
   * 
   * @param graph
   *          the graph to be traversed
   * @param callBack
   *          the call back method
   */
  public abstract void traverse(IGraph<N, ? extends Edge<?>> graph,
      ICallBack<N> callBack);

}
