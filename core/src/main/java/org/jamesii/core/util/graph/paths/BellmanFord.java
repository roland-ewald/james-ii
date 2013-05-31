/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.paths;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.LabeledEdge;

/**
 * The Bellman Ford Algorithm returns the distances of all vertices to a source
 * vertex if there is no negative cycle.
 * 
 * @param <V>
 *          the type of vertices
 * 
 * @author Nico Eggert
 */
public class BellmanFord<V> extends ShortestPath<V> {

  /**
   * Compute the shortest paths.
   * 
   * @param graph
   *          the graph
   * @param source
   *          the source
   * 
   * @return the distances of all vertices to the source vertex
   */
  @Override
  public Map<V, Double> compute(
      IGraph<V, ? extends LabeledEdge<V, Double>> graph, V source) {

    List<BellmanFordEdge<V>> edges = extractEdges(graph);
    int n = graph.getVertexCount();
    int i, j;
    List<V> oVertices = graph.getVertices();

    List<BellmanFordVertex<V>> vertices = new ArrayList<>();

    /*
     * Step 1: Initialize graph for each vertex v in vertices: if v is source
     * then v.distance := 0 else v.distance := infinity v.predecessor := null
     */

    for (i = 0; i < oVertices.size(); i++) {
      vertices.add(new BellmanFordVertex<>(oVertices.get(i)));
      if (source == vertices.get(i).getV()) {
        vertices.get(i).setDist(0);
      } else {
        vertices.get(i).setDist(Double.POSITIVE_INFINITY);
      }
      vertices.get(i).setPred(-1);

    }

    /*
     * Step 2: relax edges repeatedly for i from 1 to size(vertices): for each
     * edge uv in edges: u := uv.source v := uv.destination // uv is the edge
     * from u to v if v.distance > u.distance + uv.weight: v.distance :=
     * u.distance + uv.weight v.predecessor := u
     */

    for (i = 0; i < vertices.size(); i++) {
      for (j = 0; j < edges.size(); j++) {
        if (vertices.get(vertices.indexOf(edges.get(j).getSecond())).getDist() > vertices
            .get(vertices.indexOf(edges.get(j).getFirst())).getDist()
            + edges.get(j).getWeight()) {
          vertices.get(vertices.indexOf(edges.get(j).getSecond())).setDist(
              vertices.get(vertices.indexOf(edges.get(j).getFirst())).getDist()
                  + edges.get(j).getWeight());
          vertices.get(vertices.indexOf(edges.get(j).getSecond())).setPred(
              vertices.indexOf(edges.get(j).getFirst()));
        }
      }
    }

    // convert vertices to result
    Map<V, Double> result = new HashMap<>(vertices.size());

    for (i = 0; i <= n; i++) {
      result.put(oVertices.get(i), vertices.get(i).getDist());
    }

    return result;
  }

  /**
   * Extracts edges and their weights from the adjacency matrix of a graph.
   * 
   * @param graph
   *          the graph
   * 
   * @return a list of BellmanFord edges
   */
  public List<BellmanFordEdge<V>> extractEdges(
      IGraph<V, ? extends LabeledEdge<V, Double>> graph) {
    int i;
    List<LabeledEdge<V, Double>> edgeList = new ArrayList<>();
    List<LabeledEdge<V, Double>> edgeList2 = new ArrayList<>();
    List<BellmanFordEdge<V>> result = new ArrayList<>();

    // Add all edges of all vertices (some edges may appear twice)
    for (i = 0; i < graph.getVertexCount(); i++) {
      edgeList.addAll(graph.getEdges(graph.getVertices().get(i)));
    }

    // copy every edge from the first list into the second list ONCE.
    for (i = 0; i < edgeList.size(); i++) {
      if (!edgeList2.contains(edgeList.get(i))) {
        edgeList2.add(edgeList.get(i));
      }
    }

    // Convert to BellmanFordEdge
    for (i = 0; i < edgeList2.size(); i++) {
      result.add(new BellmanFordEdge<>(edgeList2.get(i)));
    }

    return result;
  }

  /**
   * checks whether a graph has a negative cycle.
   * 
   * @param graph
   *          the graph
   * @param source
   *          the source
   * 
   * @return true, if checks for negative cycle
   */
  public Boolean hasNegativeCycle(
      IGraph<V, ? extends LabeledEdge<V, Double>> graph, V source) {
    Boolean negativeC = false;
    List<BellmanFordEdge<V>> edges = extractEdges(graph);
    int i, j;
    List<V> oVertices = graph.getVertices();
    List<BellmanFordVertex<V>> vertices = new ArrayList<>();

    // execute Bellman Ford Algorithm (Step 1 & 2)
    for (i = 0; i < oVertices.size(); i++) {
      vertices.add(new BellmanFordVertex<>(oVertices.get(i)));
      if (source == vertices.get(i).getV()) {
        vertices.get(i).setDist(0);
      } else {
        vertices.get(i).setDist(Double.POSITIVE_INFINITY);
      }
      vertices.get(i).setPred(-1);

    }
    for (i = 0; i < vertices.size(); i++) {
      for (j = 0; j < edges.size(); j++) {
        if (vertices.get(vertices.indexOf(edges.get(j).getSecond())).getDist() > vertices
            .get(vertices.indexOf(edges.get(j).getFirst())).getDist()
            + edges.get(j).getWeight()) {
          vertices.get(vertices.indexOf(edges.get(j).getSecond())).setDist(
              vertices.get(vertices.indexOf(edges.get(j).getFirst())).getDist()
                  + edges.get(j).getWeight());
          vertices.get(vertices.indexOf(edges.get(j).getSecond())).setPred(
              vertices.indexOf(edges.get(j).getFirst()));
        }
      }
    }

    /*
     * Step 3: check for negative-weight cycles for each edge uv in edges: u :=
     * uv.source v := uv.destination if v.distance > u.distance + uv.weight:
     * error "Graph contains a negative-weight cycle"
     */

    for (i = 0; i < edges.size(); i++) {
      if (vertices.get(vertices.indexOf(edges.get(i).getSecond())).getDist() > vertices
          .get(vertices.indexOf(edges.get(i).getFirst())).getDist()
          + edges.get(i).getWeight()) {
        negativeC = true;
        break;
      }

    }

    return negativeC;

  }
}
