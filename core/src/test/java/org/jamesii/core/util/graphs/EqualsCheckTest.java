/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graphs;

import org.jamesii.core.util.graph.EqualsCheck;
import org.jamesii.core.util.graph.SimpleEdge;
import org.jamesii.core.util.graph.trees.SimpleTree;

import junit.framework.TestCase;

/**
 * Tests for the equality checker for the graph classes.
 * 
 * @author Roland Ewald
 */
public class EqualsCheckTest extends TestCase {

  /** Basic tree. */
  SimpleTree simpleTree = new SimpleTree(3);

  /** Similar tree. */
  SimpleTree similarSimpleTree = new SimpleTree(3);

  /** Structurally changed tree (values are the same). */
  SimpleTree nonSimilarSimpleTree1 = new SimpleTree(3);

  /** Tree with changed values (structure is the same). */
  SimpleTree nonSimilarSimpleTree2 = new SimpleTree(1);

  @Override
  public void setUp() {
    setUpBasicTree(simpleTree);
    setUpBasicTree(similarSimpleTree);

    nonSimilarSimpleTree1.setTreeRoot(0);
    nonSimilarSimpleTree1.addEdge(new SimpleEdge(1, 0));
    nonSimilarSimpleTree1.addEdge(new SimpleEdge(2, 1));

    nonSimilarSimpleTree2.setTreeRoot(0);
    nonSimilarSimpleTree2.addVertex(2);
    nonSimilarSimpleTree2.addEdge(new SimpleEdge(2, 0));
    nonSimilarSimpleTree2.addVertex(3);
    nonSimilarSimpleTree2.addEdge(new SimpleEdge(3, 0));
  }

  /**
   * Tests for simple equality check on trees (labels and annotations will be
   * ignored).
   */
  public void testSimpleTrees() {
    assertTrue(EqualsCheck.equals(simpleTree, similarSimpleTree));
    assertFalse(EqualsCheck.equals(simpleTree, nonSimilarSimpleTree1));
    assertFalse(EqualsCheck.equals(simpleTree, nonSimilarSimpleTree2));
  }

  /**
   * Sets the up basic tree.
   * 
   * @param tree
   *          the new up basic tree
   */
  protected void setUpBasicTree(SimpleTree tree) {
    tree.setTreeRoot(0);
    tree.addEdge(new SimpleEdge(1, 0));
    tree.addEdge(new SimpleEdge(2, 0));
  }

}
