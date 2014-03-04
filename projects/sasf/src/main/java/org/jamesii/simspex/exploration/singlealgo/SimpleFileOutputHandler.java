/*
 * The general modelling and simulation framework JAMES II.
 * Copyright by the University of Rostock.
 * 
 * LICENCE: JAMESLIC
 */
package org.jamesii.simspex.exploration.singlealgo;


import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import org.jamesii.core.util.misc.Strings;
import org.jamesii.perfdb.entities.IProblemDefinition;

/**
 * Simple handler the writes the analysis results of the
 * {@link AlgorithmChangeEvaluator} to a file.
 * 
 * @author Roland Ewald
 * 
 */
public class SimpleFileOutputHandler implements IOutputACEHandler {

  /** The writer. */
  private final BufferedWriter writer;

  /**
   * Flag that determines if the first row is yet to be written (should contain
   * a HEADER).
   */
  private boolean firstRow = true;

  /** The fields of the HEADER. */
  private static final String[] HEADER = { "Benchmark Model URI", "Parameters",
      "Old Comparison Performance", "Comparison Performance",
      "Comparison Config ID", "Comparison Config Description",
      "Best New Performance", "Best New Config ID",
      "Best New Config Description", "Best Old Performance",
      "Best Old Config ID", "Best Old Config Description",
      "Worst New Config Performance", "Worst New Config ID",
      "Worst New Config Description", "Worst Old Config Performance",
      "Worst Old Config ID", "Worst Old Config Description" };

  /**
   * The IDs that correspond to configurations containing the changed algorithm.
   */
  private Set<Long> configsAlgo;

  /**
   * Instantiates a new simple file output handler.
   * 
   * @param fileName
   *          the file name
   * @throws IOException
   *           Signals that an I/O exception has occurred.
   */
  public SimpleFileOutputHandler(String fileName) throws IOException {
    writer = new BufferedWriter(new FileWriter(fileName));
  }

  @Override
  public void init(Set<Long> configsWithAlgo) {
    configsAlgo = configsWithAlgo;
  }

  @Override
  public void output(IProblemDefinition simProblem, ComparisonJob job)
      throws IOException {

    StringBuilder sb = new StringBuilder();
    if (firstRow) {
      for (String head : HEADER) {
        sb.append(head + '\t');
      }
      sb.append('\n');
      firstRow = false;
    }

    String uri = simProblem.getProblemScheme().getUri().toString();
    Map<String, Serializable> paramMap = simProblem.getSchemeParameters();
    String params =
        Strings.dispMap(paramMap, new ArrayList<>(paramMap.keySet()),
            "=", ";", true);

    // Trigger analysis
    ComparisonResult result = job.getComparisonResult();
    SimpleStatistics stats = result.analyze(configsAlgo);

    // Write data to file
    String[] dataToWrite =
        { uri, params, stats.getOldComparedConfig().getA().toString(),
            stats.getComparedConfig().getA().toString(),
            stats.getComparedConfig().getB().toString(),
            stats.getComparedConfig().getC(),
            stats.getBestNew().getA().toString(),
            stats.getBestNew().getB().toString(), stats.getBestNew().getC(),
            stats.getBestOld().getA().toString(),
            stats.getBestOld().getB().toString(), stats.getBestOld().getC(),
            stats.getWorstNew().getA().toString(),
            stats.getWorstNew().getB().toString(), stats.getWorstNew().getC(),
            stats.getWorstOld().getA().toString(),
            stats.getWorstOld().getB().toString(), stats.getWorstOld().getC() };
    for (String data : dataToWrite) {
      sb.append(data + '\t');
    }
    sb.append('\n');
    writer.append(sb.toString());
    writer.flush();
  }

  @Override
  public void finish() throws IOException {
    writer.close();
  }

}
