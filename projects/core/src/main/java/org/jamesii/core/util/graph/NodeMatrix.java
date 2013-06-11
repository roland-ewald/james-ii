/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implements a node matrix for labeled graphs.
 * 
 * @param <V>
 *          Type of the vertex object
 * @param <D>
 *          Type of the matrix fields
 * 
 * @author Jan Himmelspach
 */
public class NodeMatrix<V, D> {

  /** The data. */
  private Map<V, Map<V, D>> data = new HashMap<>();

  /**
   * The Constructor.
   * 
   * @param nodes
   *          the nodes
   * @param initValue
   *          the init value
   */
  public NodeMatrix(List<V> nodes, D initValue) {
    for (V v1 : nodes) {
      Map<V, D> list = new HashMap<>();
      data.put(v1, list);
      for (V v2 : nodes) {
        list.put(v2, initValue);
      }
    }
  }

  /**
   * Return the value at [node1, node2].
   * 
   * @param node1
   *          the node1
   * @param node2
   *          the node2
   * 
   * @return the value
   */
  public D getValue(V node1, V node2) {
    return data.get(node1).get(node2);
  }

  /**
   * Set the value at [node1, node2].
   * 
   * @param node1
   *          the node1
   * @param node2
   *          the node2
   * @param value
   *          the value
   */
  public void setValue(V node1, V node2, D value) {
    data.get(node1).put(node2, value);
  }

}
