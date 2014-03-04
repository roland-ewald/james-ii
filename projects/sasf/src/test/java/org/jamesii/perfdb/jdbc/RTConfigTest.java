/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.jdbc;

import org.jamesii.perfdb.jdbc.RuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;

/**
 * @author Roland Ewald
 * 
 */
@Deprecated
public class RTConfigTest extends PerfDBTest<RuntimeConfiguration> {

  public RTConfigTest() throws Exception {
    super();
  }

  @Override
  public void changeEntity(RuntimeConfiguration instance) {
  }

  @Override
  public void compareRetrievedEntity(RuntimeConfiguration expected,
      RuntimeConfiguration actual) {
  }

  @Override
  public RuntimeConfiguration getEntity() throws Exception {
    return new RuntimeConfiguration(new SelectionTree(null));
  }
}
