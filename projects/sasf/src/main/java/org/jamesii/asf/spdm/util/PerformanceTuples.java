/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.asf.spdm.util;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.asf.spdm.dataimport.plugintype.AbstractDMDataImporterFactory;
import org.jamesii.core.parameters.ParameterBlock;

/**
 * Some utilities for
 * {@link org.jamesii.simspex.spdm.evaluation.IPredictorGeneratorEvaluationStrategy}
 * , {@link org.jamesii.simspex.spdm.evaluation.evaluator.IPredictorEvaluator},
 * and
 * {@link org.jamesii.asf.spdm.generators.plugintype.IPerformancePredictorGenerator}
 * implementations.
 * 
 * @author Roland Ewald
 * 
 */
public final class PerformanceTuples {

  /**
   * Should not be instantiated.
   */
  private PerformanceTuples() {
  }

  /**
   * Sorts a list of {@link PerformanceTuple} with respect to their
   * {@link Features}.
   * 
   * @return a mapping: features -> perfTuples
   */
  public static <T extends PerformanceTuple> Map<Features, List<T>> sortToFeatureMap(
      List<T> perfTuples) {
    Map<Features, List<T>> featureMap = new HashMap<>();
    for (T perfTuple : perfTuples) {
      Features features = perfTuple.getFeatures();
      if (featureMap.containsKey(features)) {
        featureMap.get(features).add(perfTuple);
      } else {
        List<T> perfTupleList = new ArrayList<>();
        perfTupleList.add(perfTuple);
        featureMap.put(features, perfTupleList);
      }
    }
    return featureMap;
  }

  /**
   * Sorts a list of {@link PerformanceTuple} with respect to their
   * {@link Configuration}.
   * 
   * @return a mapping: configuration -> perfTuples
   */
  public static <T extends PerformanceTuple> Map<Configuration, List<T>> sortToConfigMap(
      List<T> perfTuples) {
    HashMap<Configuration, List<T>> configMap = new HashMap<>();
    for (T perfTuple : perfTuples) {
      Configuration config = perfTuple.getConfiguration();
      if (configMap.containsKey(config)) {
        configMap.get(config).add(perfTuple);
      } else {
        List<T> perfTupleList = new ArrayList<>();
        perfTupleList.add(perfTuple);
        configMap.put(config, perfTupleList);
      }
    }
    return configMap;
  }

  /**
   * Retrieves all performance tuples for a certain set of features.
   * 
   * @param featureMap
   *          the feature map that links features with corresponding performance
   *          tuples
   * @param featuresList
   *          the list of features of which the performance tuples should be
   *          merged into the list
   * @return the list of performance tuples belonging to these features
   */
  public static <T extends PerformanceTuple> List<T> getTuplesForFeatures(
      Map<Features, List<T>> featureMap, List<Features> featuresList) {
    List<T> perfTupleList = new ArrayList<>();
    for (Features features : featuresList) {
      if (featureMap.containsKey(features)) {
        perfTupleList.addAll(featureMap.get(features));
      }
    }
    return perfTupleList;
  }

  /**
   * Retrieves all configuration attribute names from a list of performance
   * tuples.
   * 
   * @param tuples
   *          the list of performance tuples
   * @return a set of the attribute names that are used to describe
   *         configurations
   */
  public static <T extends PerformanceTuple> Set<String> getConfigAttributes(
      List<T> tuples) {
    Set<String> configAttribs = new HashSet<>();
    for (PerformanceTuple tuple : tuples) {
      configAttribs.addAll(tuple.getConfiguration().keySet());
    }
    return configAttribs;
  }

  /**
   * Converts the {@link PerformanceTuple} instances persisted as beans in the
   * given XML source file to tab-separated performance lists per
   * {@link Configuration}.
   * 
   * @param sourceFile
   *          the source file
   * @param destFile
   *          the destination file
   * @throws Exception
   *           if an I/O problem occurs
   */
  public static void convertToConfigDataFile(
      ParameterBlock importManagerParams, String destFile) throws IOException {
    try (FileWriter fw = new FileWriter(destFile)) {
      IDMDataImportManager<PerformanceTuple> im =
          SimSystem
              .getRegistry()
              .getFactory(AbstractDMDataImporterFactory.class,
                  importManagerParams).create(importManagerParams, SimSystem.getRegistry().createContext());
      Map<Configuration, List<PerformanceTuple>> configMap =
          sortToConfigMap(im.getPerformanceData().getInstances());
      for (Entry<Configuration, List<PerformanceTuple>> config : configMap
          .entrySet()) {
        StringBuilder line = new StringBuilder(config.getKey().toString());
        line.append('\t');
        for (PerformanceTuple tuple : config.getValue()) {
          line.append(tuple.getPerformance());
          line.append('\t');
        }
        line.append('\n');
        fw.write(line.toString());
      }
    }
  }
}
