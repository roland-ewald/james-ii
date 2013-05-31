package org.jamesii.core.util.graphs;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.trees.Tree;

/**
 * Perform tests from {@link AbstractGraphTest} on a {@link Tree}
 * 
 * @author Arne Bittig
 * @date Mar 13, 2012
 */
public class TreeTest extends AbstractGraphTest<Object, Edge<Object>> {

  @Override
  protected IGraph<Object, Edge<Object>> getEmptyGraph() {
    return new Tree<>(Collections.emptyList());
  }

  @Override
  protected List<Object> getTestVertices() {
    return Arrays.asList("v1", 2, new Object(), Math.PI);
  }

  @Override
  protected Edge<Object> getEdge(Object v1, Object v2) {
    return new Edge<>(v1, v2);
  }

}
