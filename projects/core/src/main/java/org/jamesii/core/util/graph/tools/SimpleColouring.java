/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graph.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.Graph;

/**
 * Implements a simple graph colouring scheme.
 * 
 * @author Roland Ewald
 */
public final class SimpleColouring {

  private SimpleColouring() {
  }

  /**
   * Generates a colouring using a simple DFS algorithm.
   * 
   * @param graph
   *          the graph
   * 
   * @param <V>
   *          the type of vertices
   * 
   * @return mapping that colours the graph
   */
  public static <V extends Comparable<V>> Map<V, Integer> getSimpleGraphColouring(
      Graph<V, ? extends Edge<V>> graph) {

    List<V> vertices = graph.getVertices();
    Collections.sort(vertices);
    int numOfVertices = vertices.size();

    Map<V, Integer> returnMapping = new HashMap<>(numOfVertices);

    if (numOfVertices == 0) {
      return returnMapping;
    }

    int numOfColors =
        simpleGraphColoring(graph, vertices.get(0), returnMapping, 1);

    // Repeat algorithm as long as not all vertices are colored (support
    // for unconnected graphs)
    while (returnMapping.size() < graph.getVertexCount()) {

      // Look for a node that has not been colored yet
      V vertex = null;
      for (V v : vertices) {
        vertex = v;
        if (!returnMapping.containsKey(v)) {
          break;
        }
      }

      numOfColors =
          simpleGraphColoring(graph, vertex, returnMapping, numOfColors);
    }

    return returnMapping;
  }

  /**
   * Implementation of a simple graph colouring algorithm.
   * 
   * @param graph
   *          the graph
   * @param currentNode
   *          the current node
   * @param currentMapping
   *          the current mapping
   * @param numOfColors
   *          the number of colours
   * @param <V>
   *          the type of vertices
   * @return number of colours needed to colourise the graph
   */
  private static <V extends Comparable<V>> int simpleGraphColoring(
      Graph<V, ? extends Edge<V>> graph, V currentNode,
      Map<V, Integer> currentMapping, int numOfColors) {

    // Analyse colours of neighbours
    List<V> currentNodeAdjList = graph.getNeighboursOfNode(currentNode);

    ArrayList<Integer> neighbourColors = new ArrayList<>();
    ArrayList<V> neighboursToBeVisited = new ArrayList<>();
    for (V currentNeighbour : currentNodeAdjList) {
      if (currentMapping.containsKey(currentNeighbour)) {
        int neighbourColor = currentMapping.get(currentNeighbour);
        if (!neighbourColors.contains(neighbourColor)) {
          neighbourColors.add(neighbourColor);
        }
      } else {
        neighboursToBeVisited.add(currentNeighbour);
      }
    }

    // Test whether a new color is needed, or choose a free color
    if (neighbourColors.size() == numOfColors) {
      currentMapping.put(currentNode, numOfColors);
      numOfColors++;
    } else {
      int color;
      for (color = 0; color < numOfColors; color++) {
        if (!neighbourColors.contains(color)) {
          break;
        }
      }
      currentMapping.put(currentNode, color);
    }

    // Color neighbours
    for (V currentNeighbour : neighboursToBeVisited) {
      numOfColors =
          simpleGraphColoring(graph, currentNeighbour, currentMapping,
              numOfColors);
    }

    return numOfColors;
  }

}
