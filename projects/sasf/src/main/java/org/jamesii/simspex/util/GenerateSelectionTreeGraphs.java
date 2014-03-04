/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.util;


import java.util.List;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.core.util.graph.GraphUtility;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.selectiontrees.SelectionTree;
import org.jamesii.simspex.gui.SimSpExPerspective;


/**
 * Small helper to generate DOT files out of all selection trees of runtime
 * configurations from the database.
 * 
 * @author Roland Ewald
 * 
 */
public final class GenerateSelectionTreeGraphs {

  /**
   * Should not be instantiated.
   */
  private GenerateSelectionTreeGraphs() {
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments (unused)
   */
  public static void main(String[] args) {

    try {
      IPerformanceDatabase perfDB = SimSpExPerspective.getPerformanceDataBase();
      perfDB.open();

      List<IProblemDefinition> problems = perfDB.getAllProblemDefinitions();
      List<IRuntimeConfiguration> configs = null;
      SimSystem.report(Level.INFO, "#problems:" + problems.size());
      for (IProblemDefinition problem : problems) {
        if (configs == null) {
          configs = perfDB.getAllRuntimeConfigs(problem);
        } else {
          configs.addAll(perfDB.getAllRuntimeConfigs(problem));
        }
      }

      SimSystem.report(Level.INFO, "#configs:" + configs.size());
      int counter = 0;
      for (IRuntimeConfiguration config : configs) {
        SelectionTree tree = config.getSelectionTree();
        GraphUtility.saveGraphToDOT("seltree" + counter + "_" + tree.getHash()
            + ".dot", tree, "shape=rectangle, style=filled, height=.1");
        counter++;
      }

    } catch (Exception ex) {
      SimSystem.report(Level.SEVERE,
          "Could not generate selection tree graphs.", ex);
    }

  }
}
