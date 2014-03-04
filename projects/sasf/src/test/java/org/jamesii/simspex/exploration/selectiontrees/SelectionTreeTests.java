/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.selectiontrees;

import org.jamesii.asf.integrationtest.bogus.application.simulator.BogusSimulatorFactoryA;
import org.jamesii.asf.integrationtest.bogus.application.simulator.ClassBasedProperties;
import org.jamesii.asf.integrationtest.bogus.application.simulator.FlexibleBogusSimulatorFactory;
import org.jamesii.asf.integrationtest.bogus.application.simulator.IBogusSimulatorProperties;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.core.util.graph.Edge;
import org.jamesii.core.util.graph.EqualsCheck;
import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Tests for {@link SelectionTree}.
 * 
 * @author Roland Ewald
 */
public class SelectionTreeTests extends SimpleSerializationTest<SelectionTree> {

  /** The properties to be handed over as parameters. */
  public final static IBogusSimulatorProperties TEST_PARAMETER_VALUE =
      new ClassBasedProperties(FlexibleBogusSimulatorFactory.class);

  /** The first test tree. */
  SelectionTree testTree1 = null;

  /** The second test tree. */
  SelectionTree testTree2 = null;

  /** The third test tree. */
  SelectionTree testTree3 = null;

  /** The fourth test tree. */
  SelectionTree testTree4 = null;

  @Override
  public void setUp() {

    testTree1 = new SelectionTree(null);
    SelectedFactoryNode sfn1 =
        new SelectedFactoryNode(new SelectionInformation<>(
            AbstractProcessorFactory.class, new ParameterBlock(),
            new BogusSimulatorFactoryA()));
    testTree1.addVertex(sfn1);
    testTree1.addEdge(new Edge<>(sfn1, testTree1.getRoot()));

    testTree2 = new SelectionTree(null);
    SelectedFactoryNode sfn2 =
        new SelectedFactoryNode(new SelectionInformation<>(
            AbstractProcessorFactory.class, new ParameterBlock(),
            new BogusSimulatorFactoryA()));
    testTree2.addVertex(sfn2);
    testTree2.addEdge(new Edge<>(sfn2, testTree2.getRoot()));

    testTree3 = new SelectionTree(null);
    SelectedFactoryNode sfn3 =
        new SelectedFactoryNode(new SelectionInformation<>(
            AbstractProcessorFactory.class, (new ParameterBlock()).addSubBl(
                FlexibleBogusSimulatorFactory.SIM_PROPERTIES,
                TEST_PARAMETER_VALUE), new FlexibleBogusSimulatorFactory()));
    testTree3.addVertex(sfn3);
    testTree3.addEdge(new Edge<>(sfn3, testTree3.getRoot()));

    testTree4 = new SelectionTree(null);
    SelectedFactoryNode sfn4 =
        new SelectedFactoryNode(new SelectionInformation<>(
            AbstractProcessorFactory.class, new ParameterBlock().addSubBl(
                "test1", 1.0).addSubBl("test2", 2.0),
            new BogusSimulatorFactoryA()));
    testTree4.addVertex(sfn4);
    testTree4.addEdge(new Edge<>(sfn4, testTree4.getRoot()));
  }

  /**
   * Generates test trees.
   * 
   * @return the test trees
   */
  public static SelectionTree[] getTestTrees() {
    SelectionTreeTests st = new SelectionTreeTests();
    st.setUp();
    return new SelectionTree[] { st.testTree1, st.testTree2, st.testTree3,
        st.testTree4 };
  }

  @Override
  public void tearDown() {
    testTree1 = null;
    testTree2 = null;
    testTree3 = null;
  }

  /**
   * Test equality.
   */
  public void testEquality() {
    assertTrue(EqualsCheck.equals(testTree1, testTree2));
    assertFalse(EqualsCheck.equals(testTree1, testTree3));
    assertFalse(EqualsCheck.equals(testTree1, testTree4));

    SelectedFactoryNode factoryNode =
        testTree1.getChildren(testTree1.getRoot()).get(0);
    SelectionInformation<?> si = factoryNode.getSelectionInformation();
    ParameterBlock pb = si.getParameter();
    pb.addSubBl("test1", 1.0).addSubBl("test2", 2.0);

    SelectionInformation<?> newSelectionInformation =
        new SelectionInformation<>(
            AbstractProcessorFactory.class, pb, new BogusSimulatorFactoryA());
    SelectedFactoryNode newFactoryNode =
        new SelectedFactoryNode(newSelectionInformation);
    testTree1.removeVertex(factoryNode);
    assertEquals(1, testTree1.getVertexCount());
    testTree1.addVertex(newFactoryNode);
    testTree1.addEdge(new Edge<>(newFactoryNode, testTree1
        .getRoot()));
    assertEquals(2, testTree1.getVertexCount());

    assertTrue(EqualsCheck.equals(testTree1, testTree4));
    assertEquals(testTree1.getHash(), testTree4.getHash());
  }

  @Override
  public void assertEquality(SelectionTree original,
      SelectionTree deserialisedVersion) {
    assertTrue(EqualsCheck.equals(original, deserialisedVersion));
  }

  @Override
  public SelectionTree getTestObject() throws Exception {
    return testTree1;
  }

  /**
   * Tests conversion to {@link ParameterBlock}.
   */
  public void testParamBlockConversion() {

    ParameterBlock pb1 = testTree1.toParamBlock();
    ParameterBlock pb2 = testTree2.toParamBlock();

    assertEquals(0, pb1.compareTo(pb2));
    assertNotNull(pb1.getSubBlock(ProcessorFactory.class.getName()));
    assertEquals(pb1.getSubBlockValue(ProcessorFactory.class.getName()),
        (new BogusSimulatorFactoryA()).getClass().getName());
  }
}
