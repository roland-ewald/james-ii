/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.serialization;

import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.core.util.eventset.SimpleEventQueueFactory;
import org.jamesii.core.util.eventset.plugintype.AbstractEventQueueFactory;
import org.jamesii.core.util.misc.SimpleSerializationTest;
import org.jamesii.perfdb.recording.selectiontrees.SelectedFactoryNode;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;


/**
 * Tests whether selection trees are serialzable.
 * 
 * @author Roland Ewald
 * 
 */
public class SelectionTreeSerialTest extends
    SimpleSerializationTest<SelectionTree> {

  @Override
  public void assertEquality(SelectionTree original,
      SelectionTree deserialisedVersion) {

  }

  @Override
  public SelectionTree getTestObject() {
    SelectionTree st = new SelectionTree(null);
    st.addVertex(new SelectedFactoryNode(
        new SelectionInformation<>(
            AbstractEventQueueFactory.class, new ParameterBlock(),
            new SimpleEventQueueFactory())));
    return st;
  }
}
