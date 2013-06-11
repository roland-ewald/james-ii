/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.List;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.NodeMatrix;

/**
 * Algorithm of Floyd (Extension to the one of Warshall) Computes the number of
 * hops between all nodes.
 * 
 * (1) for all i,j : d[i,j] = w[i,j]
 * 
 * (2) for k = 1 to n
 * 
 * (3) for all pairs i,j
 * 
 * (4) d[i,j] = min (d[i,j],d[i,k] + d[k,j])
 * 
 * @author Nico Eggert
 */

public class FloydWarshall extends Paths<Integer> {

  @Override
  public <V> NodeMatrix<V, Integer> compute(IGraph<V, ?> graph) {
    NodeMatrix<V, Integer> result = graph.getAdjacencyMatrix();

    List<V> vertices = graph.getVertices();
    int n = graph.getVertexCount();
    int i, j, k;

    // Floyd-Warshall algorithm
    for (k = 0; k < n; k++) {
      V vk = vertices.get(k);
      for (i = 0; i < n; i++) {
        V vi = vertices.get(i);
        for (j = 0; j < n; j++) {
          V vj = vertices.get(j);
          result.setValue(
              vi,
              vj,
              Math.min(result.getValue(vi, vj), result.getValue(vi, vk)
                  + result.getValue(vk, vj)));
        }
      }
    }

    return result;
  }

}
