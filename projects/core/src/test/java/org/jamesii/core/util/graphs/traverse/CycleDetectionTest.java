/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.graphs.traverse;

import java.util.List;

import org.jamesii.core.util.graph.AnnotatedEdge;
import org.jamesii.core.util.graph.SimpleGraph;
import org.jamesii.core.util.graph.traverse.CycleDetection;

import junit.framework.TestCase;

/**
 * Tests {@link CycleDetection}.
 * 
 * @author Roland Ewald
 */
public class CycleDetectionTest extends TestCase {

  /**
   * Tests the cycle detection.
   */
  public void testCycleDetection() {
    SimpleGraph simpleGraph = new SimpleGraph(5);
    simpleGraph.addEdge(new AnnotatedEdge<Integer, Double, Object>(0, 1));
    simpleGraph.addEdge(new AnnotatedEdge<Integer, Double, Object>(1, 2));
    simpleGraph.addEdge(new AnnotatedEdge<Integer, Double, Object>(2, 3));
    simpleGraph.addEdge(new AnnotatedEdge<Integer, Double, Object>(3, 4));
    List<Integer> cycle = CycleDetection.detectCycle(simpleGraph);
    assertTrue("There is no cycle in the given graph.", cycle.isEmpty());
    simpleGraph.addEdge(new AnnotatedEdge<Integer, Double, Object>(4, 2));
    cycle = CycleDetection.detectCycle(simpleGraph);
    assertTrue(
        "There is a cycle (2,3,4) in the given graph.",
        cycle.size() == 3 && cycle.contains(2) && cycle.contains(3)
            && cycle.contains(4));
  }
}
