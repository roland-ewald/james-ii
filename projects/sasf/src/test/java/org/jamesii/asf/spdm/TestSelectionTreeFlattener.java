/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm;


import java.util.HashMap;
import java.util.Map;

import org.jamesii.asf.integrationtest.bogus.application.simulator.BogusSimulatorFactoryA;
import org.jamesii.asf.integrationtest.bogus.application.simulator.FlexibleBogusSimulatorFactory;
import org.jamesii.asf.spdm.SelectionTreeFlattener;
import org.jamesii.core.processor.plugintype.ProcessorFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.exploration.selectiontrees.SelectionTreeTests;

import junit.framework.TestCase;

/**
 * Test for {@link SelectionTreeFlattener}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestSelectionTreeFlattener extends TestCase {

  /** The selection trees for testing. */
  SelectionTree[] selectionTrees;

  /** The result. */
  Map<String, Object> result;

  /** The selection tree flattener. */
  SelectionTreeFlattener stf;

  @Override
  public void setUp() {
    selectionTrees = SelectionTreeTests.getTestTrees();
    result = new HashMap<>();
  }

  /**
   * Test simple cases of selection tree flattening.
   */
  public void testSelectionTreeFlattening() {
    stf = new SelectionTreeFlattener(selectionTrees[0], result);
    stf.flatten();
    assertTrue(result.containsValue(BogusSimulatorFactoryA.class.getName()));
    assertTrue(result.containsKey(stf.getPathSeparator() + "null"
        + stf.getPathSeparator() + ProcessorFactory.class.getName()));
    assertEquals(1, result.size());
  }

  /**
   * Test parameter handling for tree flattening.
   */
  public void testSelectionTreeFlatteningWithParameters() {
    stf = new SelectionTreeFlattener(selectionTrees[2], result);
    stf.flatten();
    assertTrue(result.containsValue(FlexibleBogusSimulatorFactory.class.getName()));
    assertTrue(result.containsValue(SelectionTreeTests.TEST_PARAMETER_VALUE));
    assertTrue(result.containsKey(stf.getPathSeparator() + "null"
        + stf.getPathSeparator() + ProcessorFactory.class.getName()));
    assertTrue(result.containsKey(stf.getPathSeparator() + "null"
        + stf.getPathSeparator() + ProcessorFactory.class.getName()
        + SelectionTreeFlattener.PARAMETER_SEPARATOR
        + FlexibleBogusSimulatorFactory.SIM_PROPERTIES));
    assertEquals(2, result.size());
  }
}