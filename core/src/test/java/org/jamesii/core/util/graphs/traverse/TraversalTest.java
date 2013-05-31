/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graphs.traverse;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.ICallBack;
import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.traverse.BreadthFirstSearch;
import org.jamesii.core.util.graph.traverse.Traverse;
import org.jamesii.core.util.graph.trees.SimpleTree;

import junit.framework.TestCase;

/**
 * Tests for implementations of {@link Traverse}.
 * 
 * @author Roland Ewald
 */
public class TraversalTest extends TestCase {

  /**
   * Tests {@link BreadthFirstSearch}.
   */
  public void testBFS() {
    BreadthFirstSearch<Integer> bfs = new BreadthFirstSearch<>();
    SimpleTree tree = new SimpleTree(4);
    tree.setTreeRoot(0);
    tree.addEdge(new AnnotatedEdge<Integer, Double, Object>(1, 0));
    tree.addEdge(new AnnotatedEdge<Integer, Double, Object>(2, 0));
    tree.addEdge(new AnnotatedEdge<Integer, Double, Object>(3, 0));
    ListCallBack lcb = new ListCallBack();
    bfs.traverse(tree, lcb);
    assertEquals(4, lcb.getVisited().size());
    for (int i = 0; i < 4; i++) {
      assertEquals(i, lcb.getVisited().get(i).intValue());
    }
    assertEquals(4, tree.getVertexCount());
  }

}

/**
 * Callback implementation for testing.
 * 
 * @author Roland Ewald
 * 
 */
class ListCallBack implements ICallBack<Integer> {

  /** The list of visited nodes. */
  List<Integer> visited = new ArrayList<>();

  @Override
  public boolean process(Integer parameter) {
    visited.add(parameter);
    return true;
  }

  /**
   * Gets the visited nodes.
   * 
   * @return the visited nodes
   */
  public List<Integer> getVisited() {
    return visited;
  }

}
