/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Implements a *simple* labeled, directed graph.
 * 
 * @param <V>
 *          type of vertices
 * @param <E>
 *          type of edges
 * @param <LV>
 *          type of vertex labels
 * @param <LE>
 *          type of edge labels
 * 
 * @author Roland Ewald
 * @author Jan Himmelspach
 */
public class LabeledDirectedGraph<V extends Comparable<V>, E extends LabeledEdge<V, LE>, LV, LE>
    extends BasicDirectedGraph<V, E> implements ILabeledGraph<V, E, LV, LE> {

  /** The Constant serialVersionUID. */
  private static final long serialVersionUID = -7875642194394929896L;

  /** The incoming edges per vertex. */
  private Map<V, Collection<E>> incoming = new HashMap<>();

  /** The outgoing edges per vertex. */
  private Map<V, Collection<E>> outgoing = new HashMap<>();

  /** The vertex labels. */
  private Map<V, LV> vertexLabels = new HashMap<>();

  @Override
  public boolean addEdge(E edge) {

    V source = edge.getFirstVertex();
    V dest = edge.getSecondVertex();

    Collection<E> outgoingFromSource = outgoing.get(source);
    Collection<E> incomingForDest = incoming.get(dest);

    // Test whether vertices exist in graph
    if (outgoingFromSource == null || incomingForDest == null) {
      return false;
    }

    // Test if edge already exists and graph is simple
    if (this.isSimple()
        && (outgoingFromSource.contains(edge) || incomingForDest.contains(edge))) {
      return false;
    }

    incomingForDest.add(edge);
    outgoingFromSource.add(edge);

    return true;
  }

  @Override
  public void addVertex(V vertex) {
    outgoing.put(vertex, new ArrayList<E>());
    incoming.put(vertex, new ArrayList<E>());
  }

  @Override
  public void addVertices(V[] vertices) {
    for (V v : vertices) {
      addVertex(v);
    }
  }

  @Override
  public Map<V, Map<V, LE>> getEdgeLabels() {
    Map<V, Map<V, LE>> result = new HashMap<>();
    for (Entry<V, Collection<E>> outEdges : outgoing.entrySet()) {
      Map<V, LE> labelMap = new HashMap<>(1);
      for (E edge : outEdges.getValue()) {
        labelMap.put(edge.getSecondVertex(), edge.getLabel());
      }
      result.put(outEdges.getKey(), labelMap);
    }
    return result;
  }

  @Override
  public Collection<E> getEdges() {
    Collection<E> result = new ArrayList<>();
    for (Collection<E> edgelist : outgoing.values()) {
      result.addAll(edgelist);
    }
    return result;
  }

  @Override
  public Collection<E> getEdges(V vertex) {
    throw new IllegalStateException("not applicable for this graph."
        + " please use getIncommingEdges() or getOutgoingEdges()");
  }

  @Override
  public Collection<E> getIncomingEdges(V vertex) {
    return incoming.get(vertex);
  }

  /**
   * Gets the label.
   * 
   * @param edge
   *          the edge
   * 
   * @return the label
   */
  public LE getLabel(E edge) {
    return edge.getLabel();
  }

  @Override
  public LV getLabel(V vertex) {
    return vertexLabels.get(vertex);
  }

  @Override
  public List<V> getNeighboursOfNode(V vertex) {
    ArrayList<V> result = new ArrayList<>();
    for (E e : outgoing.get(vertex)) {
      result.add(e.getSecondVertex());
    }
    for (E e : incoming.get(vertex)) {
      result.add(e.getFirstVertex());
    }
    return result;
  }

  @Override
  public Collection<E> getOutgoingEdges(V vertex) {
    return outgoing.get(vertex);
  }

  @Override
  public List<V> getVertexByLabel(LV vertexLabel) {
    List<V> vertices = new ArrayList<>();
    for (Entry<V, LV> entry : vertexLabels.entrySet()) {
      if (entry.getValue().equals(vertexLabel)) {
        vertices.add(entry.getKey());
      }
    }
    return vertices;
  }

  @Override
  public int getVertexCount() {
    return outgoing.size();
  }

  @Override
  public Map<V, LV> getVertexLabels() {
    return vertexLabels;
  }

  @Override
  public List<V> getVertices() {
    return new ArrayList<>(outgoing.keySet());
  }

  @Override
  public boolean hasEdge(V v1, V v2) {
    Collection<E> edges = outgoing.get(v1);
    for (E e : edges) {
      if (e.getSecondVertex() == v2) {
        return true;
      }
    }
    return false;
  }

  @Override
  public boolean hasVertex(V vertex) {
    return incoming.get(vertex) != null;
  }

  @Override
  public boolean removeEdge(E edge) {
    return outgoing.get(edge.getFirstVertex()).remove(edge)
        || incoming.get(edge.getSecondVertex()).remove(edge);
  }

  /**
   * If edge=null, the edge's equality will not be checked via equals().
   * 
   * @param fromNode
   *          the from node
   * @param toNode
   *          the to node
   * @param edge
   *          the edge
   * 
   * @return true, if the edge has been removed
   */
  public boolean removeEdge(V fromNode, V toNode, E edge) {

    boolean testForEquality = (edge != null);
    boolean result = false;
    Iterator<E> eit = outgoing.get(fromNode).iterator();
    while (eit.hasNext()) {
      E e = eit.next();
      if ((e.getFirstVertex() == fromNode) && (e.getSecondVertex() == toNode)
          && (!testForEquality || e.equals(edge))) {
        eit.remove();
        result = true;
        break;
      }
    }

    eit = incoming.get(fromNode).iterator();
    while (eit.hasNext()) {
      E e = eit.next();
      if ((e.getFirstVertex() == toNode) && (e.getSecondVertex() == fromNode)
          && (!testForEquality || e.equals(edge))) {
        eit.remove();
        result = true;
        break;
      }
    }

    return result;
  }

  @Override
  public boolean removeVertex(V vertex) {

    Iterator<E> eit = incoming.get(vertex).iterator();
    while (eit.hasNext()) {
      E e = eit.next();
      Iterator<E> e2it = outgoing.get(e.getFirstVertex()).iterator();
      while (e2it.hasNext()) {
        E e2 = e2it.next();
        if (e == e2) {
          e2it.remove();
        }
      }
      eit.remove();
    }
    incoming.remove(vertex);

    // kill outgoing edges containing the vertex passed as parameter of this
    // method

    eit = outgoing.get(vertex).iterator();
    while (eit.hasNext()) {
      E e = eit.next();
      Iterator<E> e2it = incoming.get(e.getSecondVertex()).iterator();
      while (e2it.hasNext()) {
        E e2 = e2it.next();
        if (e == e2) {
          e2it.remove();
        }
      }
      eit.remove();
    }
    outgoing.remove(vertex);
    return false;
  }

  @Override
  public boolean removeVertices(V[] vertices) {
    boolean result = false;
    for (V v : vertices) {
      result = result || removeVertex(v);
    }
    return result;
  }

  /**
   * Wonderful method. Nice way to make multi-edges senseless or at least harm
   * them somehow ;-)
   * 
   * All edges starting at fromNode and ending at toNode will get e new edge
   * label, pretty cool, he?
   * 
   * @param fromNode
   *          the starting node
   * @param toNode
   *          the, he?, ending node, pretty surprising, isn't it?
   * @param label
   *          seems to be designed for containing useful edge information,
   *          should be useful, because it could be used quite often ;-)
   * 
   * @return true, if sets the edge
   */
  public boolean setEdge(V fromNode, V toNode, LE label) {
    Collection<E> al = outgoing.get(fromNode);
    for (E e : al) {
      if (e.getSecondVertex() == toNode) {
        e.setLabel(label);
      }
    }

    al = incoming.get(toNode);
    for (E e : al) {
      if (e.getFirstVertex() == fromNode) {
        e.setLabel(label);
      }
    }
    return false;
  }

  @Override
  public boolean setLabel(V vertex, LV label) {
    // Does vertex exist?
    if (!outgoing.containsKey(vertex)) {
      return false;
    }
    vertexLabels.put(vertex, label);
    return true;
  }

  /**
   * Gets the incoming.
   * 
   * @return the incoming
   */
  public Map<V, Collection<E>> getIncoming() {
    return incoming;
  }

  /**
   * Sets the incoming.
   * 
   * @param incoming
   *          the incoming
   */
  public void setIncoming(Map<V, Collection<E>> incoming) {
    this.incoming = incoming;
  }

  /**
   * Gets the outgoing.
   * 
   * @return the outgoing
   */
  public Map<V, Collection<E>> getOutgoing() {
    return outgoing;
  }

  /**
   * Sets the outgoing.
   * 
   * @param outgoing
   *          the outgoing
   */
  public void setOutgoing(Map<V, Collection<E>> outgoing) {
    this.outgoing = outgoing;
  }

  /**
   * Sets the vertex labels.
   * 
   * @param vertexLabels
   *          the vertex labels
   */
  public void setVertexLabels(Map<V, LV> vertexLabels) {
    this.vertexLabels = vertexLabels;
  }

}