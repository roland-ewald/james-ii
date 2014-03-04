/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.registry.failuredetection;


import java.util.List;

import junit.framework.TestCase;

import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.experiments.TaskConfiguration;
import org.jamesii.core.math.random.distributions.CamelDistributionFactory;
import org.jamesii.core.math.random.distributions.ExponentialDistributionFactory;
import org.jamesii.core.math.random.distributions.NormalDistributionFactory;
import org.jamesii.core.math.random.distributions.UniformDistributionFactory;
import org.jamesii.core.math.random.distributions.WeibullDistributionFactory;
import org.jamesii.core.math.random.distributions.plugintype.AbstractDistributionFactory;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * Tests for the {@link FailureDetector}.
 * 
 * @author Roland Ewald
 */
public class TestFailureDetection extends TestCase {

  /** The test error message. */
  final static String ERROR_MSG = "Error 1";

  /** The failure detector. */
  FailureDetector detector;

  @Override
  public void setUp() {
    detector = new FailureDetector();
  }

  /**
   * Test basic functioning of {@link FailureDetector} in a simple scenario.
   * Submits as failures the following sequence:
   * 
   * (a,b,c); (a,d,c); (a,d,e)
   * 
   * --> A should be reported broken, no other factory should remain a suspect
   * 
   * 
   * @throws Exception
   *           the exception
   */
  public void testScenarioA() throws Exception {

    FailureDescription[] fds =
        { fd(nA(), nB(), nC()), fd(nA(), nD(), nC()), fd(nA(), nD(), nE()) };

    List<FailureReport> reports = null;
    for (FailureDescription fd : fds) {
      reports = detector.failureOccurred(fd);
    }

    assertNotNull(reports);
    assertEquals(1, reports.size());
    assertEquals(nA().getSelectionInformation().getFactoryClass(),
        reports.get(0).getFactory());
    assertEquals(fds.length, reports.get(0).getTrace().size());
    assertEquals(0, detector.getNumOfSuspects());
  }

  /**
   * Test the basic functioning of {@link FailureDetector} in a simple scenario.
   * Submits as failures the following sequence:
   * 
   * (a,b,c); (d,e) ; (d,b,e)
   * 
   * --> B should be reported broken, d and e should remain to be suspects.
   * 
   * @throws Exception
   *           the exception
   */
  public void testScenarioB() throws Exception {

    FailureDescription[] fds =
        { fd(nA(), nB(), nC()), fd(nD(), nE()), fd(nD(), nB(), nE()) };

    List<FailureReport> reports = null;
    for (FailureDescription fd : fds) {
      reports = detector.failureOccurred(fd);
    }

    assertNotNull(reports);
    assertEquals(1, reports.size());
    assertEquals(nB().getSelectionInformation().getFactoryClass(),
        reports.get(0).getFactory());
    assertEquals(fds.length - 1, reports.get(0).getTrace().size());

    // Suspects d and e remain because of second failure description
    assertEquals(2, detector.getNumOfSuspects());
  }

  /**
   * Creates bogus failure description.
   * 
   * @param nodes
   *          the nodes
   * 
   * @return the failure description
   */
  protected FailureDescription fd(SelectedFactoryNode... nodes) {
    return new FailureDescription(newTree(nodes), new TaskConfiguration(),
        new RuntimeException(ERROR_MSG));
  }

  /**
   * Creates 'flat' selection tree for testing purposes.
   * 
   * @param nodes
   *          the array of nodes
   * 
   * @return the selection tree
   */
  protected SelectionTree newTree(SelectedFactoryNode... nodes) {
    SelectionTree st = new SelectionTree(null);
    st.addVertices(nodes);
    if (nodes.length == 0) {
      return st;
    }
    st.addEdge(e(st.getRoot(), nodes[0]));
    for (int i = 1; i < nodes.length; i++) {
      st.addEdge(e(nodes[0], nodes[i]));
    }
    return st;
  }

  /**
   * Tests {@link FailureDetector} in case only a single component is failing.
   * 
   * @throws Exception
   *           the exception
   */
  public void testSingleComponentFailure() throws Exception {
    List<FailureReport> reports = detector.failureOccurred(fd(nA()));
    assertEquals(1, reports.size());
    assertEquals(nA().getSelectionInformation().getFactoryClass(),
        reports.get(0).getFactory());
    assertEquals(1, reports.get(0).getTrace().size());
    FailureDescription desc = reports.get(0).getTrace().get(0);
    assertEquals(ERROR_MSG, desc.getCause().getMessage());
  }

  /**
   * Create bogus node 'a'.
   * 
   * @return the selected factory node
   */
  public static SelectedFactoryNode nA() {
    return new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractDistributionFactory.class, null,
            new NormalDistributionFactory()));
  }

  /**
   * Create bogus node 'b'.
   * 
   * @return the selected factory node
   */
  public static SelectedFactoryNode nB() {
    return new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractDistributionFactory.class, null,
            new ExponentialDistributionFactory()));
  }

  /**
   * Create bogus node 'c'.
   * 
   * @return the selected factory node
   */
  public static SelectedFactoryNode nC() {
    return new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractDistributionFactory.class, null,
            new UniformDistributionFactory()));
  }

  /**
   * Create bogus node 'd'.
   * 
   * @return the selected factory node
   */
  public static SelectedFactoryNode nD() {
    return new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractDistributionFactory.class, null,
            new WeibullDistributionFactory()));
  }

  /**
   * Create bogus node 'e'.
   * 
   * @return the selected factory node
   */
  public static SelectedFactoryNode nE() {
    return new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractDistributionFactory.class, null,
            new CamelDistributionFactory()));
  }

  /**
   * Creates edge for bogus selection tree.
   * 
   * @param n1
   *          the first node
   * @param n2
   *          the second node
   * 
   * @return the edge
   */
  public static Edge<SelectedFactoryNode> e(SelectedFactoryNode n1,
      SelectedFactoryNode n2) {
    return new Edge<>(n1, n2);
  }
}
