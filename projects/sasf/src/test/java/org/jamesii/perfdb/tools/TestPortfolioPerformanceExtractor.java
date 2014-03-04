/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.perfdb.tools;

import junit.framework.TestCase;

import org.jamesii.asf.portfolios.plugintype.PortfolioPerformanceData;
import org.jamesii.core.algoselect.SelectionInformation;
import org.jamesii.core.data.DBConnectionData;
import org.jamesii.core.util.misc.Files;
import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.gui.SimSpExPerspective;

/**
 * Some simple tests for the {@link PortfolioPerformanceExtractor}.
 * 
 * @author Roland Ewald
 * 
 */
public class TestPortfolioPerformanceExtractor extends TestCase {

  /** The instance of the portfolio performance extractor to be tested. */
  PortfolioPerformanceExtractor ppe;

  @Override
  public void setUp() {
    ppe =
        new PortfolioPerformanceExtractor("*", null,
            TotalRuntimePerfMeasurerFactory.class);
  }

  /**
   * Test vector extension.
   */
  public void testVectorExtension() {

    ppe.incrementPerformanceVectorLength();
    assertEquals(0, ppe.getPerformanceVectors().size());

    ppe.getPerformanceVectors().add(new Double[] { 1. });
    ppe.incrementPerformanceVectorLength();
    assertEquals(1, ppe.getPerformanceVectors().size());
    assertEquals(2, ppe.getPerformanceVectors().get(0).length);
    assertEquals(1., ppe.getPerformanceVectors().get(0)[0]);
    assertNull(ppe.getPerformanceVectors().get(0)[1]);
  }

  /**
   * Usage example for portfolio performance data extractor.
   * 
   * TODO make unit test out of this
   * 
   * @param args
   *          command line arguments (first argument is the portfolio
   *          performance data)
   */
  public static void main(String[] args) {

    new SelectionInformation<>(null, null, null);
    SimSpExPerspective.setDbConnectionData(new DBConnectionData(
        "jdbc:mysql://localhost/exp_sr", "root", "root",
        "com.mysql.jdbc.Driver"));

    PortfolioPerformanceExtractor portExtract =
        new PortfolioPerformanceExtractor("CyclicChain",
            SimSpExPerspective.getPerformanceDataBase(),
            TotalRuntimePerfMeasurerFactory.class);

    try {
      PortfolioPerformanceData portPerfData = portExtract.extract();
      System.out
          .println(Strings.displayMatrix(portPerfData.performances, '\t'));
      for (SelectionTree config : portPerfData.configurations) {
        System.out.println();
        System.out.println(Strings.dispIterable(config.getUniqueFactories()));
      }

      String fileName = args[0];
      Files.save(portPerfData, fileName);
      PortfolioPerformanceData desPerfData =
          (PortfolioPerformanceData) Files.load(fileName);
      System.out.println(Strings.displayMatrix(desPerfData.performances, '\t'));

      for (SelectionTree config : desPerfData.configurations) {
        System.out.println();
        System.out.println(Strings.dispIterable(config.getUniqueFactories()));
      }

    } catch (Throwable t) {
      t.printStackTrace();
    }

  }
}
