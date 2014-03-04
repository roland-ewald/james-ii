/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport;

import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.core.parameters.ParameterBlock;
import org.jamesii.simspex.spdm.dataimport.xml.XMLImportManager;
import org.jamesii.simspex.spdm.dataimport.xml.XMLImportManagerFactory;

import junit.framework.TestCase;

/**
 * Test for the {@link XMLImportManager}.
 * 
 * @author Roland Ewald
 */
public class XMLImportManagerTest extends TestCase {

  /** The import manager. */
  IDMDataImportManager manager;

  @Override
  public void setUp() {
    manager =
        (new XMLImportManagerFactory()).create(new ParameterBlock(
            "./testdata/test.xml", XMLImportManagerFactory.RESULT_FILE));
  }

  public void testImport() {
    PerformanceDataSet perfData = manager.getPerformanceData();
    assertNotNull(perfData);
    assertNotNull(perfData.getInstances());
    assertNotNull(perfData.getMetaData());
    assertEquals(11, perfData.getInstances().size());
  }

}
