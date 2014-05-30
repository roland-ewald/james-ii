/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 *
 * LICENCE: JAMESLIC
 */
package org.jamesii.core.util.hierarchy;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.collection.CollectionUtils;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.IGraph;
import org.jamesii.core.util.graph.traverse.CycleDetection;
import org.jamesii.core.util.misc.SimpleSerializationTest;

/**
 * Test class for {@link IHierarchy} instances (especially
 * {@link AbstractHierarchy} subclasses w.r.t. to serializability).
 * 
 * @author Arne Bittig
 * @param <H>
 *          Hierarchy type to test
 */
public abstract class AbstractHierarchyTest<H extends IHierarchy<String> & Serializable>
    extends SimpleSerializationTest<H> {

  protected AbstractHierarchyTest(boolean skipSerializationTests) {
    this.skipSerializationTests = skipSerializationTests;
  }

  /**
   * Get a hierarchy to be tested
   * 
   * @return Empty (but modifiable) hierarchy
   */
  protected abstract <T> IHierarchy<T> getNewHierarchy();

  private IHierarchy<String> sampleH;

  @Override
  protected void setUp() {
    this.sampleH = getNewHierarchy();
    fillSampleHierarchy(sampleH);
  }

  /**
   * Create a sample hierarchy by filling an empty String hierarchy with a few
   * elements, including cycles
   * 
   * @param h
   *          Hierarchy to fill
   */
  public static void fillSampleHierarchy(IHierarchy<String> h) {
    assertTrue(h.isEmpty());
    final String nodeB1 = "b1";
    final String nodeB2 = "b2";
    final String nodeB3 = "b3";
    h.addChildParentRelation(nodeB1, nodeB2);
    h.addChildParentRelation(nodeB2, nodeB1);
    h.addChildParentRelation(nodeB3, nodeB2);

    // addition order is important: rename a* to d*
    // and a different cycle may be detected
    final String nodeD1 = "a1";
    final String nodeD2 = "a2";
    final String nodeD3 = "a3";
    final String nodeD4 = "a4";
    h.addChildParentRelation(nodeD1, nodeD2);
    h.addChildParentRelation(nodeD2, nodeD3);
    h.addChildParentRelation(nodeD3, nodeD4);
    h.addChildParentRelation(nodeD4, nodeD1);

    final String nodeC1 = "c1";
    final String nodeC2 = "c2";
    final String nodeC3 = "c3";
    h.addChildParentRelation(nodeC1, nodeC2);
    h.addChildParentRelation(nodeC2, nodeC3);
    h.addChildParentRelation(nodeC3, nodeC1);
    final String nodeC4 = "c4";
    h.addChildParentRelation(nodeC4, nodeC3);
  }

  /** test the {@link IHierarchy#addOrphan(Object)} method (and removal) */
  public void testAddOrphan() {
    boolean supportsOrphans = sampleH.getOrphans() != null;
    String orph = "newOrphan";
    assertEquals(supportsOrphans, sampleH.addOrphan(orph));
    assertTrue(sampleH.removeNode(orph).isEmpty());
  }

  /**
   * Test {@link IHierarchy#addChildParentRelation(Object, Object)},
   * {@link IHierarchy#removeNode(Object)} and
   * {@link IHierarchy#removeChildParentRelation(Object)}
   */
  public void testAddAndRemove() {
    boolean supportsOrphans = checkOrphans(sampleH);
    // add sample nodes
    final String nodeN1 = "n1";
    final String nodeN2 = "n2";
    final String nodeN3 = "n3";
    sampleH.addChildParentRelation(nodeN1, nodeN3);
    sampleH.addChildParentRelation(nodeN2, nodeN3);
    // check getChildren
    Collection<String> ch3 = sampleH.getChildren(nodeN3);
    assertEquals(2, ch3.size());
    assertTrue(ch3.contains(nodeN1));
    assertTrue(ch3.contains(nodeN2));

    // add new top (root) node
    final String nodeN4 = "n4";
    sampleH.addChildParentRelation(nodeN3, nodeN4);

    // check node status
    assertEquals(Hierarchies.NodeStatus.LEAF,
        Hierarchies.getNodeStatus(sampleH, nodeN2));
    assertEquals(Hierarchies.NodeStatus.INNER,
        Hierarchies.getNodeStatus(sampleH, nodeN3));
    assertEquals(Hierarchies.NodeStatus.ROOT,
        Hierarchies.getNodeStatus(sampleH, nodeN4));

    // remove leaf, check children' update
    sampleH.removeNode(nodeN2);
    assertFalse(sampleH.getChildren(nodeN3).contains(nodeN2));
    assertTrue(supportsOrphans ? sampleH.getOrphans().isEmpty() : sampleH
        .getOrphans() == null);
    sampleH.addChildParentRelation(nodeN2, nodeN3);

    // remove inner node, check children relation
    sampleH.removeNode(nodeN3);
    assertNull(sampleH.getParent(nodeN3));
    assertTrue(sampleH.getChildren(nodeN3).isEmpty());
    assertTrue(supportsOrphans ? sampleH.getOrphans().isEmpty() : sampleH
        .getOrphans() == null);
    assertEquals(nodeN4, sampleH.getParent(nodeN2));

    Collection<String> ch4 = sampleH.getChildren(nodeN4);
    assertEquals(2, ch4.size());
    assertTrue(ch3.containsAll(ch4));
    assertTrue(ch4.containsAll(ch3));
    SimSystem.report(Level.INFO, "Does the hierarchy reuse"
        + " child collections? " + (ch3 == ch4)); // NOSONAR

    // test remove child-parent relation
    sampleH.removeChildParentRelation(nodeN2);

    // test root node removal
    sampleH.addChildParentRelation(nodeN3, nodeN4);
    assertEquals(Hierarchies.NodeStatus.ROOT,
        Hierarchies.getNodeStatus(sampleH, nodeN4));
    sampleH.removeNode(nodeN4);
    Collection<String> elements = sampleH.getAllNodes();
    assertTrue(!supportsOrphans ^ elements.contains(nodeN1));
    assertTrue(!supportsOrphans ^ elements.contains(nodeN3));
    assertTrue(!elements.contains(nodeN4));
    assertTrue(!supportsOrphans
        || sampleH.getOrphans().containsAll(
            Arrays.asList(nodeN1, nodeN2, nodeN3)));
  }

  /**
   * check whether the hierarchy does not support orphans, or if none of the
   * orphans have parents or children
   * 
   * @return true if the hierarchy supported orphans
   */
  private static <T> boolean checkOrphans(IHierarchy<T> hierarchy) {
    Collection<T> orphans = hierarchy.getOrphans();
    if (orphans == null) {
      return false;
    }
    for (T o : orphans) {
      assertNull(hierarchy.getParent(o));
      assertTrue(hierarchy.getChildren(o).isEmpty());
    }
    return true;
  }

  /** Test result of operations on elements not actually in the hierarchy */
  public void testUselessOperations() {
    assertTrue(sampleH.removeNode("absentNode").isEmpty());
    assertNull(sampleH.getParent("alsoAbsentNode"));
    assertTrue(sampleH.getChildren("absentNode").isEmpty());
    try {
      sampleH.addChildParentRelation(null, "sampleNode");
      fail("Null must not be a valid argument for a child");
    } catch (RuntimeException e) { /* exception expected */
    }
    try {
      sampleH.addChildParentRelation("sampleNode", null);
      fail("Null must not be a valid argument for a parent");
    } catch (RuntimeException e) { /* exception expected */
    }
    try {
      assertFalse("Addition of null element to orphans should fail",
          sampleH.addOrphan(null));
    } catch (RuntimeException e) { /* exception expected */
    }
  }

  /**
   * Use {@link Hierarchies#asGraph(IHierarchy)} on a test hierarchy and compare
   * results of {@link Hierarchies#detectCycles(IHierarchy)} and
   * {@link CycleDetection#detectCycle(org.jamesii.core.util.graph.IGraph)}
   * 
   */
  public void testWrappingAndCycleDetection() {
    // new sample hierarchy
    IHierarchy<String> h = sampleH; // createSampleHierarchy();
    IGraph<String, Edge<String>> hAsGraph = Hierarchies.asGraph(h);

    Collection<Edge<String>> edges = hAsGraph.getEdges();
    SimSystem.report(Level.INFO, "Edges: " + edges);

    List<String> cycle = CycleDetection.detectCycle(hAsGraph);
    assertFalse(cycle.isEmpty());
    SimSystem.report(Level.INFO, "Cycle found by CycleDetection"
        + ".detectCycle: " + cycle);
    Collection<List<String>> cycles = Hierarchies.detectCycles(h);
    assertTrue(cycle.size() >= 2);
    SimSystem.report(Level.INFO, "Cycles found by Hierarchies"
        + ".detectCycles: " + cycles);
    for (List<String> c : cycles) {
      assertTrue(c.size() >= 2);
    }
    Collection<? extends String> cycleFoundByBoth =
        CollectionUtils.containsCollection(cycles, cycle);
    SimSystem.report(Level.INFO, "Cycle found by both: " + cycleFoundByBoth);
    assertNotNull(cycleFoundByBoth);

  }

  private boolean skipSerializationTests;

  @SuppressWarnings("unchecked")
  @Override
  public H getTestObject() {
    if (skipSerializationTests) {
      return null;
    }
    IHierarchy<String> h = getNewHierarchy();
    H hierarchy;
    try {
      hierarchy = (H) h;
      fillSampleHierarchy(hierarchy);
      return hierarchy;
    } catch (ClassCastException ex) {
      skipSerializationTests = true;
      fail(h + " does not seem to be serializable.");
      return null;
    }

  }

  @Override
  public void testSerialization() throws Exception {
    if (!skipSerializationTests) {
      super.testSerialization();
    }
  }

  @Override
  public void testSerializationXMLBean() throws Exception {
    if (!skipSerializationTests) {
      super.testSerializationXMLBean();
    }
  }

  @Override
  public void testSerializationViaRMI() throws Exception {
    if (!skipSerializationTests) {
      super.testSerializationViaRMI();
    }
  }

  @Override
  public void assertEquality(H original, H deserialisedVersion) {
    assertEquals(original.getRoots(), deserialisedVersion.getRoots());
    assertEquals(original.getOrphans(), deserialisedVersion.getOrphans());
    assertEquals(original.getAllNodes(), deserialisedVersion.getAllNodes());
    assertEquals(original.getChildToParentMap(),
        deserialisedVersion.getChildToParentMap());

  }

}
