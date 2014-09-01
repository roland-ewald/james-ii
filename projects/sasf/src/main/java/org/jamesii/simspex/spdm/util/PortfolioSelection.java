/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.util;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.perfdb.IPerformanceDatabase;
import org.jamesii.perfdb.entities.IApplication;
import org.jamesii.perfdb.entities.IPerformance;
import org.jamesii.perfdb.entities.IPerformanceType;
import org.jamesii.perfdb.entities.IProblemDefinition;
import org.jamesii.perfdb.entities.IProblemScheme;
import org.jamesii.perfdb.entities.IRuntimeConfiguration;
import org.jamesii.perfdb.recording.performance.plugintype.PerformanceMeasurerFactory;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;
import org.jamesii.simspex.gui.SimSpExPerspective;
import org.jamesii.simspex.spdm.dataimport.database.DatabaseImportManager;
import org.jamesii.simspex.util.SimulationProblemDefinition;

/**
 * Helper class to support portfolio selection.
 * 
 * @author Roland Ewald
 * 
 */
public class PortfolioSelection {

  /** The minimum number of required command line arguments. */
  private static final int MIN_NUMBER_CMD_LINE_ARGS = 3;

  /** The performance database to be used. */
  private IPerformanceDatabase perfDB;

  /** The class of the performance measurer. */
  private static final Class<? extends PerformanceMeasurerFactory> PERF_MEASURE_CLASS =
      TotalRuntimePerfMeasurerFactory.class;

  /**
   * Instantiates a new portfolio selection helper object.
   */
  public PortfolioSelection() {
    perfDB = SimSpExPerspective.getPerformanceDataBase();
    perfDB.open();
  }

  /**
   * Extract portfolio data.
   * 
   * @param minConfigs
   *          the minimum number of configurations
   * @param minReps
   *          the minimum number of replications
   * @param uri
   *          the URI of the problem scheme
   */
  public void extractPortfolioData(int minConfigs, int minReps, URI uri) {

    Map<IRuntimeConfiguration, List<Double>> perfMap =
        new HashMap<>();
    IProblemScheme problemScheme = perfDB.getProblemScheme(uri);
    List<IProblemDefinition> problemDefinitions =
        perfDB.getAllProblemDefinitions(problemScheme);

    for (IProblemDefinition problemDefinition : problemDefinitions) {

      SimSystem.report(Level.INFO,
          "Processing problem:" + problemDefinition.getID());

      Map<IRuntimeConfiguration, List<IApplication>> rtMap =
          new HashMap<>();
      List<IApplication> apps = perfDB.getAllApplications(problemDefinition);

      if (apps.size() < minConfigs * minReps) {
        continue;
      }

      for (IApplication app : apps) {
        if (!rtMap.containsKey(app.getRuntimeConfiguration())) {
          rtMap.put(app.getRuntimeConfiguration(),
              new ArrayList<IApplication>());
        }
        rtMap.get(app.getRuntimeConfiguration()).add(app);
      }

      // Check if problem is OK
      if (rtMap.size() < minConfigs || !sufficientReplications(minReps, rtMap)) {
        continue;
      }

      fillPerformanceMap(minReps, perfMap, problemDefinition, rtMap);
    }

    printPerformanceEntries(perfMap);

  }

  /**
   * Checks whether there are sufficiently many replications.
   * 
   * @param minReps
   *          the minimum number of replications
   * @param rtMap
   *          the map of runtime configurations
   * @return true, if number of replications is sufficient for each runtime
   *         configuration
   */
  private boolean sufficientReplications(int minReps,
      Map<IRuntimeConfiguration, List<IApplication>> rtMap) {
    for (List<IApplication> appList : rtMap.values()) {
      if (appList.size() < minReps) {
        return false;
      }
    }
    return true;
  }

  /**
   * Fills the performance map.
   * 
   * @param minReps
   *          the minimal number of replications
   * @param perfMap
   *          the performance map to be filled
   * @param problemDefinition
   *          the problem definition
   * @param rtMap
   *          the map of runtime configurations
   */
  private void fillPerformanceMap(int minReps,
      Map<IRuntimeConfiguration, List<Double>> perfMap,
      IProblemDefinition problemDefinition,
      Map<IRuntimeConfiguration, List<IApplication>> rtMap) {

    if (perfMap.isEmpty()) {
      for (IRuntimeConfiguration rtConfig : rtMap.keySet()) {
        perfMap.put(rtConfig, new ArrayList<Double>());
      }
    }

    for (Entry<IRuntimeConfiguration, List<IApplication>> rtEntry : rtMap
        .entrySet()) {
      IRuntimeConfiguration config = rtEntry.getKey();
      if (!perfMap.containsKey(config)) {
        SimSystem.report(Level.INFO, "Config '" + config
            + "' was not executed for all problems - will be ignored.");
      }

      // Use the same minimal number of replications for problem
      // Otherwise the co-variance is meaningless
      IPerformanceType perfType = perfDB.getPerformanceType(PERF_MEASURE_CLASS);
      List<IApplication> confApps = rtMap.get(config);
      for (int i = 0; i < minReps; i++) {
        IPerformance perf = perfDB.getPerformance(confApps.get(i), perfType);
        perfMap.get(config)
            .add(
                perf.getPerformance()
                    / SimulationProblemDefinition
                        .getSimStopTime(problemDefinition));
      }
    }
  }

  /**
   * Prints the performance entries.
   * 
   * @param perfMap
   *          the performance map
   */
  private void printPerformanceEntries(
      Map<IRuntimeConfiguration, List<Double>> perfMap) {
    for (Entry<IRuntimeConfiguration, List<Double>> perfEntry : perfMap
        .entrySet()) {
      StringBuilder perfEntryDesc =
          new StringBuilder(DatabaseImportManager.createConfiguration(
              perfEntry.getKey()).toString());
      perfEntryDesc.append('\t');
      for (Double d : perfEntry.getValue()) {
        perfEntryDesc.append(d);
        perfEntryDesc.append('\t');
      }
      perfEntryDesc.append("\n");
      SimSystem.report(Level.INFO, perfEntryDesc.toString());
    }
  }

  /**
   * The main method.
   * 
   * @param args
   *          the arguments
   */
  public static void main(String[] args) {
    if (args.length < MIN_NUMBER_CMD_LINE_ARGS) {
      SimSystem
          .report(
              Level.SEVERE,
              "Three arguments needed: [min # configurations per problem def], [min # replications per configuration], [URI of problem scheme]");
    }
    try {
      PortfolioSelection ps = new PortfolioSelection();
      ps.extractPortfolioData(Integer.parseInt(args[0]),
          Integer.parseInt(args[1]), new URI(args[2]));
    } catch (URISyntaxException ex) {
      SimSystem.report(Level.INFO, "Portfolio selection failed.", ex);
    }
  }
}
