package org.jamesii.core.util.graph.paths;

import java.util.Map;

import org.jamesii.ChattyTestCase;
import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.SimpleGraph;
import org.jamesii.core.util.graph.paths.Dijkstra;
import org.jamesii.core.util.misc.Strings;

/**
 * Test for {@link Dijkstra} algorithm.
 * 
 * @author Roland Ewald
 * 
 */
public class TestDijkstra extends ChattyTestCase {

  /** The Constant NUM_OF_VERTICES_EMPTY_GRAPH. */
  public static final int NUM_OF_VERTICES_EMPTY_GRAPH = 100;

  /** The Dijkstra algorithm. */
  Dijkstra<Integer> dijkstra;

  @Override
  protected void setUp() throws Exception {
    dijkstra = new Dijkstra<>();
  }

  /**
   * Simple test of Dijkstra algorithm.
   */
  public void testSimpleExample() {
    SimpleGraph graph = createTestGraph();

    Map<Integer, Double> pathLengths = dijkstra.compute(graph, 0);
    addInformation("Calculated result:" + Strings.dispMap(pathLengths));

    assertEquals("There should be a path length for each vertex.",
        graph.getVertexCount(), pathLengths.size());
    for (Integer vertex : graph.getVertices()) {
      assertNotNull(pathLengths.get(vertex));
      assertFalse(Double.isInfinite(vertex));
    }

    // Check true solution:
    assertEquals(0., pathLengths.get(0));
    assertEquals(7., pathLengths.get(1));
    assertEquals(9., pathLengths.get(2));
    assertEquals(20., pathLengths.get(3));
    assertEquals(20., pathLengths.get(4));
    assertEquals(20., pathLengths.get(4));
    assertEquals(11., pathLengths.get(5));
  }

  /**
   * Test unconnected graph. All distances (except to the start node) should be
   * infinite.
   */
  public void testUnconnectedGraph() {
    SimpleGraph graph = new SimpleGraph(NUM_OF_VERTICES_EMPTY_GRAPH);
    Map<Integer, Double> pathLengths = dijkstra.compute(graph, 0);
    for (int i = 1; i < NUM_OF_VERTICES_EMPTY_GRAPH; i++) {
      assertTrue(Double.isInfinite(pathLengths.get(i)));
    }
  }

  /**
   * Creates a simple test graph. The example is from <a
   * href="http://en.wikipedia.org/wiki/Dijkstra%27s_algorithm">Wikipedia</a>.
   * 
   * @return the graph
   */
  private SimpleGraph createTestGraph() {
    SimpleGraph testGraph = new SimpleGraph(6);

    testGraph.addEdge(new AnnotatedEdge<>(0, 1, 7.));
    testGraph.addEdge(new AnnotatedEdge<>(0, 2, 9.));
    testGraph.addEdge(new AnnotatedEdge<>(0, 5, 14.));

    testGraph.addEdge(new AnnotatedEdge<>(1, 2, 10.));
    testGraph.addEdge(new AnnotatedEdge<>(1, 3, 15.));

    testGraph.addEdge(new AnnotatedEdge<>(2, 3, 11.));
    testGraph.addEdge(new AnnotatedEdge<>(2, 5, 2.));

    testGraph.addEdge(new AnnotatedEdge<>(3, 4, 6.));

    testGraph.addEdge(new AnnotatedEdge<>(4, 5, 9.));
    return testGraph;
  }
}
