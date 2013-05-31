/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graphs;

import java.util.ArrayList;
import java.util.List;

import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.trees.Tree;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Serialisation test for {@link Tree}.
 * 
 * @author Roland Ewald
 */
public class TreeSerialisationTest extends
    SimpleSerializationTest<Tree<Integer, Edge<Integer>>> {

  /** The number of vertices in the test tree. */
  private static final int NUM_VERTICES = 3;

  /** The test tree. */
  final Tree<Integer, Edge<Integer>> testTree;

  /**
   * Instantiates a new tree serialisation test. Creates the test tree.
   */
  public TreeSerialisationTest() {
    List<Integer> vertices = new ArrayList<>();
    for (int i = 0; i < NUM_VERTICES; i++) {
      vertices.add(i);
    }
    testTree = new Tree<>(vertices);
    testTree.setTreeRoot(1);
    testTree.addEdge(new Edge<>(1, 0)); // root -> vertex #1
    testTree.addEdge(new Edge<>(1, 2)); // root -> vertex #2
  }

  @Override
  public Tree<Integer, Edge<Integer>> getTestObject() throws Exception {
    return testTree;
  }

  @Override
  public void assertEquality(Tree<Integer, Edge<Integer>> original,
      Tree<Integer, Edge<Integer>> deserialisedVersion) {
    assertEquals(testTree.getTreeRoot(), deserialisedVersion.getTreeRoot());
    assertEquals(original.getVEMap().size(), deserialisedVersion.getVEMap()
        .size());
    assertEquals(original.getVertices(), deserialisedVersion.getVertices());
  }

}
