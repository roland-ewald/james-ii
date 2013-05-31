package org.jamesii.core.util.graphs;


import java.util.Arrays;
import java.util.List;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledDirectedGraph;
import org.jamesii.core.util.graph.LabeledEdge;

/**
 * Perform tests from {@link AbstractGraphTest} on a
 * {@link LabeledDirectedGraph}
 * 
 * @author Arne Bittig
 * @date Mar 13, 2012
 */
public class LabeledDirectedGraphTest extends
    AbstractGraphTest<Double, LabeledEdge<Double, String>> {

  @Override
  protected IGraph<Double, LabeledEdge<Double, String>> getEmptyGraph() {
    return new LabeledDirectedGraph<Double, LabeledEdge<Double, String>, Integer, String>();
  }

  @Override
  protected List<Double> getTestVertices() {
    return Arrays.asList(1.1, 2., Math.PI, 42.);
  }

  @Override
  protected LabeledEdge<Double, String> getEdge(Double v1, Double v2) {
    return new LabeledEdge<>(v1, v2, "l" + v1 + "x" + v2);
  }

}
