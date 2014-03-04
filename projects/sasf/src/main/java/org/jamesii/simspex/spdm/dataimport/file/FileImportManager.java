/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.spdm.dataimport.file;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;

import org.jamesii.SimSystem;
import org.jamesii.asf.spdm.Configuration;
import org.jamesii.asf.spdm.Features;
import org.jamesii.asf.spdm.dataimport.IDMDataImportManager;
import org.jamesii.asf.spdm.dataimport.PerformanceDataSet;
import org.jamesii.asf.spdm.dataimport.PerformanceTuple;
import org.jamesii.core.base.Entity;
import org.jamesii.perfdb.recording.performance.totaltime.TotalRuntimePerfMeasurerFactory;

/**
 * Imports performance tuples from file.
 * 
 * @author Kaustav Saha
 * @author Roland Ewald
 * 
 */
@Deprecated
public class FileImportManager extends Entity implements
    IDMDataImportManager<PerformanceTuple> {

  /** Compares files by name. */
  private static final class CompareFilesByName implements Comparator<File>,
      Serializable {
    private static final long serialVersionUID = 7328114868189173389L;

    @Override
    public int compare(File file1, File file2) {
      return file1.getAbsolutePath().compareTo(file2.getAbsolutePath());
    }
  }

  /** Serialisation ID. */
  private static final long serialVersionUID = -614443103906899776L;

  /** Filter for files containing relevant data (file name has to contain this). */
  public static final String TARGET_FILE_NAME = "problem";

  /** Description of the model. */
  public static final String MODEL = "model";

  /** First element: SSA. */
  public static final String SIM = "ssa";

  /** Second element: parameter for SSA implementations. */
  public static final String SIMPARAMS = "params";

  /** Third element: event queue. */
  public static final String EQ = "eq";

  /** Fourth element: random number generator. */
  public static final String RNG = "rng";

  /** Directory containing the performance data. */
  private final File dirFile;

  /** The flag that determines if performance shall be maximised. */
  private final boolean maximisePerformance;

  /**
   * Default constructor.
   * 
   * @param directory
   *          the directory with the performance data to be read
   * @param maximisePerf
   *          flag to determine whether to maximise or to minimise
   */
  public FileImportManager(String directory, boolean maximisePerf) {
    dirFile = new File(directory);
    maximisePerformance = maximisePerf;
  }

  @Override
  public PerformanceDataSet getPerformanceData() {

    List<PerformanceTuple> performanceData = new ArrayList<>();

    if (!dirFile.isDirectory()) {
      return new PerformanceDataSet(performanceData, maximisePerformance);
    }

    SimSystem.report(Level.INFO, "Main directory for file data import: " + dirFile.getAbsolutePath());

    List<File> importableFiles = getEligibleFiles(dirFile);
    Collections.sort(importableFiles, new CompareFilesByName());

    for (File file : importableFiles) {
      performanceData.addAll(readProblemFile(file, getModelProps(file)));
    }

    SimSystem.report(Level.INFO, "Size of performance data: " + performanceData.size()
    + " tuples from " + importableFiles.size() + " files.");
    return new PerformanceDataSet(performanceData, maximisePerformance);
  }

  /**
   * Function to extract model features from file name.
   * 
   * @param file
   *          the file with the performance data
   * @return map of model properties (such as size, structure, etc.)
   */
  protected Map<String, ? extends Serializable> getModelProps(File file) {
    HashMap<String, String> modelProps = new HashMap<>();
    modelProps.put(MODEL,
        file.getName().substring(0, file.getName().indexOf('_')));
    return modelProps;
  }

  /**
   * Recursive function to retrieve all files eligible for import (determined by
   * name filtering) from a sub-directory.
   * 
   * @param dir
   *          the directory to be looked into
   * @return the list of eligible files
   */
  protected List<File> getEligibleFiles(File dir) {

    List<File> results = new ArrayList<>();

    if (!dir.isDirectory()) {
      return results;
    }

    File[] files = dir.listFiles();

    for (File file : files) {
      if (file.isDirectory()) {
        results.addAll(getEligibleFiles(file));
      } else if (file.getName().indexOf(TARGET_FILE_NAME) != -1) {
        results.add(file);
      }
    }

    return results;
  }

  /**
   * Splits a line into a list of tokens. Add empty information where two
   * separators follow each other, to mark missing information.
   * 
   * @param line
   *          the line to be split.
   * @return the list with tokens, separated by underscores ('_') or tabs
   */
  public static List<String> splitLine(String line) {
    StringTokenizer st = new StringTokenizer(line, "_\t", true);
    ArrayList<String> result = new ArrayList<>();
    boolean lastTokenDelimiter = false;
    while (st.hasMoreElements()) {
      String token = st.nextToken();
      if (token.equals("_") || token.equals("\t")) {
        if (lastTokenDelimiter) {
          // Add empty information if two separators follow each other
          result.add("");
        }
        lastTokenDelimiter = true;
      } else {
        result.add(token);
        lastTokenDelimiter = false;
      }
    }
    return result;
  }

  /**
   * Generates a performance tuple from a set of features and a list of tokens.
   * 
   * @param features
   *          the features of the model
   * @param tokens
   *          the tokens, containing the configuration of the system and its
   *          performance
   * @return a performance tuple
   */
  public static PerformanceTuple generatePerformanceTuple(Features features,
      List<String> tokens) {

    Configuration conf = new Configuration(null);
    String[] identifiers = { SIM, SIMPARAMS, EQ, RNG };
    for (int i = 0; i < identifiers.length; i++) {
      String ident = identifiers[i];
      if (tokens.get(i).isEmpty()) {
        continue;
      }
      conf.put(ident, tokens.get(i));

    }

    // Average performance is 6th element
    double avgPerf = Double.parseDouble(tokens.get(5));

    return new PerformanceTuple(features, conf,
        TotalRuntimePerfMeasurerFactory.class, avgPerf);
  }

  /**
   * Reads performance data file line-by-line.
   * 
   * @param file
   *          the file to be read
   * @param modelFeatures
   *          the model description as a map of properties
   * @return list of performance tuples
   */
  public static List<PerformanceTuple> readProblemFile(File file,
      Map<String, ? extends Serializable> modelFeatures) {

    List<PerformanceTuple> results = new ArrayList<>();

    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(file));
      String currentLine;
      while ((currentLine = br.readLine()) != null) {
        Features features = new Features();
        features.putAll(modelFeatures);
        results.add(generatePerformanceTuple(features, splitLine(currentLine)));
      }
    } catch (IOException ex) {
      SimSystem.report(Level.SEVERE,
          "Problem while reading file '" + file.getName() + "'.", ex);
    } finally {
      try {
        if (br != null) {
          br.close();
        }
      } catch (IOException e) {
        SimSystem.report(Level.SEVERE, "Could not close reader for file '"
            + file.getName() + "'.", e);
      }
    }

    return results;
  }
}
