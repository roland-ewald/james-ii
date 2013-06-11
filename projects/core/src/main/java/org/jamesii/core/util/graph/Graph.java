/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

/**
 * Implements a simple graph. Uses adjacency lists to store information.
 * Vertices can be associated with arbitrary objects.
 * 
 * Date: November 9, 2004
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * 
 * @author Roland Ewald
 */
public class Graph<V extends Comparable<V>, E extends Edge<V>> extends
    BasicGraph<V, E> {

  /** Serialisation ID. */
  private static final long serialVersionUID = 3107629135468585152L;

  /**
   * Creates a node 'matrix', filled entirely with the same value.
   * 
   * @param graph
   *          graph whose vertices to use
   * @param value
   *          the initial value
   * @param <D>
   *          the type of the value that represents an edge
   * @return an adjacency representation of the graph
   * @see NodeMatrix
   */
  public static <V, D> NodeMatrix<V, D> getMatrix(IGraph<V, ?> graph, D value) {
    return new NodeMatrix<>(graph.getVertices(), value);
  }

  /** Adjacency Map. */
  private Map<V, Map<V, Collection<E>>> adjacencyMap = new HashMap<>();

  /**
   * Standard constructor.
   * 
   * @param vertices
   *          vertices in the graph
   */
  public Graph(V[] vertices) {
    for (V v : vertices) {
      adjacencyMap.put(v, new HashMap<V, Collection<E>>());
    }
  }

  /**
   * Adds an edge to the graph (interchanging start and end vertices doesn't
   * matter, the graph is not directed).
   * 
   * @param edge
   *          the edge to be added
   * 
   * @return true, if the edge could be added, otherwise false
   */
  @Override
  public boolean addEdge(E edge) {

    V minimum = Graph.getMinimum(edge);
    V maximum = Graph.getMaximum(edge);

    Map<V, Collection<E>> vertexAdjacencyMap = adjacencyMap.get(minimum);

    // Test whether vertices exist in graph
    if (vertexAdjacencyMap == null || !adjacencyMap.containsKey(maximum)) {
      return false;
    }

    Collection<E> edgesBetweenVertices = vertexAdjacencyMap.get(maximum);

    if (edgesBetweenVertices == null) {
      edgesBetweenVertices = new ArrayList<>();
      vertexAdjacencyMap.put(maximum, edgesBetweenVertices);
    }

    // Test if simple
    if (this.isSimple() && edgesBetweenVertices.size() > 0) {
      return false;
    }

    edgesBetweenVertices.add(edge);

    return true;
  }

  /**
   * Adds the vertex.
   * 
   * @param vertex
   *          the vertex
   * 
   * @see org.jamesii.core.util.graph.BasicGraph#addVertex(java.lang.Object)
   */
  @Override
  public void addVertex(V vertex) {
    adjacencyMap.put(vertex, new HashMap<V, Collection<E>>());
  }

  /**
   * Gets the adjacency lists.
   * 
   * @return the adjacency matrix
   */
  @Override
  public List<List<V>> getAdjacencyLists() {

    List<List<V>> adjacencyLists = new ArrayList<>();

    List<V> vertices = getVertices();
    Collections.sort(vertices);

    for (V v : vertices) {
      adjacencyLists.add(getNeighboursOfNode(v));
    }

    return adjacencyLists;
  }

  @Override
  public Collection<E> getEdges(V vertex) {
    Collection<E> edges = new ArrayList<>();
    Collection<Collection<E>> edgesToHigherNodes =
        adjacencyMap.get(vertex).values();

    // Add all edges to higher nodes
    for (Collection<E> higherNodeEdges : edgesToHigherNodes) {
      edges.addAll(higherNodeEdges);
    }

    for (Entry<V, Map<V, Collection<E>>> entry : adjacencyMap.entrySet()) {
      if (entry.getKey().compareTo(vertex) < 0) {
        Collection<E> edgesToVertex = entry.getValue().get(vertex);
        if (edgesToVertex != null) {
          edges.addAll(edgesToVertex);
        }
      }
    }

    return edges;
  }

  /**
   * Returns all neighbours of this node.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return all neighbours of given node
   */
  @Override
  public List<V> getNeighboursOfNode(V vertex) {
    Collection<E> edges = getEdges(vertex);
    Set<V> verts = new HashSet<>();
    for (E edge : edges) {
      if (edge.getFirstVertex() != vertex) {
        verts.add(edge.getFirstVertex());
        continue;
      }
      verts.add(edge.getSecondVertex());
    }
    return new ArrayList<>(verts);
  }

  /**
   * Gets the vertex count.
   * 
   * @return total count of vertices
   */
  @Override
  public int getVertexCount() {
    return adjacencyMap.size();
  }

  /**
   * Gets the vertices.
   * 
   * @return the vertices
   * 
   * @see org.jamesii.core.util.graph.BasicGraph#getVertices()
   */
  @Override
  public List<V> getVertices() {
    return new ArrayList<>(adjacencyMap.keySet());
  }

  /**
   * Checks for edge.
   * 
   * @param fromVertex
   *          the from vertex
   * @param toVertex
   *          the to vertex
   * 
   * @return true, if edge exists
   */
  @Override
  public boolean hasEdge(V fromVertex, V toVertex) {
    return adjacencyMap.get(fromVertex).get(toVertex) != null
        || adjacencyMap.get(toVertex).get(fromVertex) != null;
  }

  /**
   * Checks for vertex.
   * 
   * @param vertex
   *          the vertex
   * @return true, if successful
   */
  @Override
  public boolean hasVertex(V vertex) {
    return this.adjacencyMap.get(vertex) != null;
  }

  @Override
  public boolean isDirected() {
    return false;
  }

  /**
   * Removes an edge from the graph - ATTENTION: This has to be the *same* edge
   * object that was added!.
   * 
   * @param edge
   *          edge to be removed
   * 
   * @return true, if removal was successful
   */
  @Override
  public boolean removeEdge(E edge) {

    V minimum = Graph.getMinimum(edge);
    V maximum = Graph.getMaximum(edge);

    Map<V, Collection<E>> neighbors = this.adjacencyMap.get(minimum);

    if (neighbors == null) {
      return false;
    }

    Collection<E> edgeList = neighbors.get(maximum);

    // Only remove existing edges between existing vertices
    if (edgeList == null) {
      return false;
    }

    boolean success = edgeList.remove(edge);

    if (edgeList.size() == 0) {
      neighbors.remove(maximum);
    }

    return success;
  }

  /**
   * Removes a vertex from the graph (including all edges, to wich this is
   * connected).
   * 
   * @param vertex
   *          the vertex
   * 
   * @return true, if removal was successful
   */
  @Override
  public boolean removeVertex(V vertex) {

    // Remove adjacency list of vertex
    if (adjacencyMap.remove(vertex) == null) {
      return false;
    }

    // Remove edges from 'smaller' vertices to vertex
    for (Entry<V, Map<V, Collection<E>>> adjacencyList : adjacencyMap
        .entrySet()) {
      if (adjacencyList.getKey().compareTo(vertex) <= 0) {
        adjacencyList.getValue().remove(vertex);
      }
    }

    return true;
  }

  @Override
  public Collection<E> getEdges() {
    Set<E> edges = new HashSet<>();
    for (Map<V, Collection<E>> map : adjacencyMap.values()) {
      for (Collection<E> elements : map.values()) {
        edges.addAll(elements);
      }
    }
    return edges; // new ArrayList<E>(edges);
  }

  /**
   * Gets the maximum.
   * 
   * @param <V>
   * @param <E>
   * 
   * @param edge
   *          the edge
   * 
   * @return the maximum
   */
  private static <V extends Comparable<V>, E extends Edge<V>> V getMaximum(
      E edge) {
    V vertex1 = edge.getFirstVertex();
    V vertex2 = edge.getSecondVertex();
    if (vertex1.compareTo(vertex2) > 0) {
      return vertex1;
    }
    return vertex2;
  }

  /**
   * Gets the minimum vertex of an edge. This method therefore presumes that the
   * vertices of the edge are comparable.
   * 
   * @param edge
   *          the edge
   * 
   * @param <V>
   *          type of a (comparable) vertex
   * @param <E>
   *          type of the edge
   * 
   * @return the minimum
   */
  private static <V extends Comparable<V>, E extends Edge<V>> V getMinimum(
      E edge) {
    V vertex1 = edge.getFirstVertex();
    V vertex2 = edge.getSecondVertex();
    if (vertex1.compareTo(vertex2) > 0) {
      return vertex2;
    }
    return vertex1;
  }

  /**
   * Get the internal adjacency map to work on.
   * 
   * @return
   */
  protected final Map<V, Map<V, Collection<E>>> getAdjacencyMap() {
    return adjacencyMap;
  }
}