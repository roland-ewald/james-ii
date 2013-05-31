package org.jamesii.core.util.graphs;

import java.util.List;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;

import junit.framework.TestCase;

/**
 * Tests for some {@link IGraph}s.
 * 
 * @author Arne Bittig
 * @param <V>
 *          Vertex type with which to test graph
 * @param <E>
 *          Edge type with which to test graph
 */
public abstract class AbstractGraphTest<V, E extends Edge<V>> extends TestCase {

  private IGraph<V, E> g;

  private V v1, v2, v3, v4;

  @Override
  protected void setUp() throws Exception {
    super.setUp();
    g = getEmptyGraph();
    assertEquals(0, g.getVertexCount());
    List<V> vert = getTestVertices();
    assertTrue(vert.size() >= 4);
    v1 = vert.get(0);
    v2 = vert.get(1);
    v3 = vert.get(2);
    v4 = vert.get(3);
  }

  /**
   * Get a graph to perform tests on, with given vertices and no edges -
   * applicable to all graph classes, including those requiring comparable
   * vertex types
   * 
   * @return Graph
   */
  protected abstract IGraph<V, E> getEmptyGraph();

  /**
   * Get some (>=4) instances of the vertex type to test the graph with
   * 
   * @return At least four instances of the vertex type
   */
  protected abstract List<V> getTestVertices();

  /**
   * Get an edge of the edge type to test the graph with
   * 
   * @param vert1
   *          First vertex
   * @param vert2
   *          Second vertex
   * @return Edge connecting v1 and v2
   */
  protected abstract E getEdge(V vert1, V vert2);

  /**
   * Test vertex addition and presence check.
   */
  @SuppressWarnings("unchecked")
  public void testAddAndHasVertex() {
    boolean v3AddedPreviously = g.hasVertex(v3);
    g.addVertex(v1);
    try {
      g.addVertices((V[]) new Object[] { v2, v4 });
    } catch (ClassCastException e) {
      // ApplicationLogger.log(Level.INFO, "addVertices(V[]) could not"
      // + " be tested as array of V could not be created.");
      g.addVertex(v2);
      g.addVertex(v4);
    }
    assertTrue(g.hasVertex(v4));
    assertTrue(g.hasVertex(v1));
    assertTrue(v3AddedPreviously ^ !g.hasVertex(v3));
  }

  /**
   * test edge addition and subsequent edge presence test methods
   */
  public void testAddAndHasEdge() {
    g.addVertex(v1);
    g.addVertex(v2);
    g.addVertex(v4);
    E edge21 = getEdge(v2, v1);
    E edge12 = getEdge(v1, v2);
    assertTrue("Edge addition between existing vertices should work",
        g.addEdge(edge21));
    boolean isSimpleUndirected = !g.isDirected() && g.isSimple();
    assertEquals("Adding the opposite direction of an existing edge should"
        + " work if and only if the graph is directed or not simple",
        !isSimpleUndirected, g.addEdge(edge12));
    assertTrue(g.hasEdge(v1, v2) ^ isSimpleUndirected);
    assertTrue(g.hasEdge(v2, v1));
    E edge13 = getEdge(v1, v3);
    assertFalse("Edge addition with un-added vertex should fail",
        g.addEdge(edge13));
  }

  // /**
  // * test whether use of mutating methods (setter) on an edge are noticed
  // * by the graph's hasEdge method
  // */
  // public void testEdgeChangeAfterAddition() {
  // E edge13 = getEdge(v1, v3);
  // g.addVertex(v1);
  // g.addVertex(v3);
  // assertTrue(g.addEdge(edge13));
  // assertTrue(g.hasEdge(v1, v3));
  // g.addVertex(v2);
  // edge13.setFirstVertex(v2);
  // assertTrue(g.hasEdge(v2, 3));
  // assertFalse(g.hasEdge(v1, 3));
  // }

}
