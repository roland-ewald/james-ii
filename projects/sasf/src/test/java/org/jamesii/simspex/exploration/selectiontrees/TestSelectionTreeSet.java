/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.selectiontrees;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jamesii.asf.integrationtest.bogus.application.model.BogusModel;
import org.jamesii.core.distributed.partitioner.partitioning.AbstractExecutablePartition;
import org.jamesii.core.factories.Factory;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.plugins.IParameter;
import org.jamesii.core.processor.plugintype.AbstractProcessorFactory;
import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.perfdb.recording.selectiontrees.FactoryVertex;
import org.jamesii.perfdb.recording.selectiontrees.IParameterBlockAugmenter;
import org.jamesii.perfdb.recording.selectiontrees.SelTreeSetVertex;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSet;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTreeSetDefinition;

/**
 * Tests for the selection tree set implementation.
 * 
 * @author Roland Ewald
 * 
 */
public class TestSelectionTreeSet extends
    SimpleSerializationTest<SelectionTreeSet> {

  /** Selection tree set to be tested. */
  SelectionTreeSet treeSet = null;

  /** Parameter block for the creation of the {@link SelectionTreeSet}. */
  ParameterBlock paramBlock;

  @Override
  public void setUp() throws Exception {
    super.setUp();
    paramBlock = new ParameterBlock();
    paramBlock.addSubBl(AbstractProcessorFactory.PARTITION,
        new AbstractExecutablePartition(new BogusModel(
            new HashMap<String, Serializable>()), null, null));
    treeSet = new SelectionTreeSet(AbstractProcessorFactory.class, paramBlock);
  }

  /**
   * Test counting and parameter block generation.
   */
  public void testCountingAndParamBlockGeneration() {
    treeSet.generateFactoryCombinations();
    List<ParameterBlock> combinations = treeSet.getFactoryCombinations();
    assertEquals(
        "Combinatorial calculation yields the same result as actual parameter block combination",
        treeSet.calculateFactoryCombinations(), combinations.size());
    assertEquals("Each parameter block is unique (unequal to all others)",
        combinations.size(), new HashSet<>(combinations).size());
    System.out.println("# Combinations:" + combinations.size());
  }

  /**
   * Test constrained counting and parameter block generation.
   */
  public void testConstrainedCountingAndParamBlockGeneration() {
    SelectionTreeSetDefinition tree = treeSet.getTree();
    List<SelTreeSetVertex> children = tree.getChildren(tree.getRoot());
    for (SelTreeSetVertex vertex : children) {
      if (vertex instanceof FactoryVertex<?>) {
        Set<? extends Factory<?>> factories =
            ((FactoryVertex<?>) vertex).getFactories();
        if (factories.size() > 0) {
          ((FactoryVertex<?>) vertex).getConstraints().ignore(
              factories.iterator().next());
          break;
        }
      }
    }

    treeSet.generateFactoryCombinations();
    List<ParameterBlock> combinations = treeSet.getFactoryCombinations();
    assertEquals(treeSet.calculateFactoryCombinations(), combinations.size());
    assertEquals(combinations.size(), new HashSet<>(combinations).size());
    System.out.println("# Combinations:" + combinations.size());
  }

  public void testParamBlockAugmentation() throws ClassNotFoundException {
    final List<ParameterBlock> augmentedBlocks = new ArrayList<>();
    new SelectionTreeSet(AbstractProcessorFactory.class, paramBlock,
        new IParameterBlockAugmenter() {
          @Override
          public ParameterBlock augment(ParameterBlock originalParamBlock,
              SelectionTreeSet selectionTreeSet, SelTreeSetVertex currentNode,
              IParameter topLevelParameter) {
            originalParamBlock.addSubBl("testBlock", 42);
            augmentedBlocks.add(originalParamBlock);
            return originalParamBlock;
          }
        });
    assertTrue(augmentedBlocks.size() > 0);
  }

  @Override
  public void assertEquality(SelectionTreeSet original,
      SelectionTreeSet deserialisedVersion) {
    assertEquals(original.calculateFactoryCombinations(),
        deserialisedVersion.calculateFactoryCombinations());
  }

  @Override
  public SelectionTreeSet getTestObject() throws Exception {
    return new SelectionTreeSet(AbstractProcessorFactory.class, paramBlock);
  }

}
