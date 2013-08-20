/*
 * The general modeling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.traverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IDirectedGraph;
import org.jamesii.core.util.misc.Pair;

/**
 * 
 * This class uses Tarjan's algorithm to detect cycles in a directed graph.
 * 
 * @author Sebastian Nähring
 * 
 */
public class DirectedCycleDetection {

  /** Global index using during the algorithm. */
  private static int globIndex;

  /**
   * Detects all cycles of the given graph and returns the cycle with the most
   * vertices. May be empty if no cycle is detected. Cycles of length 1 are also
   * returned.
   * 
   * @param graph
   *          the graph to search
   * @return the cycle producing the longest path, if there are several cycles
   *         with the same length the last one detected is returned.
   */
  public static <V> List<V> detectBiggestCycle(IDirectedGraph<V, ?> graph) {
    int index = -1;
    int max = 0;

    List<List<V>> cycles = detectCycles(graph);
    for (int i = 0; i < cycles.size(); i++) {
      if (cycles.get(i).size() > max) {
        max = cycles.get(i).size();
        index = i;
      }
    }

    return index == -1 ? null : cycles.get(index);
  }

  /**
   * Determines all cycles in the directed graph. Cycles of length 1 are also
   * returned.
   * 
   * @return the list of all cycles (list of vertices) in the graph, may be
   *         empty if no cycle is detected
   */
  public static <V> List<List<V>> detectCycles(IDirectedGraph<V, ?> graph) {

    // clear all previous data
    globIndex = 0;
    List<List<V>> cycles = new ArrayList<>();
    List<V> stack = new ArrayList<>();
    Map<V, Pair<Integer, Integer>> indices = new HashMap<>();

    for (V vertex : graph.getVertices()) {
      // use Tarjan's algorithm for unhandled vertices
      if (getIndex(vertex, indices) == -1) {
        tarjan(graph, vertex, cycles, stack, indices);
      }

      // add cycles to self
      if (graph.hasEdge(vertex, vertex)) {
        List<V> self = new ArrayList<V>();
        self.add(vertex);
        cycles.add(self);
      }
    }

    return cycles;
  }

  /**
   * Tarjan's algorithm for discovering the strongly connected components in a
   * directed graph. It's used to discover the cycles in the graph.
   * 
   * @param vertex
   *          the vertex to start from
   */
  private static <V> void tarjan(IDirectedGraph<V, ?> graph, V vertex,
      List<List<V>> cycles, List<V> stack,
      Map<V, Pair<Integer, Integer>> indices) {
    setIndex(vertex, globIndex, indices);
    setLow(vertex, globIndex, indices);
    globIndex++;
    stack.add(0, vertex);

    for (Edge<V> edge : graph.getOutgoingEdges(vertex)) {
      V next = edge.getSecondVertex();

      if (getIndex(next, indices) == -1) {
        tarjan(graph, next, cycles, stack, indices);
        setLow(vertex,
            Math.min(getLow(vertex, indices), getLow(next, indices)), indices);
      } else if (stack.contains(next)) {
        setLow(vertex,
            Math.min(getLow(vertex, indices), getIndex(next, indices)), indices);
      }
    }

    if (getLow(vertex, indices) == getIndex(vertex, indices)) {
      List<V> cycle = new ArrayList<V>();
      V comp;
      do {
        comp = stack.remove(0);
        cycle.add(comp);
      } while (!vertex.equals(comp));

      // only add components bigger than 1, because otherwise it's no cycle
      if (cycle.size() > 1) {
        cycles.add(cycle);
      }
    }
  }

  /**
   * Gets the pair of index and lowlink value used for Tarjan's algorithm.
   * 
   * @param vertex
   *          the vertex
   * @return the pair if index and lowlink
   */
  private static <V> Pair<Integer, Integer> getPair(V vertex,
      Map<V, Pair<Integer, Integer>> indices) {
    Pair<Integer, Integer> pair = indices.get(vertex);
    if (pair == null) {
      pair = new Pair<>();
      indices.put(vertex, pair);
    }
    return pair;
  }

  /**
   * Gets the index of the given vertex used for Tarjan's algorithm.
   * 
   * @param vertex
   *          the vertex
   * @return the index value, -1 if none is set yet
   */
  private static <V> int getIndex(V vertex,
      Map<V, Pair<Integer, Integer>> indices) {
    Integer index = getPair(vertex, indices).getFirstValue();
    return index == null ? -1 : index;
  }

  /**
   * Gets the lowlink of the given vertex used for Tarjan's algorithm.
   * 
   * @param vertex
   *          the vertex
   * @return the lowlink value, -1 if none is set yet
   */
  private static <V> int getLow(V vertex, Map<V, Pair<Integer, Integer>> indices) {
    Integer low = getPair(vertex, indices).getSecondValue();
    return low == null ? -1 : low;
  }

  /**
   * Sets the index of the given vertex used for Tarjan's algorithm.
   * 
   * @param vertex
   *          the vertex
   * @param value
   *          the index value
   */
  private static <V> void setIndex(V vertex, int value,
      Map<V, Pair<Integer, Integer>> indices) {
    getPair(vertex, indices).setFirstValue(value);
  }

  /**
   * Sets the lowlink of the given vertex used for Tarjan's algorithm.
   * 
   * @param vertex
   *          the vertex
   * @param value
   *          the lowlink value
   */
  private static <V> void setLow(V vertex, int value,
      Map<V, Pair<Integer, Integer>> indices) {
    getPair(vertex, indices).setSecondValue(value);
  }
}
