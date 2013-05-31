package org.jamesii.core.util.graphs;

import java.util.Arrays;
import java.util.List;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.Graph;
import org.jamesii.core.util.graph.IGraph;

/**
 * Perform tests from {@link AbstractGraphTest} on a {@link Graph}
 * 
 * @author Arne Bittig
 * @date Mar 13, 2012
 */
public class GraphTest extends AbstractGraphTest<Integer, Edge<Integer>> {

  @Override
  protected IGraph<Integer, Edge<Integer>> getEmptyGraph() {
    return new Graph<>(new Integer[0]);
  }

  @Override
  protected List<Integer> getTestVertices() {
    return Arrays.asList(1, 2, 3, 4);
  }

  @Override
  protected Edge<Integer> getEdge(Integer v1, Integer v2) {
    return new Edge<>(v1, v2);
  }

}
