/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.List;

import org.jamesii.core.util.graph.Graph;
import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.NodeMatrix;

/**
 * Algorithm of Warshall.
 * 
 * (1) for all i,j : d[i,j] = w[i,j]
 * 
 * (2) for k = 1 to n
 * 
 * (3) for all pairs i,j
 * 
 * (4) if d[i,j] = 0 (5) d[i,j] = d[i,k] * d[k,j].
 * 
 * @author Nico Eggert
 */

public class Warshall extends Paths<Boolean> {

  @Override
  public <V> NodeMatrix<V, Boolean> compute(IGraph<V, ?> graph) {

    List<V> vertices = graph.getVertices();
    NodeMatrix<V, Integer> adjmatrix = graph.getAdjacencyMatrix();

    // Warshall's algorithm
    for (V vk : vertices) {
      for (V vi : vertices) {
        for (V vj : vertices) {
          if (adjmatrix.getValue(vi, vj) == 0) {
            adjmatrix.setValue(vi, vj,
                adjmatrix.getValue(vi, vk) * adjmatrix.getValue(vk, vj));
          }
        }
      }
    }

    NodeMatrix<V, Boolean> result = Graph.getMatrix(graph, false);
    // conversion to a boolean matrix
    for (V vi : vertices) {
      for (V vj : vertices) {
        result.setValue(vi, vj, !(adjmatrix.getValue(vi, vj) == 0));

      }
    }

    return result;
  }

}
