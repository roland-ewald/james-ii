/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.traverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.IGraph;

/**
 * Implements a simple cycle detection algorithm (using DFS). This code is also
 * used to determine whether a graph is a tree.
 * 
 * @author Roland Ewald
 */
public final class CycleDetection {

  private CycleDetection() {
  }

  /**
   * Detect one cycle (if any is present) in a simple graph, ignoring edge
   * directions if graph is directed.
   * 
   * A length-2 cycle node1->node2, node2->node1 will not be detected since this
   * is a multi-edge, i.e. the graph is not simple. In a directed graph, the
   * "feed-forward loop" node1->node2, node1->node3, node2->node3 will be
   * considered a cycle, since directions are ignored. Also, which vertex is
   * first in the returned list is arbitrary, as is the direction in which the
   * cycle is returned.
   * 
   * @param graph
   *          the graph in which the cycles shall be detected
   * 
   * @param <V>
   *          the type of the vertex
   * 
   * @return the list of nodes forming the cycle (empty list if none present)
   */
  public static <V> List<V> detectCycle(IGraph<V, ?> graph) {

    Map<V, Boolean> reachableVertices = new HashMap<>();

    List<V> recentlyReachedVertices = new ArrayList<>();
    List<V> visitedVertices = new ArrayList<>();
    List<V> vertices = graph.getVertices();

    // Look for the next non-reached vertex
    for (V vertex : vertices) {
      if (!reachableVertices.containsKey(vertex)) {

        // Testing whether a cycle was found (=> ready to return)
        if (!searchTree(graph, vertex, recentlyReachedVertices, visitedVertices)) {
          int size = recentlyReachedVertices.size();
          return new ArrayList<>(recentlyReachedVertices.subList(
              recentlyReachedVertices.indexOf(recentlyReachedVertices
                  .get(size - 1)), size - 1));
        }

        // Adding visited vertices
        for (V v : visitedVertices) {
          reachableVertices.put(v, true);
        }

        visitedVertices.clear();
      }
    }

    return new ArrayList<>(); // true - empty list means there is no cycle
  }

  /**
   * Gets the neighbours.
   * 
   * @param graph
   *          the graph
   * @param vertex
   *          the vertex
   * 
   * @param <V>
   *          the type of the vertex
   * 
   * @return the neighbours
   */
  protected static <V> List<V> getNeighbours(IGraph<V, ?> graph, V vertex) {
    return graph.getNeighboursOfNode(vertex);
  }

  /**
   * Searches - beginning at the start vertex - through the graph (DFS), assumes
   * that the graph is undirected and simple.
   * 
   * @param graph
   *          the graph
   * @param startVertex
   *          the start vertex
   * @param recentlyReachedVertices
   *          the recently reached vertices
   * @param visitedVertices
   *          the visited vertices
   * 
   * @param <V>
   *          the type of the vertex
   * 
   * @return true, if search tree
   */
  protected static <V> boolean searchTree(IGraph<V, ?> graph, V startVertex,
      List<V> recentlyReachedVertices, List<V> visitedVertices) {

    // Add start vertex to the list of formerly visited vertices
    visitedVertices.add(startVertex);
    recentlyReachedVertices.add(startVertex);

    // Search for all possible neighbours of startVertex
    List<V> adjacencyList = CycleDetection.getNeighbours(graph, startVertex);
    for (V neighbour : adjacencyList) {

      int indexOfNeighbour = recentlyReachedVertices.indexOf(neighbour);

      if (indexOfNeighbour >= 0
          && indexOfNeighbour == recentlyReachedVertices.size() - 2) {
        continue;
      }

      if (indexOfNeighbour != -1) {
        recentlyReachedVertices.add(neighbour);
        return false;
      }

      if (!searchTree(graph, neighbour, recentlyReachedVertices,
          visitedVertices)) {
        return false;
      }
    }

    recentlyReachedVertices.remove(startVertex);

    return true;
  }
}
