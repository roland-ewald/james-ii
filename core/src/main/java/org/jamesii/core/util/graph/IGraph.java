/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.Collection;
import java.util.List;

/**
 * Basic graph interface.
 * 
 * @param <V>
 *          Type of vertices
 * @param <E>
 *          Type of edges
 * 
 * @author Roland Ewald
 */
public interface IGraph<V, E extends Edge<V>> {

  /**
   * Adds edge.
   * 
   * @param edge
   *          the edge
   * 
   * @return true, if operation was successful
   */
  boolean addEdge(E edge);

  /**
   * Adds a vertex to the graph.
   * 
   * @param vertex
   *          vertex to be added
   */
  void addVertex(V vertex);

  /**
   * Adds a number of vertices.
   * 
   * @param vertices
   *          the vertices
   */
  void addVertices(V[] vertices);

  /**
   * Get adjacency lists.
   * 
   * @return the adjacency lists
   */
  List<List<V>> getAdjacencyLists();

  /**
   * Gets the adjacency matrix.
   * 
   * @return the adjacency matrix
   */
  NodeMatrix<V, Integer> getAdjacencyMatrix();

  /**
   * Gets the edges.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return the edges
   */
  Collection<E> getEdges(V vertex);

  /**
   * Get neighbours of this node.
   * 
   * @param vertex
   *          the vertex
   * 
   * @return set of all neighbours of this node
   */
  List<V> getNeighboursOfNode(V vertex);

  /**
   * Return the number of vertices in the graph.
   * 
   * @return the vertex count
   */
  int getVertexCount();

  /**
   * Return all vertices of the graph.
   * 
   * @return the vertices
   */
  List<V> getVertices();

  /**
   * Removes edge.
   * 
   * @param edge
   *          the edge
   * 
   * @return true, if removes the edge
   */
  boolean removeEdge(E edge);

  /**
   * Removes a vertex from the graph.
   * 
   * @param vertex
   *          vertex to be removed
   * 
   * @return true, if removes the vertex
   */
  boolean removeVertex(V vertex);

  /**
   * Removes a number of vertices.
   * 
   * @param vertices
   *          the vertices
   * 
   * @return true, if removes the vertices
   */
  boolean removeVertices(V[] vertices);

  /**
   * Get all edges within the graph.
   * 
   * @return collection of all edges
   */
  Collection<E> getEdges();

  /**
   * Checks for edge.
   * 
   * @param vertex1
   *          the v1
   * @param vertex2
   *          the v2
   * @return true, if successful
   */
  boolean hasEdge(V vertex1, V vertex2);

  /**
   * Checks for vertex.
   * 
   * @param vertex
   *          the vertex
   * @return true, if successful
   */
  boolean hasVertex(V vertex);

  /**
   * Checks if the graph is simple. Simple graphs are graphs that have no loops
   * (an edge from a vertex to itself) and at most one edge between two
   * vertices. Note: Return value of true may indicate only that the respective
   * graph can <i>in principle</i> have multiedges or loops, not that it
   * contains either at the time of the method call.
   * 
   * @return true, if graph is simple
   */
  boolean isSimple();

  /**
   * Checks if the graph is directed. 'Directed' should be understood in a
   * rather general way: all graph implementations for which adding/removing an
   * edge(v_1,v_2) is semantically different from adding/removing an edge
   * (v_2,v_1) should return true here.
   * 
   * @return true, if this graph is directed
   */
  boolean isDirected();
}
